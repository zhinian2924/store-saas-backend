package com.example.storesaas.product;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.CommonStatus;
import com.example.storesaas.common.constants.DeleteStatus;
import com.example.storesaas.common.constants.ProductStatus;
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

    /**
     * 获取商品分类列表
     * @return 商品分类列表
     */
    public List<ProductCategory> categories() {
        Long tenantId = AuthContext.tenantId();
        return categoryMapper.selectList(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getTenantId, tenantId)
                .eq(ProductCategory::getDeleted, DeleteStatus.NOT_DELETED)
                .orderByAsc(ProductCategory::getSortNo));
    }

    /**
     * 创建分类
     * @param request 分类请求
     * @return 商品分类
     */
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

    /**
     * 获取商品列表
     * @return 商品列表
     */
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
        product.setStock(request.stock() == null ? 0 : request.stock());
        product.setStatus(normalizeProductStatus(request.status(), product.getStock()));
        fillCreate(product);
        productMapper.insert(product);
        return product;
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = tenantProduct(AuthContext.tenantId(), id);
        product.setCategoryId(request.categoryId());
        product.setName(request.name());
        product.setImageUrl(request.imageUrl());
        product.setPrice(request.price());
        product.setStatus(normalizeProductStatus(request.status(), product.getStock()));
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        return product;
    }

    public Product setProductStatus(Long id, Integer status) {
        Product product = tenantProduct(AuthContext.tenantId(), id);
        product.setStatus(normalizeProductStatus(status, product.getStock()));
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
        return product;
    }

    public ProductCategory setCategoryStatus(Long id, Integer status) {
        Long tenantId = AuthContext.tenantId();
        ProductCategory category = categoryMapper.selectOne(new LambdaQueryWrapper<ProductCategory>()
                .eq(ProductCategory::getTenantId, tenantId)
                .eq(ProductCategory::getId, id)
                .eq(ProductCategory::getDeleted, DeleteStatus.NOT_DELETED));
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        int nextStatus = Integer.valueOf(CommonStatus.DISABLED).equals(status) ? CommonStatus.DISABLED : CommonStatus.ENABLED;
        category.setStatus(nextStatus);
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
        if (nextStatus == CommonStatus.DISABLED) {
            productMapper.stopByCategory(tenantId, id);
        }
        return category;
    }

    public void deleteProduct(Long id) {
        Product product = tenantProduct(AuthContext.tenantId(), id);
        product.setDeleted(DeleteStatus.DELETED);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateById(product);
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

    private int normalizeProductStatus(Integer status, Integer stock) {
        int nextStatus = status == null ? ProductStatus.ON_SALE : status;
        if (!ProductStatus.valid(nextStatus)) {
            throw new BusinessException("商品状态不支持");
        }
        if (nextStatus == ProductStatus.ON_SALE && (stock == null || stock <= 0)) {
            return ProductStatus.SOLD_OUT;
        }
        return nextStatus;
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
