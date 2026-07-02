package com.example.storesaas.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.storesaas.product.entity.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ProductMapper extends BaseMapper<Product> {
    @Update("""
            update biz_product
            set stock = stock - #{quantity}, updated_at = now()
            where id = #{productId}
              and tenant_id = #{tenantId}
              and stock >= #{quantity}
              and deleted = 0
            """)
    int deductStock(@Param("tenantId") Long tenantId, @Param("productId") Long productId, @Param("quantity") Integer quantity);
}
