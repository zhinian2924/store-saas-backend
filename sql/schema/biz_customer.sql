-- 消费者表
create table if not exists biz_customer
(
    id         bigint primary key auto_increment comment '主键ID',
    tenant_id  bigint       not null comment '租户ID',
    openid     varchar(128) not null comment '微信openid',
    nickname   varchar(128) comment '昵称',
    avatar_url varchar(255) comment '头像URL',
    status     tinyint      not null default 1 comment '状态 1-正常 0-禁用',
    created_at datetime     not null comment '创建时间',
    updated_at datetime     not null comment '更新时间',
    deleted    tinyint      not null default 0 comment '逻辑删除标记 0-未删除 1-已删除',
    unique key uk_customer_tenant_openid (tenant_id, openid) comment '租户下openid唯一',
    key idx_customer_tenant (tenant_id) comment '租户索引'
) comment '消费者';
