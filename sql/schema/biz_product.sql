-- 商品表
create table if not exists biz_product
(
    id          bigint primary key auto_increment comment '主键ID',
    tenant_id   bigint         not null comment '租户ID',
    category_id bigint comment '分类ID',
    name        varchar(128)   not null comment '商品名称',
    image_url   varchar(255) comment '商品图片URL',
    price       decimal(10, 2) not null comment '单价',
    stock       int            not null default 0 comment '库存数量',
    status      tinyint        not null default 1 comment '状态：1-上架 0-下架',
    created_at  datetime       not null comment '创建时间',
    updated_at  datetime       not null comment '更新时间',
    deleted     tinyint        not null default 0 comment '逻辑删除：0-未删除 1-已删除',
    key idx_product_tenant (tenant_id) comment '租户ID索引',
    key idx_product_category (category_id) comment '分类ID索引'
) comment '业务商品';
