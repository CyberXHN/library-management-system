# AGENTS.md — AI 编程助手工作指南

> 面向在本仓库工作的 AI 编程助手（Copilot / Codex / Claude Code / ZCode 等）。人类协作者请读 CONTRIBUTING.md。

## 项目是什么

《软件设计与开发II》课程 5 人大作业：C/S 架构**图书管理系统**（Java SE）。

- 界面：Swing（菜单栏 + JTable 管理面板，架构与代码风格对齐教材第九章餐饮系统示例）
- 数据库：MySQL + JDBC，库名 `library`，字符集 utf8
- 硬性约束：**不使用 SSH / SpringBoot 等第三方框架**，JDK 8+ 语法
- 功能模块：登录、读者管理、图书/分类管理、借书、还书、统计查询、备份恢复、逾期提醒

## 源码布局（约定）

```
src/
├── com.library.entity/   User、Reader、Category、Book、Borrow（均 implements Serializable）
├── com.library.dao/      IBaseDao<T> 泛型接口 + UserDao/ReaderDao/CategoryDao/BookDao/BorrowDao 及各自 Impl
├── com.library.util/     JDBCConnection、BaseTableModel<T>、XxxDaoFactory ×5、BackupUtil、OverdueTimer
├── com.library.frame/    MainLibrary（入口）、LoginFrame、MainFrame
├── com.library.pane/     ReaderManagePane、CategoryManagePane、BookManagePane、BorrowPane、ReturnPane、StatisticPane
└── com.library.dialog/   ReaderAddDialog、BookAddDialog
sql/library.sql           建库建表脚本
```

## 构建 / 运行

- 无构建工具的普通 Java 工程：IDE 直接运行入口类 `com.library.frame.MainLibrary`，或 `javac` 编译 `src/` 后运行；
- 运行前先在本地 MySQL 执行 `sql/library.sql`；连接参数在 `JDBCConnection`（URL：`jdbc:mysql://localhost:3306/library`）；
- 程序入口逻辑：启动 `OverdueTimer` → 打开 `LoginFrame`。

## 必须遵守的硬规则

1. **接口契约已冻结**：实体字段、DAO 方法签名、数据库表结构不许擅自增删改。用户要求改这些时，先提醒需要走"全组同意 → 更新契约 → 群通知"流程。
2. **跨模块只调 DAO 接口**，绝不直接写他人负责表的 SQL；文件归属见下表，别人的文件不要改。
3. 所有 SQL 一律 `PreparedStatement`，拒绝拼接 SQL（课程考核点）。
4. 实体类保持 `Serializable` + 无参构造 + getter/setter；新增字段必须同步考虑表结构与契约。
5. 库存变动只能走 `BookDao.updateStock(bookId, delta)`，且实现必须并发安全（原子 SQL `UPDATE book SET stock = stock + ? WHERE id = ? AND stock + ? >= 0`，或方法级 `synchronized`）。
6. 界面规格：窗口 1024×728；表格展示统一用 `BaseTableModel<T>`（反射按属性路径取值，支持 `category.name` 这类嵌套属性），不要为每个实体手写 TableModel。
7. 业务口径：借期 30 天；逾期每天罚款 0.5 元；借阅单号 `borrowNo` = `yyyyMMdd` + 4 位序号；图书状态"在架/下架"，下架不可借。

## 分工与文件归属（谁的东西别乱动）

| 成员 | 板块 | 文件 |
|---|---|---|
| A（组长） | 基础层 + 登录 + 集成 | 5 个实体类、IBaseDao、JDBCConnection、UserDao(+Impl)、5 个 DaoFactory、LoginFrame、MainFrame、MainLibrary、sql/ |
| B | 读者管理 | ReaderDaoImpl、ReaderManagePane、ReaderAddDialog |
| C | 图书管理 | CategoryDaoImpl、BookDaoImpl、CategoryManagePane、BookManagePane、BookAddDialog |
| D | 借阅管理 | BorrowDaoImpl、BorrowPane、ReturnPane |
| E | 统计 + 系统管理 | BaseTableModel、BackupUtil、OverdueTimer、StatisticPane |

## Git 约定

- 分支：`main`（受保护，仅 PR 合入）← `dev`（每周集成）← `feature/<模块>`（个人）；
- 提交信息：`feat: …` / `fix: …` / `docs: …` / `refactor: …` / `test: …` / `chore: …`，后接中文描述；
- 详细流程见 CONTRIBUTING.md。

## 其他约定

- 注释、提交信息、沟通一律用中文；标识符用英文；
- 生成代码风格贴近教材示例：类头注释、DAO 结构、Swing 写法；
- `plans/` 目录是本地规划资料，已被 .gitignore 排除：不要提交它，也不要假设仓库里存在它。完整的接口契约文档不在仓库中，涉及精确方法签名时先向用户确认，不要凭记忆编造。
