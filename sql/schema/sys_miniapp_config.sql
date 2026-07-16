-- 小程序技术配置表
create table if not exists sys_miniapp_config
(
    id                    bigint primary key auto_increment comment '主键ID',
    tenant_id             bigint       not null comment '租户ID',
    app_id                varchar(64)  not null comment '微信小程序AppID',
    app_secret_ciphertext varchar(512) not null comment '加密后的AppSecret',
    status                tinyint      not null default 1 comment '状态 1-启用 0-停用',
    created_by            bigint comment '创建人ID',
    updated_by            bigint comment '最后更新人ID',
    created_at            datetime     not null comment '创建时间',
    updated_at            datetime     not null comment '更新时间',
    deleted               tinyint      not null default 0 comment '逻辑删除标记 0-未删除 1-已删除',
    unique key uk_miniapp_tenant (tenant_id) comment '一个租户仅绑定一个小程序',
    unique key uk_miniapp_app_id (app_id) comment 'AppID全局唯一'
) comment '租户小程序技术配置';
