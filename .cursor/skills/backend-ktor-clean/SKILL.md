---
name: backend-ktor-clean
description: Build and refactor Ktor backend services with strict Clean Architecture boundaries (api/application/domain/infrastructure), dependency direction control, DTO mapping rules, and layered testing. Use when implementing Ktor APIs, integrating upstream clients, or refactoring messy backend modules.
---

# Backend Ktor Clean

## 目标

为 `Indexed-server` 提供统一、严格、可执行的 Ktor 后端架构规范，优先用于新模块和重构模块。

## 官方参考

- https://ktor.io/docs/server-create-a-new-project.html
- https://ktor.io/docs/server-routing.html
- https://ktor.io/docs/server-serialization.html
- https://ktor.io/docs/server-testing.html
- https://github.com/ktorio/ktor-samples

## 目录约定（强约束）

以模块 `comics-api` 为例：

```text
comics-api/src/main/kotlin/com/indexed/server/comics/
  api/                 # Ktor route + request/response adapter（HTTP层）
  application/         # 用例编排（UseCase）
  domain/              # 领域模型 + 仓储接口 + 领域错误
  infrastructure/      # 外部实现（Jikan client / DB / cache / repository impl）
  bootstrap/           # 依赖装配（可选）
```

## 依赖方向（强约束）

- `api` -> `application`
- `application` -> `domain`
- `infrastructure` -> `domain`
- 禁止：`domain` 依赖 `ktor/http/client/sql` 等框架细节
- 禁止：`api` 直接调用 `infrastructure`

## 分层职责

### `api`

- 只做：
  - 参数解析与校验
  - 调用 use case
  - 结果映射到 HTTP 状态码与响应 DTO
- 不做：
  - 业务计算
  - 第三方 API 拼装
  - 数据访问

### `application`

- 每个用例一个类（或函数对象），如：
  - `ListComicsUseCase`
  - `GetComicDetailUseCase`
  - `GetComicChaptersUseCase`
- 只处理业务流程，不感知 Ktor `ApplicationCall`

### `domain`

- 定义：
  - 领域实体（尽量与 API DTO 解耦）
  - 仓储接口（如 `ComicCatalogRepository`）
  - 领域错误（如 `ComicNotFound`, `InvalidComicId`）
- 禁止出现 HTTP 状态码、Ktor 类型

### `infrastructure`

- 放实现细节：
  - `JikanClient`（上游请求）
  - `JikanComicRepository`（把上游数据转领域对象）
  - 重试、限流、缓存（后续）

## DTO 与映射规范

- API DTO 仅存在于 `api/dto`
- 上游 DTO 仅存在于 `infrastructure/upstream/dto`
- 领域模型仅存在于 `domain/model`
- 映射函数放在靠近“源类型”的位置：
  - `JikanMangaDto -> DomainComic`：放 `infrastructure`
  - `DomainComic -> ComicInfoResponse`：放 `api`
- 禁止跨层直接复用 DTO

## 错误处理规范

- Use case 返回 `Result<T>` 或显式领域错误类型
- `api` 层统一把领域错误转换为 HTTP：
  - `NotFound` -> `404`
  - `Validation` -> `400`
  - `UpstreamUnavailable` -> `502/503`
- 不在深层抛裸 `IllegalStateException` 给 route

## 路由写法规范

- 一个 route 文件只负责一个聚合边界（如 `ComicRoutes`）
- route handler 目标长度 <= 20 行
- route handler 结构固定：
  1. parse/validate
  2. call use case
  3. map response

## 测试分层

- `api`：`testApplication` 做路由/状态码/序列化测试
- `application`：纯 Kotlin 单元测试（mock repository）
- `infrastructure`：上游客户端映射测试（可用 fixture JSON）

## 重构流程（执行清单）

复制并更新以下 checklist：

```text
Refactor Checklist:
- [ ] 建立 application/domain/infrastructure 目录
- [ ] 抽出 domain repository interface
- [ ] 把 route 中业务逻辑迁移到 use case
- [ ] 把 Jikan 调用放进 infrastructure client/repository
- [ ] 建立领域错误并统一 HTTP 映射
- [ ] 修正 DTO 分层与 mapper 位置
- [ ] 补充 api/application 测试
- [ ] 运行 ./gradlew :comics-api:test
```

## 命名规范

- Use case：动词开头，`ListComicsUseCase`
- Repository 接口：`*Repository`
- Infrastructure 实现：`Jikan*Repository` / `*Client`
- API 响应：`*Response`
- API 请求参数：`*Query` / `*Request`

## 适用场景

- 新建 Ktor API 模块
- 路由中出现大量业务逻辑
- 从 mock 切换真实上游数据
- 后端结构混乱，需要分层重构
