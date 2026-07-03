-- 商品分类表
create table if not exists biz_product_category (
  id bigint primary key auto_increment comment '主键ID',
  tenant_id bigint not null comment '租户ID',
  name varchar(128) not null comment '分类名称',
  sort_no int not null default 0 comment '排序号',
  status tinyint not null default 1 comment '状态：1-启用 0-停用',
  created_at datetime not null comment '创建时间',
  updated_at datetime not null comment '更新时间',
  deleted tinyint not null default 0 comment '逻辑删除：0-未删除 1-已删除',
  key idx_category_tenant (tenant_id) comment '租户ID索引'
) comment '商品分类';
