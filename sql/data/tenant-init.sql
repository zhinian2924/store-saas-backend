-- 插入平台管理员用户
insert into sys_user (tenant_id, username, mobile, password, nickname, account_type, status, created_at, updated_at,
                      deleted)
select null,
       'admin',
       null,
       'admin123',
       '平台管理员',
       'PLATFORM',
       1,
       now(),
       now(),
       0 where not exists (select 1 from sys_user where account_type = 'PLATFORM' and username = 'admin');

-- 插入租户（已审核通过）
insert into sys_tenant (tenant_code, name, status, created_at, updated_at, deleted)
select 'demo',
       '示例门店',
       1,
       now(),
       now(),
       0 where not exists (select 1 from sys_tenant where tenant_code = 'demo');

-- 插入门店
insert into biz_store (tenant_id, name, address, business_hours, created_at, updated_at, deleted)
select t.id, '示例门店', '北京市朝阳区建国路88号', '09:00-22:00', now(), now(), 0
from sys_tenant t
where t.tenant_code = 'demo'
  and not exists (select 1 from biz_store s where s.tenant_id = t.id and s.deleted = 0);

-- 插入店主用户（已启用）
insert into sys_user (tenant_id, username, mobile, password, nickname, account_type, status, created_at, updated_at,
                      deleted)
select t.id,
       'demo',
       '13800000000',
       'demo123',
       '店主',
       'STORE',
       1,
       now(),
       now(),
       0
from sys_tenant t
where t.tenant_code = 'demo'
  and not exists (select 1 from sys_user where account_type = 'STORE' and mobile = '13800000000');
