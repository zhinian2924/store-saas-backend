insert into sys_user (tenant_id, username, password, nickname, account_type, status, created_at, updated_at, deleted)
select null, 'admin', 'admin123', '平台管理员', 'PLATFORM', 1, now(), now(), 0
where not exists (select 1 from sys_user where account_type = 'PLATFORM' and username = 'admin');
