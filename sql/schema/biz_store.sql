-- 门店表
create table if not exists biz_store
(
    id             bigint primary key auto_increment comment '主键ID',
    tenant_id      bigint       not null comment '租户ID',
    name           varchar(128) not null comment '门店名称',
    address        varchar(255) comment '门店地址',
    business_hours varchar(64) comment '营业时间',
    logo_url       varchar(255) comment 'Logo URL',
    theme_color    varchar(7)   not null default '#0F766E' comment '小程序主题色',
    created_at     datetime     not null comment '创建时间',
    updated_at     datetime     not null comment '更新时间',
    deleted        tinyint      not null default 0 comment '逻辑删除：0-未删除 1-已删除',
    key idx_store_tenant (tenant_id) comment '租户ID索引'
) comment '业务门店';
