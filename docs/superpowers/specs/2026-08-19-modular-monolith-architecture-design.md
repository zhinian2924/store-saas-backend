# 模块化单体架构设计

## 1. 背景与目标

当前项目是基于 Spring Boot、MyBatis-Plus、Sa-Token、Redis、Apollo 和 MinIO 的多租户门店 SaaS 单体应用。项目已经按业务域分包，但订单、支付、库存和小程序流程仍然直接依赖跨模块 Mapper，业务规则与基础设施实现耦合较重。

本设计将系统演进为模块化单体，并为未来按业务域拆分微服务保留边界。第一阶段保持单个 Spring Boot 应用和单体数据库，重点保证模块依赖清晰、交易状态一致、租户隔离明确和外部依赖可替换。

## 2. 目标模块

```text
com.example.storesaas
├─ shared/
│  ├─ response/ exception/ tenant/ security/ pagination/ event/
├─ identity/       账号、登录、角色和权限
├─ tenant/         租户、门店和租户生命周期
├─ catalog/        商品和商品分类
├─ customer/       消费者、地址和购物车
├─ order/          订单、订单明细和订单状态
├─ inventory/      库存预占、扣减、释放和流水
├─ payment/        支付单、支付渠道和支付回调
├─ analytics/      销售统计和只读报表
├─ miniapp/        小程序配置和微信适配
├─ media/          文件存储适配
└─ interfaces/
   ├─ admin/       后台管理端接口
   └─ mini/        小程序端接口
```

每个业务模块内部使用以下分层：

```text
模块/
├─ interfaces/       Controller、请求 DTO、响应 VO
├─ application/      用例编排和事务边界
├─ domain/           领域模型、状态规则和接口
└─ infrastructure/   Mapper、Repository 实现和外部系统适配
```

小程序和后台是访问渠道，不是订单、支付或商品业务模块。两个渠道共享应用服务，但使用各自的请求模型、权限策略和响应模型。

## 3. 依赖规则

```text
interfaces -> application
application -> domain、shared
infrastructure -> domain、shared
domain 不得依赖 infrastructure
模块不得依赖其他模块的 infrastructure、Entity、Mapper 或 DTO
shared 不得依赖业务模块
```

模块间只能通过公开的 Application Service、Domain Interface、Snapshot、Command、Result 或领域事件通信。

例如订单模块依赖 `catalog.api.ProductReader`、`inventory.api.InventoryReservation` 和 `payment.api.PaymentOrderCreator`，不直接依赖商品、库存或支付 Mapper。

## 4. 核心交易链路

### 4.1 订单

订单负责创建订单、固化商品快照、计算订单金额、维护订单状态和处理取消。订单明细必须保存商品名称、购买时价格、数量和行项目金额，历史订单不再依赖实时商品价格。

订单不得直接修改商品库存、支付单状态或商品当前价格。

### 4.2 商品

商品目录通过 `ProductReader` 提供可售商品查询和商品快照，不向其他模块暴露数据库 Entity 或 Mapper。

### 4.3 库存

库存模块是库存变更的唯一入口，提供：

```text
reserve(tenantId, orderId, items)
commit(tenantId, orderId)
release(tenantId, orderId)
```

库存流程为“可用 -> 预占 -> 已扣减”，取消或支付超时则释放预占库存。库存更新必须使用租户条件、条件更新或乐观锁、订单幂等键和库存流水。

### 4.4 支付

支付模块负责创建支付单、发起支付、处理支付回调、支付状态转换和通知订单。支付回调按支付流水号和渠道交易号幂等处理，已经完成的回调不得重复扣库存或更新订单。

支付状态建议为：

```text
WAITING -> PAYING -> PAID
                   └-> FAILED
WAITING/PAYING -> CLOSED
```

### 4.5 应用流程

创建订单：

```text
渠道 Controller
  -> OrderApplicationService
  -> ProductReader 查询可售商品
  -> 生成商品快照和订单金额
  -> InventoryService.reserve
  -> 保存订单和明细
  -> PaymentApplicationService 创建支付单
```

支付确认：

```text
支付回调
  -> PaymentApplicationService.confirmPayment
  -> 校验签名并幂等更新支付单
  -> OrderStateManager.markPaid
  -> InventoryService.commit
  -> 发布 OrderPaidEvent
```

订单取消统一使用一个应用用例，同时执行库存释放和支付单关闭。后台取消、小程序取消和超时任务不得各自实现一套状态逻辑。

## 5. 数据访问和租户隔离

每个模块只访问自己负责的表。应用层依赖 Repository 接口，MyBatis-Plus Mapper 只能出现在 infrastructure 层。Repository 负责 Entity 与领域模型转换。

租户上下文通过明确接口传递：

```text
requiredTenantId()
currentUserId()
accountType()
isPlatformAccount()
```

租户级 Repository 查询必须显式接收 `tenantId`。数据按平台级、租户级、门店级和消费者级区分，禁止依赖隐式线程状态完成全部隔离。

库存目前仍可暂存于商品表，但只有 inventory 模块允许修改库存字段。后续拆分库存服务时，再迁移为独立库存表或独立数据库。

统一使用 MyBatis-Plus 自动填充审计字段，逐步补充状态历史、库存流水、支付回调日志和租户审核日志。

## 6. 基础设施抽象

业务层通过接口使用外部系统：

```text
PasswordHasher
TokenGateway
SmsCodeStore
WechatLoginGateway
ObjectStorage
PaymentChannel
```

Redis、Sa-Token、微信 API、MinIO 和支付渠道的实现放到 infrastructure。密码使用 BCrypt 或 Argon2 哈希，禁止明文保存和比较；短信验证码、密钥、Token、数据库密码不得写入日志。

公共配置、开发配置、测试配置和生产配置分别放在 `application.yml`、`application-dev.yml`、`application-test.yml` 和 `application-prod.yml`，生产 profile 通过启动参数或环境变量激活。

数据库脚本逐步迁移到 Flyway：

```text
db/migration/
├─ V1__init_schema.sql
├─ V2__add_unique_indexes.sql
├─ V3__add_order_status_history.sql
├─ V4__add_inventory_reservation.sql
└─ V5__add_payment_callback_log.sql
```

## 7. 迁移阶段

1. 建立模块骨架，统一响应、异常、分页、租户上下文和审计字段。
2. 优先迁移 catalog、inventory、order、payment，先稳定交易一致性。
3. 将后台和小程序 Controller 改造成应用服务适配层，移除渠道内重复业务逻辑。
4. 拆分 identity，隔离 Token、Redis 短信验证码和密码哈希。
5. 迁移 customer、media 和 analytics，保持 analytics 为独立只读模块。
6. 完成 Flyway、索引、唯一约束、生产配置、日志和监控治理。

迁移期间允许保留旧 Service 作为兼容代理，但新 Controller 不得继续增加对旧 Service 和跨模块 Mapper 的依赖。

## 8. 测试与架构约束

领域单元测试覆盖订单、支付、库存、租户状态、权限和金额规则，不启动 Spring 或数据库。应用服务测试使用 Mock Repository 和 Gateway，验证跨模块编排、幂等和租户隔离。Repository 测试验证 SQL 条件、逻辑删除、并发库存扣减、唯一约束和事务回滚。API 测试覆盖后台端、小程序端、支付回调、取消、库存不足、跨租户访问和权限错误。

使用 ArchUnit 固化以下约束：

```text
interfaces 不得直接访问 Mapper
domain 不得依赖 Spring、MyBatis、Redis、MinIO
模块不得访问其他模块 infrastructure
shared 不得依赖业务模块
```

关键指标包括订单创建成功/失败、支付回调成功/重复、库存预占成功/失败、库存释放成功和租户访问拒绝。日志至少携带 traceId、tenantId、userId、orderId 和 paymentNo，但不得包含敏感凭证。

## 9. 验收标准

1. 后台端和小程序端共享订单、库存和支付应用服务。
2. 业务模块不直接访问其他模块 Mapper。
3. 库存只有一个写入口，库存操作具备幂等和流水记录。
4. 订单金额来自商品快照，支付回调不会重复处理。
5. 租户级查询显式携带租户上下文。
6. Redis、Sa-Token、MinIO、微信 API 和支付渠道均通过 Gateway 隔离。
7. 核心领域测试不依赖 Spring 和数据库。
8. 架构依赖规则由自动化测试保护。
9. 未来可以按业务模块迁移为独立服务，而不是按技术层拆迁。
