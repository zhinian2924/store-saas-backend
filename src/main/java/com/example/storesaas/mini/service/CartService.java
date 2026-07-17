package com.example.storesaas.mini.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.mini.CustomerContext;
import com.example.storesaas.mini.dto.CartItemDTO;
import com.example.storesaas.mini.entity.CartItem;
import com.example.storesaas.mini.mapper.CartItemMapper;
import com.example.storesaas.mini.vo.CartItemVO;
import com.example.storesaas.product.ProductService;
import com.example.storesaas.product.entity.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {
    private final CartItemMapper mapper;
    private final ProductService products;

    public CartService(CartItemMapper mapper, ProductService products) {
        this.mapper = mapper;
        this.products = products;
    }

    public List<CartItemVO> list() {
        return mapper.selectList(query()).stream().map(CartItemVO::from).toList();
    }

    @Transactional
    public CartItemVO add(Long productId, CartItemDTO request) {
        Long tenantId = CustomerContext.tenantId();
        Product product = products.tenantProduct(tenantId, productId);
        if (product.getStatus() == null || product.getStatus() != 1) throw new BusinessException("商品当前不可购买");
        CartItem item = mapper.selectOne(query().eq(CartItem::getProductId, productId));
        if (item == null) {
            item = new CartItem();
            item.setTenantId(tenantId);
            item.setCustomerId(CustomerContext.customerId());
            item.setProductId(productId);
            item.setQuantity(request.quantity());
            item.setPrice(product.getPrice());
            fill(item);
            mapper.insert(item);
        } else {
            item.setQuantity(item.getQuantity() + request.quantity());
            item.setPrice(product.getPrice());
            item.setUpdatedAt(LocalDateTime.now());
            mapper.updateById(item);
        }
        return CartItemVO.from(item);
    }

    @Transactional
    public CartItemVO update(Long productId, CartItemDTO request) {
        CartItem item = owned(productId);
        item.setQuantity(request.quantity());
        item.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(item);
        return CartItemVO.from(item);
    }

    @Transactional
    public void remove(Long productId) {
        CartItem item = owned(productId);
        item.setDeleted(DeleteStatus.DELETED);
        item.setUpdatedAt(LocalDateTime.now());
        mapper.updateById(item);
    }

    private CartItem owned(Long productId) {
        CartItem item = mapper.selectOne(query().eq(CartItem::getProductId, productId));
        if (item == null) throw new BusinessException("购物车商品不存在");
        return item;
    }

    private LambdaQueryWrapper<CartItem> query() {
        return new LambdaQueryWrapper<CartItem>().eq(CartItem::getTenantId, CustomerContext.tenantId()).eq(CartItem::getCustomerId, CustomerContext.customerId()).eq(CartItem::getDeleted, DeleteStatus.NOT_DELETED);
    }

    private void fill(CartItem item) {
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        item.setDeleted(DeleteStatus.NOT_DELETED);
    }
}
