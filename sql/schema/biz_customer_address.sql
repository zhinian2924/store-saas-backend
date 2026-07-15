-- 消费者地址表
create table if not exists biz_customer_address
(
    id          bigint primary key auto_increment comment '主键ID',
    tenant_id   bigint       not null comment '租户ID',
    customer_id bigint       not null comment '消费者ID',
    consignee   varchar(64)  not null comment '收货人',
    phone       varchar(32)  not null comment '联系电话',
    province    varchar(64) comment '省份',
    city        varchar(64) comment '城市',
    district    varchar(64) comment '区/县',
    detail      varchar(255) not null comment '详细地址',
    is_default  tinyint      not null default 0 comment '是否默认地址 0-否 1-是',
    created_at  datetime     not null comment '创建时间',
    updated_at  datetime     not null comment '更新时间',
    deleted     tinyint      not null default 0 comment '逻辑删除标记 0-未删除 1-已删除',
    key idx_address_customer_tenant (customer_id, tenant_id) comment '消费者租户联合索引'
) comment '消费者地址';
