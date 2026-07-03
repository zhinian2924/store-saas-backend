-- 租户表
create table if not exists sys_tenant (
  id bigint primary key auto_increment comment '主键ID',
  tenant_code varchar(64) not null unique comment '租户编码',
  name varchar(128) not null comment '租户名称',
  status tinyint not null default 1 comment '状态：1-正常 0-停用',
  created_at datetime not null comment '创建时间',
  updated_at datetime not null comment '更新时间',
  deleted tinyint not null default 0 comment '逻辑删除：0-未删除 1-已删除'
) comment '系统租户';
