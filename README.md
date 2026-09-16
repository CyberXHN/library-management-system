# 图书管理系统（Java SE · C/S 架构）

> 《软件设计与开发Ⅱ》课程 5 人大作业：Swing + MySQL + JDBC 的图书借阅管理系统。
> **不使用 SSH / SpringBoot 等第三方框架**（课程硬性要求），JDK 8+。

一句话上手：**起 MySQL → 导入 `sql/library.sql` → 运行 `com.library.frame.MainLibrary` → 用 `admin / admin123` 登录。**

---

## 一、技术栈

| 项 | 说明 |
|---|---|
| 语言 | Java SE（JDK 8+，本机用 21 也可） |
| 界面 | Swing（菜单栏 + JTable 管理面板 + JDialog 表单） |
| 数据库 | MySQL 5.7 / 8.0，库名 `library`，字符集 utf8mb4 |
| 数据访问 | 原生 JDBC，全部 `PreparedStatement`（禁止拼接 SQL） |
| 驱动 | `lib/mysql-connector-j-8.0.33.jar`（MySQL 8 驱动 `com.mysql.cj.jdbc.Driver`） |
| 依赖 | 除 MySQL 驱动外无任何第三方库 |

架构对齐教材第九章餐饮系统示例：**实体类（entity）+ 控制类（DAO）+ 边界类（frame/pane/dialog）+ 工具类（util）**。

知识点落点：泛型（`IBaseDao<T>`）、反射（`BaseTableModel<T>`）、序列化（`BackupUtil`）、多线程（`OverdueTimer`）、JDBC（各 `XxxDaoImpl`）、集合（`List` / `Map` 统计）。

---

## 二、功能一览

```
登录（user 表校验）
├── 读者管理 → 读者信息管理        读者增删改查、按借书证号查询
├── 图书管理
│   ├── 分类管理                   分类增删改查（有图书引用时禁止删除）
│   └── 图书管理                   图书增删改查、模糊检索、上架/下架
├── 借阅管理
│   ├── 借书                       校验读者 → 检索图书 → 扣库存 → 生成借阅记录
│   └── 还书                       展示未还记录 → 计算逾期罚款 → 恢复库存
├── 统计查询 → 借阅统计            借阅总数、逾期数量
└── 系统管理
    ├── 数据备份                   把图书数据序列化写入 .dat
    ├── 数据恢复                   反序列化备份文件并写回数据库
    └── 退出系统
```

后台还有一条 `OverdueTimer` 线程：每 60 秒扫描一次未还记录，把到期未还的置为"逾期"。

---

## 三、快速开始（第 0–3 步）

### 前置要求

- JDK 8 或以上（`java -version` 能打印版本）
- MySQL 5.7 / 8.0 已安装
- MySQL JDBC 驱动：**仓库已自带** `lib/mysql-connector-j-8.0.33.jar`（正常 clone 就有；万一缺了见下面第 0 步）

### 第 0 步：准备 MySQL JDBC 驱动（仓库已自带，通常可跳过）

驱动 **`lib/mysql-connector-j-8.0.33.jar` 已随仓库提交**（2.4 MB），clone 下来就在 `lib/` 下。
只有 `lib/` 里缺了这个 jar（误删、换电脑拷漏了）或要换版本时，才按下面重新下载：

| 下载来源 | 地址 | 说明 |
|---|---|---|
| Maven Central（官方，推荐） | `https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar` | 直接下就是 jar 本体 |
| 阿里云镜像（国内快） | `https://maven.aliyun.com/repository/public/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar` | 与官方同一构件 |
| MySQL 官网 | https://dev.mysql.com/downloads/connector/j/ | 选 `8.0.33` → `Platform Independent` → 下 zip，解压后取里面的 `mysql-connector-j-8.0.33.jar`（整包还含源码/文档，体积更大） |

下载后**放进项目 `lib/` 目录、文件名保持原样**，再校验一下是否完整（可选）：

```bash
# Git Bash / Linux / macOS
sha256sum lib/mysql-connector-j-8.0.33.jar
```
```cmd
:: Windows CMD（PowerShell 用 Get-FileHash -Algorithm SHA256）
certutil -hashfile lib\mysql-connector-j-8.0.33.jar SHA256
```

应得到 `e2a3b2fc726a1ac64e998585db86b30fa8bf3f706195b78bb77c5f99bf877bd9`
（本仓库该 jar 与 Maven Central 官方文件二进制一致）。

**驱动版本与代码的对应关系**（换版本时注意改 `JDBCConnection`）：

| 驱动版本 | 驱动类名（`JDBCConnection.DRIVER`） | 适用 |
|---|---|---|
| 8.x（本仓库 8.0.33） | `com.mysql.cj.jdbc.Driver` | MySQL 5.7 / 8.0 |
| 5.1.x（教材老写法） | `com.mysql.jdbc.Driver` | MySQL 5.x；不支持 MySQL 8 的 `caching_sha2_password` 认证，不推荐 |

准备工作做完后，后续照第 3 步把 `lib/mysql-connector-j-8.0.33.jar` 挂进 IDE 依赖（或写进命令行 classpath）。

### 第 1 步：启动 MySQL，导入建库脚本

先确认服务在跑（Windows）：

```cmd
net start MySQL
```

> 需要**管理员**权限；也可以用 `services.msc` 图形界面启动，或直接开 MySQL Workbench。
> 验证：`netstat -ano | findstr 3306` 能看到 `LISTENING`。

然后在项目根目录导入脚本（**第一次必须做**，脚本会 `DROP` 再 `CREATE` 库）：

```cmd
cd /d "路径\图书管理系统"
"C:\Program Files\MySQL\bin\mysql" -u root -p --default-character-set=utf8mb4 < sql\library.sql
```

脚本末尾会打印自检结果：`user=2 / category=5 / book=10 / reader=4 / borrow=4`。

> ⚠️ `--default-character-set=utf8mb4` 不能省，否则 Windows 下中文会乱码。

### 第 2 步：确认数据库连接参数

打开 `src/com/library/util/JDBCConnection.java`，按本地环境改 `USER` / `PWD`（默认 `root` / `123456`）：

```java
private static final String URL  = "jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf8";
private static final String USER = "root";
private static final String PWD  = "123456";
```

> 改完**不要提交**这个文件（`git diff` 自查）；连服务器时把 `localhost` 换成服务器 IP。

### 第 3 步：编译并运行

**方式 A：IDE（IDEA / Eclipse，推荐）**

1. 以普通 Java 工程打开项目根目录；
2. 把 `lib/mysql-connector-j-8.0.33.jar` 加进依赖（驱动获取见第 0 步；IDEA：Project Structure → Libraries → **+** → Java → 选 jar；Eclipse：Build Path → Add JARs）；
3. 运行入口类 **`com.library.frame.MainLibrary`**（`src/com/library/frame/MainLibrary.java`）。

**方式 B：命令行（Git Bash）**

```bash
javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.0.33.jar" -d bin $(find src -name "*.java")
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" com.library.frame.MainLibrary
```

**方式 C：命令行（CMD / PowerShell）**

```cmd
dir /s /b src\*.java > %TEMP%\sources.txt
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.0.33.jar" -d bin @%TEMP%\sources.txt
java -cp "bin;lib\mysql-connector-j-8.0.33.jar" com.library.frame.MainLibrary
```

```powershell
javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.0.33.jar" -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp "bin;lib/mysql-connector-j-8.0.33.jar" com.library.frame.MainLibrary
```

> - 入口是 `MainLibrary`（不是 `LoginFrame`）：它会先启动逾期检测线程，再打开登录窗口；
> - `bin/` 是编译输出目录，已在 `.gitignore` 中，不会入库；
> - 类路径分隔符：Windows 用 `;`，Linux / macOS 用 `:`。

### 登录账号

| 用户名 | 密码 |
|---|---|
| `admin` | `admin123` |
| `test` | `123456` |

（数据来自 `sql/library.sql` 的演示数据，见脚本末尾。）

---

## 四、验收路径（点一遍就够）

1. 登录 → `admin / admin123`；
2. **读者信息管理**：新增/编辑/删除读者，按借书证号查询（证号唯一、电话 11 位）；
3. **分类管理**：新增分类 → 删除它，应被"该分类下仍有图书"拦住；
4. **图书管理**：按关键字模糊检索、新增/修改图书、上架/下架；
5. **借书**：输入借书证号校验读者 → 检索图书 → 确认借书（库存 -1，提示借阅单号与应还日期）；
6. **还书**：选中未还记录 → 确认（逾期按 0.5 元/天罚款，库存 +1）；
7. **借阅统计**：查看借阅总数 / 逾期数量；
8. **系统管理 → 数据备份**：另存为 `backup.dat`；改动几本书后 **数据恢复**，应提示"更新 X 条，新增 Y 条"。

---

## 五、业务口径（全组统一）

| 项 | 口径 |
|---|---|
| 借期 | 30 天（`borrowDate` + 30 天 = `dueDate`） |
| 逾期罚款 | 每逾期 1 天 0.5 元 |
| 借阅单号 | `yyyyMMdd` + 4 位当日序号（唯一） |
| 图书状态 | 在架 / 下架，**下架不可借** |
| 库存 | 只能通过 `BookDao.updateStock(bookId, delta)` 增减，实现为原子 SQL，并发安全 |
| 逾期状态 | `OverdueTimer` 后台线程自动置为"逾期" |
| 备份范围 | 图书数据（`List<Book>`）；恢复时按 id 命中则覆盖更新，缺失则新增（不清表，避免打断借阅记录对 bookId 的引用） |

---

## 六、工程结构

```
src/com/library/
├── entity/   User、Reader、Category、Book、Borrow          （均 implements Serializable）
├── dao/      IBaseDao<T>（泛型接口）+ UserDao/ReaderDao/CategoryDao/BookDao/BorrowDao
│             及各自 Impl（每张表 SQL 只出现在对应 Impl 中）
├── util/     JDBCConnection（连接开关）、BaseTableModel<T>（反射表格模型）、
│             XxxDaoFactory ×5（工厂）、BackupUtil（序列化备份）、OverdueTimer（多线程）
├── frame/    MainLibrary（入口）、LoginFrame（登录）、MainFrame（主界面 1024×728）
├── pane/     ReaderManagePane、CategoryManagePane、BookManagePane、
│             BorrowPane、ReturnPane、StatisticPane
└── dialog/   ReaderAddDialog、BookAddDialog
sql/library.sql    建库建表 + 演示数据（统一用它初始化，禁止各自改表）
docs/              接口契约、模块讲解文档
lib/               MySQL 驱动 jar（mysql-connector-j-8.0.33.jar，已入库）
```

窗口规格统一 **1024×728**；表格一律 `BaseTableModel<T>` + `JTable`（反射按属性路径取值，支持 `category.name` 这类嵌套属性）。

---

## 七、常见问题排查

| 现象 | 原因 / 处理 |
|---|---|
| `Communications link failure` | MySQL 没启动，或 URL 的 host/port 不对 |
| `Access denied for user 'root'@'localhost'` | 密码与 `JDBCConnection.PWD` 不一致 |
| 登录提示"用户名或密码错误"但账号没错 | 多半是没导入 `sql/library.sql`（DAO 里 SQLException 只打印堆栈，界面统一按密码错提示） |
| 中文乱码 | 导入时漏了 `--default-character-set=utf8mb4`，重新导入 |
| `Public Key Retrieval is not allowed` | MySQL 8 驱动偶发，URL 末尾追加 `&allowPublicKeyRetrieval=true&useSSL=false` |
| `Unknown database 'library'` | 同上，先执行建库脚本 |
| 编译报 `程序包 com.library.entity 不存在` | 编译时漏了源码目录 / 未加 `lib` 里的驱动 jar |
| `No suitable driver found for jdbc:mysql://...` | 运行 classpath 没带 `lib/mysql-connector-j-8.0.33.jar`（获取方法见第 0 步） |
| `lib/` 里找不到驱动 jar | 本仓库已提交该 jar，正常 clone 应该有；缺失按第 0 步下载，文件名需保持 `mysql-connector-j-8.0.33.jar` |
| 端口 3306 被占用 | 改 MySQL 端口的同时要改 `JDBCConnection.URL` |

---

## 八、已知问题（待对应模块负责人处理）

**暂无待处理缺陷。** 原 E 模块问题（`BaseTableModel` 嵌套属性、`BackupUtil` 契约签名、`OverdueTimer` 逾期判定与重复写库、`StatisticPane` 空值保护与借阅排行展示）已随 PR #21 / #22 / #24 全部修复。

> 接口契约的正式版见 `docs/图书管理系统-接口契约.md`，实体字段 / DAO 签名 / 表结构**冻结**，任何修改需"全组同意 → 更新契约 → 群通知"。

---

## 九、文档与协作

| 文档 | 内容 |
|---|---|
| `docs/图书管理系统-接口契约.md` | **最重要**：实体字段、DAO 方法签名、表结构、文件归属、跨模块调用口径 |
| `docs/读者管理模块讲解.md` | 读者管理模块（B）实现讲解，答辩参考 |
| `CONTRIBUTING.md` | 分支模型、日常流程、提交信息规范、代码规范 |
| `AGENTS.md` | AI 编程助手工作指南（人类协作者看 CONTRIBUTING.md） |

协作速记：

- 分支：`main`（受保护，仅 PR）← `dev`（每周集成）← `feature/<模块>`（个人）；
- 提交信息：`feat: 中文描述` / `fix: …` / `docs: …` / `refactor: …` / `test: …` / `chore: …`；
- 一个文件只有一个主人（见契约"文件归属总表"），不改别人的文件；
- 跨模块取数只调对方 DAO 接口，**绝不直接写他人负责表的 SQL**；
- 克隆地址：`https://github.com/CyberXHN/library-management-system.git`。

---

## 十、分工对照

| 成员 | 板块 | 主要文件 |
|---|---|---|
| A（组长） | 基础层 + 登录 + 集成 | 5 个实体、`IBaseDao`、`JDBCConnection`、`UserDao(+Impl)`、5 个 `XxxDaoFactory`、`LoginFrame`、`MainFrame`、`MainLibrary`、`sql/` |
| B | 读者管理 | `ReaderDaoImpl`、`ReaderManagePane`、`ReaderAddDialog` |
| C | 图书管理 | `CategoryDaoImpl`、`BookDaoImpl`、`CategoryManagePane`、`BookManagePane`、`BookAddDialog` |
| D | 借阅管理 | `BorrowDaoImpl`、`BorrowPane`、`ReturnPane` |
| E | 统计 + 系统管理 | `BaseTableModel`、`BackupUtil`、`OverdueTimer`、`StatisticPane` |
