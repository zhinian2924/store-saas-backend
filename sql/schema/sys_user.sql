-- 系统用户表
create table if not exists sys_user
(
    id           bigint primary key auto_increment comment '主键ID',
    tenant_id    bigint comment '租户ID',
    username     varchar(64)  not null comment '用户名',
    mobile       varchar(20) comment '手机号',
    password     varchar(128) not null comment '密码',
    nickname     varchar(64) comment '昵称',
    staff_role   varchar(32) comment '店员岗位',
    permissions  varchar(512) comment '权限码，逗号分隔',
    account_type varchar(32)  not null comment '账号类型',
    status       tinyint      not null default 1 comment '状态：1-正常 0-停用',
    created_at   datetime     not null comment '创建时间',
    updated_at   datetime     not null comment '更新时间',
    deleted      tinyint      not null default 0 comment '逻辑删除：0-未删除 1-已删除',
    unique key uk_user_type_username (account_type, username) comment '账号类型+用户名唯一索引',
    unique key uk_user_type_mobile (account_type, mobile) comment '账号类型+手机号唯一索引',
    key idx_user_tenant (tenant_id) comment '租户ID索引'
) comment '系统用户';
