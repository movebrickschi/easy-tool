<div align="center">

# 🧰 easy-tool

**一个可插拔、模块化的 Spring Boot 全栈工具集 · 让业务开发不再重复造轮**

*A pluggable, modular Spring Boot utility framework — stop reinventing the wheel*

[![Java](https://img.shields.io/badge/Java-8%2F17%2F21-orange?logo=openjdk&logoColor=white)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7%20%7C%203.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Maintenance](https://img.shields.io/badge/Maintained-yes-brightgreen.svg)](https://github.com/movebrickschi/easy-tool/commits/release-3.x)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/movebrickschi/easy-tool/pulls)

[📖 中文说明](#-项目简介) · [🚀 快速开始](#-快速开始) · [🧩 模块一览](#-模块一览) · [💬 反馈](https://github.com/movebrickschi/easy-tool/issues)

</div>

---

## 🆕 项目简介

`easy-tool` 是一个基于 **Spring Boot + Hutool** 构建的业务开发工具集，将日常开发中高频使用的能力以「**开箱即用**」的方式封装。采用 **多模块架构**，需要什么引什么，避免依赖臃肿。

### 设计初衷

- 🎯 **减少重复造轮**：把 Redis、MyBatis-Plus、HTTP 请求、AI 调用等场景中「人人都要写一遍」的东西提炼成统一能力。
- 🧩 **模块可插拔**：你只需要引入需要的模块，不必担心带进一堆用不到的依赖。
- 🔗 **与 Spring Boot 生态无缝集成**：基于 Spring Boot 自动装配机制，依赖即用，零配置启动。
- 📝 **详尽的示例与设计说明**：源码可读、可拓展、易于定制。

---

## 🔖 版本说明

| 版本分支 / Branch | 适配 / Compatibility | 状态 / Status |
| --- | --- | --- |
| `release-3.x` | Spring Boot **3.0+** · JDK 17+ | ✅ 主推荐 / Recommended |
| `release-2.5.x` | Spring Boot **2.7+** · JDK 8+ | ✅ 足额维护 / LTS |

> 💡 推荐新项目使用 `release-3.x`，备件项目可选 `release-2.5.x`。

---

## 🧩 模块一览 · Modules

| 模块 / Module | 说明 / Description | 依赖场景 |
| --- | --- | --- |
| **`easy-tool-core`** | 核心公共能力，包含通用实体、常量、常用工具类 | 所有模块的基础 |
| **`easy-tool-redis-spring-boot-starter`** | Redis / Redisson 二次封装，包含缓存、分布式锁等 | 高并发项目 |
| **`easy-tool-mybatis-plus`** | MyBatis-Plus 增强：通用 Service/Mapper、查询助手、安全检查 | 业务 CRUD |
| **`easy-tool-request`** | HTTP 客户端封装，统一请求 / 响应 / 重试 / 日志 | 外部服务调用 |
| **`easy-tool-ai`** | AI 能力封装（调用、Token 计算、上下文等） | LLM 应用集成 |
| **`easy-tool-all`** | 一错全拿：包含以上所有模块 | 懒人专用 😄 |

---

## 🚀 快速开始

### 1️⃣ 按需引入（推荐）

Maven：

```xml
<dependency>
    <groupId>io.github.movebrickschi</groupId>
    <artifactId>easy-tool-redis-spring-boot-starter</artifactId>
    <version>3.x.x</version>
</dependency>
```

Gradle (Kotlin DSL)：

```kotlin
implementation("io.github.movebrickschi:easy-tool-redis-spring-boot-starter:3.x.x")
```

> 💡 将 `3.x.x` 替换为 Maven 中央仓库中的最新版本。

### 2️⃣ 一错全拿（快速试用）

```xml
<dependency>
    <groupId>io.github.movebrickschi</groupId>
    <artifactId>easy-tool-all</artifactId>
    <version>3.x.x</version>
</dependency>
```

### 3️⃣ 启动应用

不需要额外配置，Spring Boot 启动后自动装配：

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

随后在任何位置注入并使用。

---

## 💡 为什么选择 easy-tool

| 问题 / Pain Point | easy-tool 的答案 / What easy-tool offers |
| --- | --- |
| 每个项目都要重写一遍 Redis 常用封装 | `easy-tool-redis-spring-boot-starter` 直接拿来就用 |
| HTTP 客户端重试、日志、超时代码重复 | `easy-tool-request` 提供统一 API |
| AI 大模型调用起点高 | `easy-tool-ai` 快速接入 |
| 担心依赖臃肿 | 模块化设计，按需引入 |

---

## 🤝 参与贡献 · Contributing

欢迎提 Issue 、PR！

1. Fork 本仓库
2. 创建特性分支：`git checkout -b feature/awesome`
3. 提交代码：`git commit -m 'feat: add awesome'`
4. 推送：`git push origin feature/awesome`
5. 发起 Pull Request

---

## 📜 License

[Apache License 2.0](LICENSE) © [movebrickschi](https://github.com/movebrickschi)

---

<div align="center">

**如果这个项目帮助到了你，请给个 Star ⭐ 支持一下**<br/>
*If this project helps you, please consider giving it a star ⭐*

[⚡ 查看作者其他作品 / See more works by the author](https://github.com/movebrickschi)

</div>
