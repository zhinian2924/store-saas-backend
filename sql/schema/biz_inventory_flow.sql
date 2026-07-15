-- 库存流水表
create table if not exists biz_inventory_flow
(
    id           bigint primary key auto_increment comment '主键ID',
    tenant_id    bigint      not null comment '租户ID',
    product_id   bigint      not null comment '商品ID',
    flow_type    varchar(32) not null comment '流水类型：IN-入库 OUT-出库',
    quantity     int         not null comment '变动数量',
    before_stock int         not null comment '变动前库存',
    after_stock  int         not null comment '变动后库存',
    remark       varchar(255) comment '备注',
    created_at   datetime    not null comment '创建时间',
    updated_at   datetime    not null comment '更新时间',
    deleted      tinyint     not null default 0 comment '逻辑删除：0-未删除 1-已删除',
    key idx_flow_tenant (tenant_id) comment '租户ID索引',
    key idx_flow_product (product_id) comment '商品ID索引'
) comment '库存流水';
