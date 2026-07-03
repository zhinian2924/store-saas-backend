-- 订单表
create table if not exists biz_order (
  id bigint primary key auto_increment comment '主键ID',
  tenant_id bigint not null comment '租户ID',
  customer_id bigint comment '客户ID',
  order_no varchar(64) not null unique comment '订单号',
  status varchar(32) not null comment '订单状态',
  total_amount decimal(10,2) not null comment '订单总金额',
  created_at datetime not null comment '创建时间',
  updated_at datetime not null comment '更新时间',
  deleted tinyint not null default 0 comment '逻辑删除：0-未删除 1-已删除',
  key idx_order_tenant (tenant_id) comment '租户ID索引'
) comment '业务订单';
