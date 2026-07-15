-- 支付单表
create table if not exists biz_payment_order
(
    id         bigint primary key auto_increment comment '主键ID',
    tenant_id  bigint         not null comment '租户ID',
    order_id   bigint         not null comment '订单ID',
    pay_no     varchar(64)    not null unique comment '支付流水号',
    channel    varchar(32)    not null comment '支付渠道',
    status     varchar(32)    not null comment '支付状态',
    amount     decimal(10, 2) not null comment '支付金额',
    created_at datetime       not null comment '创建时间',
    updated_at datetime       not null comment '更新时间',
    deleted    tinyint        not null default 0 comment '逻辑删除：0-未删除 1-已删除',
    key idx_payment_tenant_order (tenant_id, order_id) comment '租户+订单ID联合索引'
) comment '支付单';
