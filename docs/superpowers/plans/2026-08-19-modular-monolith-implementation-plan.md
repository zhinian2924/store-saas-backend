# 模块化单体实施计划

## 1. 实施原则

- 采用渐进式迁移，不一次性移动所有包。
- 保持现有接口路径和响应格式兼容，先替换内部实现。
- 先处理订单、库存、支付的一致性，再迁移其他业务域。
- 新代码禁止增加跨模块 Mapper、Entity 和 DTO 依赖。
- 每个阶段完成定向测试后再进入下一阶段。

## 2. 阶段一：公共边界

### 目标

建立模块化单体所需的共享类型和上下文，不改变业务行为。

### 工作项

1. 建立 `shared.response`、`shared.exception`、`shared.tenant`、`shared.security`、`shared.event` 包。
2. 将当前 `common` 中的响应、异常、分页模型迁移到 `shared`，保留兼容类或委托类。
3. 定义 `TenantContext` 和 `CurrentUserContext` 接口。
4. 统一租户、用户和账号类型的读取方式。
5. 增加基础架构测试依赖和第一批 ArchUnit 规则。
6. 建立模块迁移约定和包级文档。

### 验证

- 现有 Controller 测试通过。
- `ApiResponse`、全局异常和分页 JSON 结构不变。
- 架构测试可以识别 Controller 直接依赖 Mapper 的违规代码。

## 3. 阶段二：商品目录边界

### 目标

让订单和小程序端通过商品目录接口读取商品，不再直接引用商品 Mapper 和 Entity。

### 工作项

1. 建立 `catalog.api.ProductReader` 和 `ProductSnapshot`。
2. 将现有 `ProductService` 的可售校验提取为目录应用服务。
3. 增加商品 Repository 接口和 MyBatis 实现。
4. 修改 `OrderService`、`MiniOrderService`、`CartService` 使用 `ProductReader`。
5. 修改小程序公开商品接口，移除直接 Mapper 查询。
6. 统一商品状态、库存展示和租户条件。

### 验证

- 商品上下架和库存不足行为不变。
- 后台订单、小程序订单和购物车使用同一套可售校验。
- `order`、`mini` 新代码不再引用 `product.mapper`。

## 4. 阶段三：库存预占与流水

### 目标

建立库存唯一写入口，消除支付和订单代码直接修改商品库存的路径。

### 工作项

1. 定义 `inventory.api.InventoryReservation`。
2. 增加预占、确认、释放的领域模型和结果类型。
3. 增加库存 Repository 和条件更新 SQL。
4. 增加订单级库存幂等记录或唯一约束。
5. 保留现有库存流水，并统一流水类型和来源字段。
6. 将现有 `InventoryService` 的库存变更逻辑收敛到库存模块。
7. 删除支付服务直接更新商品库存的代码。

### 验证

- 并发扣减不会出现负库存。
- 同一订单重复预占、确认、释放不会重复修改库存。
- 租户 A 无法读写租户 B 的商品库存。
- 库存不足时订单创建不会留下不可支付订单。

## 5. 阶段四：订单应用服务

### 目标

统一后台和小程序的订单创建、查询、取消和状态流转。

### 工作项

1. 建立 `order.application.OrderApplicationService`。
2. 定义 `CreateOrderCommand`、`CancelOrderCommand` 和订单结果模型。
3. 建立订单 Repository，隔离 `StoreOrderMapper` 和 `OrderItemMapper`。
4. 创建订单时保存商品名称、价格和金额快照。
5. 将后台 `OrderController` 和小程序 `MiniOrderController` 改为调用应用服务。
6. 统一取消订单规则，并调用库存释放和支付关闭接口。
7. 保留旧 `OrderService` 作为短期兼容代理，禁止新增调用。

### 验证

- 后台和小程序创建出的订单状态、金额和明细一致。
- 商品价格变化不影响已创建订单。
- 取消订单具备幂等性。
- 跨租户订单查询和明细查询均被拒绝。

## 6. 阶段五：支付应用服务

### 目标

统一支付单创建、支付确认、回调幂等和订单状态通知。

### 工作项

1. 定义 `payment.api.PaymentOrderCreator` 和支付结果处理接口。
2. 建立支付 Repository，隔离 `PaymentOrderMapper`。
3. 统一支付状态机和非法状态处理。
4. 增加支付回调日志及渠道交易号唯一约束。
5. 将 `PaymentService` 和 `MiniPaymentService` 合并到应用服务边界。
6. 支付确认成功后调用订单状态服务和库存确认接口。
7. 增加 `OrderPaidEvent`，先采用单体内事务提交后事件。

### 验证

- 重复支付回调不会重复扣库存。
- 非法支付状态转换被拒绝。
- 支付失败、关闭和超时路径不会错误标记订单已支付。
- 支付回调签名失败不会修改业务数据。

## 7. 阶段六：身份、客户和基础设施

### 工作项

1. 拆分 `AuthService` 为登录、短信、资料和租户注册应用服务。
2. 抽取 `PasswordHasher`、`SmsCodeStore` 和 `TokenGateway`。
3. 迁移小程序消费者、地址和购物车到 `customer` 模块。
4. 抽取 `WechatLoginGateway` 和 `ObjectStorage`。
5. 使用 BCrypt 或 Argon2 替换明文密码处理。
6. 将生产配置中的敏感值改为环境变量或 Apollo 注入。

### 验证

- 平台账号、店主、员工和消费者登录场景分别通过。
- Redis、Sa-Token、微信和 MinIO 依赖不出现在领域层。
- 日志不包含密码、验证码、密钥和 Token。

## 8. 阶段七：数据库与部署治理

1. 引入 Flyway 并将现有初始化 SQL 转换为版本迁移。
2. 增加订单号、支付单号、租户编码和渠道交易号唯一约束。
3. 增加订单、商品、库存和支付常用查询索引。
4. 拆分 `application-dev.yml`、`application-test.yml` 和 `application-prod.yml`。
5. 增加 Docker 健康检查和启动 profile。
6. 增加订单、支付、库存和租户拒绝的业务指标。

## 9. 每阶段交付门槛

每个阶段必须满足：

1. 只修改当前阶段涉及的模块。
2. 先执行定向测试，再执行全量测试。
3. 记录与当前改动无关的既有失败，不擅自修改无关模块。
4. `git diff --check` 通过，工作区不残留临时配置。
5. 架构测试不允许新增跨模块 infrastructure 依赖。

## 10. 第一批建议实施范围

第一轮代码实施只覆盖阶段一到阶段三：

```text
shared 边界
catalog.ProductReader
inventory.InventoryReservation
订单/支付中的库存调用替换
ArchUnit 基础规则
```

第一轮不修改外部 API 路径，不引入微服务、消息队列或新的数据库实例，确保改动可回滚且便于验证。
