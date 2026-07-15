create table if not exists biz_cart_item (
    id bigint primary key auto_increment comment '主键ID',
    tenant_id bigint not null comment '租户ID',
    customer_id bigint not null comment '消费者ID',
    product_id bigint not null comment '商品ID',
    quantity int not null comment '数量',
    price decimal(10,2) comment '加入时单价',
    created_at datetime not null comment '创建时间',
    updated_at datetime not null comment '更新时间',
    deleted tinyint not null default 0 comment '逻辑删除标记 0-未删除 1-已删除',
    unique key uk_cart_customer_product(customer_id,tenant_id,product_id) comment '消费者租户商品唯一',
    key idx_cart_customer_tenant(customer_id,tenant_id) comment '消费者租户联合索引'
) comment '消费者购物车';
