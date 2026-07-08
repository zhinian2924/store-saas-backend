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

    @Update("""
            update biz_product
            set status = case
                    when stock <= 0 and status = 1 then 2
                    when stock > 0 and status = 2 then 1
                    else status
                end,
                updated_at = now()
            where id = #{productId}
              and tenant_id = #{tenantId}
              and deleted = 0
            """)
    int syncStatusByStock(@Param("tenantId") Long tenantId, @Param("productId") Long productId);

    @Update("""
            update biz_product
            set status = 3, updated_at = now()
            where tenant_id = #{tenantId}
              and category_id = #{categoryId}
              and deleted = 0
            """)
    int stopByCategory(@Param("tenantId") Long tenantId, @Param("categoryId") Long categoryId);
}
