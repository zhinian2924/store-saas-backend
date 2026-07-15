-- 订单明细表
create table if not exists biz_order_item
(
    id           bigint primary key auto_increment comment '主键ID',
    tenant_id    bigint         not null comment '租户ID',
    order_id     bigint         not null comment '订单ID',
    product_id   bigint         not null comment '商品ID',
    product_name varchar(128)   not null comment '商品名称（快照）',
    image_url    varchar(255) comment '商品图片',
    price        decimal(10, 2) not null comment '单价',
    quantity     int            not null comment '数量',
    amount       decimal(10, 2) not null comment '小计金额',
    created_at   datetime       not null comment '创建时间',
    updated_at   datetime       not null comment '更新时间',
    deleted      tinyint        not null default 0 comment '逻辑删除：0-未删除 1-已删除',
    key idx_item_tenant_order (tenant_id, order_id) comment '租户+订单ID联合索引'
) comment '订单明细';
