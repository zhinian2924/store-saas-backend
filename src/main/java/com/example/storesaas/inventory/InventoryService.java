package com.example.storesaas.inventory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.PageResult;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.InventoryFlowType;
import com.example.storesaas.inventory.dto.StockAdjustDTO;
import com.example.storesaas.inventory.entity.InventoryFlow;
import com.example.storesaas.inventory.mapper.InventoryFlowMapper;
import com.example.storesaas.inventory.vo.InventoryFlowVO;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.mapper.ProductMapper;
import com.example.storesaas.security.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private final ProductMapper productMapper;
    private final InventoryFlowMapper flowMapper;

    public InventoryService(ProductMapper productMapper, InventoryFlowMapper flowMapper) {
        this.productMapper = productMapper;
        this.flowMapper = flowMapper;
    }

    @Transactional
    public InventoryFlowVO adjust(StockAdjustDTO request) {
        Long tenantId = AuthContext.tenantId();
        Product product = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(Product::getId, request.productId())
                .eq(Product::getDeleted, DeleteStatus.NOT_DELETED));
        if (product == null) {
            throw new BusinessException("商品不存在");
        }

        int delta = switch (request.flowType()) {
            case InventoryFlowType.PURCHASE_IN, InventoryFlowType.CHECK_GAIN -> request.quantity();
            case InventoryFlowType.DAMAGE_OUT, InventoryFlowType.CHECK_LOSS -> -request.quantity();
            default -> throw new BusinessException("库存变更类型不支持");
        };
        int before = product.getStock();
        int after = before + delta;
        if (after < 0) {
            throw new BusinessException("库存不足");
        }
        product.setStock(after);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        productMapper.syncStatusByStock(tenantId, product.getId());

        return InventoryFlowVO.from(createFlow(tenantId, product.getId(), request.flowType(), delta, before, after, request.remark()));
    }

    /**
     * 创建库存变更记录
     * @param tenantId
     * @param productId
     * @param flowType
     * @param quantity
     * @param before
     * @param after
     * @param remark
     * @return
     */
    public InventoryFlow createFlow(Long tenantId, Long productId, String flowType, Integer quantity,
                                    Integer before, Integer after, String remark) {
        InventoryFlow flow = new InventoryFlow();
        flow.setTenantId(tenantId);
        flow.setProductId(productId);
        flow.setFlowType(flowType);
        flow.setQuantity(quantity);
        flow.setBeforeStock(before);
        flow.setAfterStock(after);
        flow.setRemark(remark);
        flow.setCreatedAt(LocalDateTime.now());
        flow.setUpdatedAt(LocalDateTime.now());
        flow.setDeleted(DeleteStatus.NOT_DELETED);
        flowMapper.insert(flow);
        return flow;
    }

    /**
     * 分页获取库存变更记录
     * @param page 页码（从 1 开始）
     * @param size 每页条数
     * @return 分页库存变更记录
     */
    public PageResult<InventoryFlowVO> flows(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.min(Math.max(size, 1), 100);
        Long tenantId = AuthContext.tenantId();
        Page<InventoryFlow> flowPage = flowMapper.selectPage(
                new Page<>(safePage, safeSize),
                new LambdaQueryWrapper<InventoryFlow>()
                        .eq(InventoryFlow::getTenantId, tenantId)
                        .eq(InventoryFlow::getDeleted, DeleteStatus.NOT_DELETED)
                        .orderByDesc(InventoryFlow::getId));
        List<InventoryFlow> records = flowPage.getRecords();
        Map<Long, String> productNames = loadProductNames(records);
        List<InventoryFlowVO> vos = records.stream()
                .map(flow -> InventoryFlowVO.from(flow, productNames.get(flow.getProductId())))
                .toList();
        return PageResult.of(flowPage, vos);
    }

    private Map<Long, String> loadProductNames(List<InventoryFlow> records) {
        if (records.isEmpty()) {
            return Map.of();
        }
        List<Long> productIds = records.stream().map(InventoryFlow::getProductId).distinct().toList();
        return productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));
    }
}
