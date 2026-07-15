create table if not exists biz_customer_store (
    id bigint primary key auto_increment comment '主键ID',
    customer_id bigint not null comment '消费者ID',
    tenant_id bigint not null comment '租户ID',
    store_id bigint not null comment '门店ID',
    created_at datetime not null comment '创建时间',
    updated_at datetime not null comment '更新时间',
    deleted tinyint not null default 0 comment '逻辑删除标记 0-未删除 1-已删除',
    unique key uk_customer_store (customer_id, tenant_id) comment '消费者租户下唯一',
    key idx_customer_store_tenant (tenant_id) comment '租户索引'
    ) comment '消费者门店关系';