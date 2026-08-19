package com.example.storesaas.inventory;

import com.example.storesaas.catalog.api.ProductReader;
import com.example.storesaas.catalog.api.ProductSnapshot;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.InventoryFlowType;
import com.example.storesaas.common.constants.ProductStatus;
import com.example.storesaas.inventory.api.InventoryReservation;
import com.example.storesaas.inventory.mapper.InventoryFlowMapper;
import com.example.storesaas.catalog.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 当前阶段的库存边界实现：支付时立即完成扣减并写入库存流水。
 * 待库存预占表落地后，再将 commit/release 替换为完整生命周期。
 */
@Service
public class InventoryReservationService implements InventoryReservation {
    private final ProductReader productReader;
    private final ProductMapper productMapper;
    private final InventoryService inventoryService;

    public InventoryReservationService(ProductReader productReader,
                                       ProductMapper productMapper,
                                       InventoryService inventoryService) {
        this.productReader = productReader;
        this.productMapper = productMapper;
        this.inventoryService = inventoryService;
    }

    @Override
    @Transactional
    public void reserve(Long tenantId, Long orderId, List<ReservationItem> items) {
        for (ReservationItem item : items) {
            ProductSnapshot before = productReader.getTenantProduct(tenantId, item.productId());
            if (before.status() == null || before.status() != ProductStatus.ON_SALE) {
                throw new BusinessException(before.name() + "当前不可销售");
            }
            int affected = productMapper.deductStock(tenantId, item.productId(), item.quantity());
            if (affected != 1) {
                throw new BusinessException(before.name() + "库存不足");
            }
            productMapper.syncStatusByStock(tenantId, item.productId());
            ProductSnapshot after = productReader.getTenantProduct(tenantId, item.productId());
            inventoryService.createFlow(
                    tenantId,
                    item.productId(),
                    InventoryFlowType.ORDER_OUT,
                    -item.quantity(),
                    before.stock(),
                    after.stock(),
                    "订单支付扣减:" + orderId
            );
        }
    }

    @Override
    public void commit(Long tenantId, Long orderId) {
        // 当前实现 reserve 已在支付事务内完成扣减，暂不需要二次确认。
    }

    @Override
    public void release(Long tenantId, Long orderId) {
        // 完整释放流程依赖库存预占表，待后续数据库迁移后实现。
    }
}
