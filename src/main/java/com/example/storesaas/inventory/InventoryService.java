package com.example.storesaas.inventory;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.InventoryFlowType;
import com.example.storesaas.inventory.dto.StockAdjustRequest;
import com.example.storesaas.inventory.entity.InventoryFlow;
import com.example.storesaas.inventory.mapper.InventoryFlowMapper;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.mapper.ProductMapper;
import com.example.storesaas.security.AuthContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {
    private final ProductMapper productMapper;
    private final InventoryFlowMapper flowMapper;

    public InventoryService(ProductMapper productMapper, InventoryFlowMapper flowMapper) {
        this.productMapper = productMapper;
        this.flowMapper = flowMapper;
    }

    @Transactional
    public InventoryFlow adjust(StockAdjustRequest request) {
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

        return createFlow(tenantId, product.getId(), request.flowType(), delta, before, after, request.remark());
    }

    public InventoryFlow createFlow(Long tenantId, Long productId, String flowType, Integer quantity, Integer before, Integer after, String remark) {
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

    public List<InventoryFlow> flows() {
        Long tenantId = AuthContext.tenantId();
        return flowMapper.selectList(new LambdaQueryWrapper<InventoryFlow>()
                .eq(InventoryFlow::getTenantId, tenantId)
                .eq(InventoryFlow::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByDesc(InventoryFlow::getId));
    }
}
