package com.example.storesaas.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.CommonStatus;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.product.dto.CategoryRequest;
import com.example.storesaas.product.dto.ProductRequest;
import com.example.storesaas.product.entity.Product;
import com.example.storesaas.product.entity.ProductCategory;
import com.example.storesaas.product.mapper.ProductCategoryMapper;
import com.example.storesaas.product.mapper.ProductMapper;
import com.example.storesaas.security.AuthContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    private final ProductCategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    public ProductService(ProductCategoryMapper categoryMapper, ProductMapper productMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
    }

    public List<ProductCategory> categories() {
        Long tenantId = AuthContext.tenantId();
        return categoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getTenantId, tenantId)
                .eq(ProductCategory::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByAsc(ProductCategory::getSortNo));
    }

    public ProductCategory createCategory(CategoryRequest request) {
        ProductCategory category = new ProductCategory();
        category.setTenantId(AuthContext.tenantId());
        category.setName(request.name());
        category.setSortNo(request.sortNo() == null ? 0 : request.sortNo());
        category.setStatus(request.status() == null ? CommonStatus.ENABLED : request.status());
        fillCreate(category);
        categoryMapper.insert(category);
        return category;
    }

    public List<Product> products() {
        Long tenantId = AuthContext.tenantId();
        return productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(Product::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByDesc(Product::getId));
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        product.setTenantId(AuthContext.tenantId());
        product.setCategoryId(request.categoryId());
        product.setName(request.name());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setStatus(request.status() == null ? CommonStatus.ENABLED : request.status());
        fillCreate(product);
        productMapper.insert(product);
        return product;
    }

    public Product tenantProduct(Long tenantId, Long productId) {
        Product product = productMapper.selectOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getTenantId, tenantId)
                .eq(Product::getId, productId)
                .eq(Product::getDeleted, DeleteStatus.NOT_DELETED));
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return product;
    }

    private void fillCreate(Object entity) {
        LocalDateTime now = LocalDateTime.now();
        if (entity instanceof ProductCategory category) {
            category.setCreatedAt(now);
            category.setUpdatedAt(now);
            category.setDeleted(DeleteStatus.NOT_DELETED);
        }
        if (entity instanceof Product product) {
            product.setCreatedAt(now);
            product.setUpdatedAt(now);
            product.setDeleted(DeleteStatus.NOT_DELETED);
        }
    }
}
