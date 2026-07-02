create database if not exists store_saas default character set utf8mb4 collate utf8mb4_unicode_ci;
use store_saas;

create table if not exists sys_tenant (
  id bigint primary key auto_increment,
  tenant_code varchar(64) not null unique,
  name varchar(128) not null,
  status tinyint not null default 1,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0
);

create table if not exists biz_store (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  name varchar(128) not null,
  address varchar(255),
  business_hours varchar(64),
  logo_url varchar(255),
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_store_tenant (tenant_id)
);

create table if not exists sys_user (
  id bigint primary key auto_increment,
  tenant_id bigint,
  username varchar(64) not null,
  password varchar(128) not null,
  nickname varchar(64),
  account_type varchar(32) not null,
  status tinyint not null default 1,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  unique key uk_user_type_username (account_type, username),
  key idx_user_tenant (tenant_id)
);

create table if not exists biz_product_category (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  name varchar(128) not null,
  sort_no int not null default 0,
  status tinyint not null default 1,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_category_tenant (tenant_id)
);

create table if not exists biz_product (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  category_id bigint,
  name varchar(128) not null,
  image_url varchar(255),
  price decimal(10,2) not null,
  stock int not null default 0,
  status tinyint not null default 1,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_product_tenant (tenant_id),
  key idx_product_category (category_id)
);

create table if not exists biz_inventory_flow (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  product_id bigint not null,
  flow_type varchar(32) not null,
  quantity int not null,
  before_stock int not null,
  after_stock int not null,
  remark varchar(255),
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_flow_tenant (tenant_id),
  key idx_flow_product (product_id)
);

create table if not exists biz_order (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  customer_id bigint,
  order_no varchar(64) not null unique,
  status varchar(32) not null,
  total_amount decimal(10,2) not null,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_order_tenant (tenant_id)
);

create table if not exists biz_order_item (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  order_id bigint not null,
  product_id bigint not null,
  product_name varchar(128) not null,
  price decimal(10,2) not null,
  quantity int not null,
  amount decimal(10,2) not null,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_item_tenant_order (tenant_id, order_id)
);

create table if not exists biz_payment_order (
  id bigint primary key auto_increment,
  tenant_id bigint not null,
  order_id bigint not null,
  pay_no varchar(64) not null unique,
  channel varchar(32) not null,
  status varchar(32) not null,
  amount decimal(10,2) not null,
  created_at datetime not null,
  updated_at datetime not null,
  deleted tinyint not null default 0,
  key idx_payment_tenant_order (tenant_id, order_id)
);

insert into sys_user (tenant_id, username, password, nickname, account_type, status, created_at, updated_at, deleted)
select null, 'admin', 'admin123', '平台管理员', 'PLATFORM', 1, now(), now(), 0
where not exists (select 1 from sys_user where account_type = 'PLATFORM' and username = 'admin');
