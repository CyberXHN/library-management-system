-- ============================================================
-- 图书管理系统 · 建库建表脚本（契约冻结版，负责人：A）
--
-- 用法（在 mysql 客户端所在机器执行）：
--   mysql -u root -p < sql/library.sql
--   或登录后执行：source /path/to/library.sql
--
-- 注意：
--   1. 脚本会先 DROP 再 CREATE，重复执行 = 清空重置全部数据；
--   2. 兼容 MySQL 5.7 / 8.0；
--   3. 字符集 utf8mb4，与 JDBC URL 中 characterEncoding=utf8 兼容
--      （Connector/J 会自动映射）；
--   4. 表结构与《图书管理系统-接口契约》一致，修改需走契约变更流程。
-- ============================================================

DROP DATABASE IF EXISTS library;
CREATE DATABASE library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE library;

-- ------------------------------------------------------------
-- 1. 管理员表（登录用，A 负责）
-- ------------------------------------------------------------
CREATE TABLE user (
  id       INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名',
  password VARCHAR(20) NOT NULL COMMENT '密码'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员';

-- ------------------------------------------------------------
-- 2. 读者表（B 负责）
-- ------------------------------------------------------------
CREATE TABLE reader (
  id     INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name   VARCHAR(20) NOT NULL COMMENT '姓名',
  sex    VARCHAR(4)  COMMENT '性别：男/女',
  tel    VARCHAR(11) COMMENT '电话',
  cardNo VARCHAR(10) NOT NULL UNIQUE COMMENT '借书证号（唯一）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='读者';

-- ------------------------------------------------------------
-- 3. 图书分类表（C 负责）
-- ------------------------------------------------------------
CREATE TABLE category (
  id      INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name    VARCHAR(20) NOT NULL COMMENT '分类名',
  describ VARCHAR(50) COMMENT '描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类';

-- ------------------------------------------------------------
-- 4. 图书表（C 负责）
-- ------------------------------------------------------------
CREATE TABLE book (
  id         INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  name       VARCHAR(50) NOT NULL COMMENT '书名',
  categoryId INT NOT NULL COMMENT '分类 id（关联 category.id）',
  isbn       VARCHAR(20) COMMENT 'ISBN 号（唯一）',
  author     VARCHAR(20) COMMENT '作者',
  publisher  VARCHAR(30) COMMENT '出版社',
  price      DOUBLE COMMENT '价格',
  stock      INT DEFAULT 0 COMMENT '可借库存',
  status     VARCHAR(4) DEFAULT '在架' COMMENT '在架/下架（下架不可借）',
  UNIQUE KEY uk_book_isbn (isbn),
  KEY idx_book_category (categoryId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书';

-- ------------------------------------------------------------
-- 5. 借阅记录表（D 负责）
-- ------------------------------------------------------------
CREATE TABLE borrow (
  id         INT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  borrowNo   VARCHAR(20) NOT NULL UNIQUE COMMENT '借阅单号：yyyyMMdd+4位序号',
  readerId   INT NOT NULL COMMENT '读者 id（关联 reader.id）',
  bookId     INT NOT NULL COMMENT '图书 id（关联 book.id）',
  borrowDate DATE NOT NULL COMMENT '借出日期',
  dueDate    DATE NOT NULL COMMENT '应还日期（借出+30天）',
  returnDate DATE COMMENT '实际归还日期（NULL=未还）',
  fine       DOUBLE DEFAULT 0 COMMENT '逾期罚款（每天 0.5 元）',
  status     VARCHAR(4) DEFAULT '借出' COMMENT '借出/已还/逾期',
  KEY idx_borrow_reader (readerId),
  KEY idx_borrow_book (bookId),
  KEY idx_borrow_status (status),
  KEY idx_borrow_due (dueDate)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录';

-- ============================================================
-- 演示数据（联调自测用，可整段删除）
-- ============================================================

-- 管理员：默认账号 admin / admin123
INSERT INTO user (id, username, password) VALUES
(1, 'admin', 'admin123'),
(2, 'test',  '123456');

-- 分类
INSERT INTO category (id, name, describ) VALUES
(1, '计算机', '编程语言、软件开发'),
(2, '文学',   '中外文学作品'),
(3, '历史',   '历史研究与通俗历史'),
(4, '科技',   '科普与自然科学'),
(5, '艺术',   '音乐、美术、设计');

-- 图书
INSERT INTO book (id, name, categoryId, isbn, author, publisher, price, stock, status) VALUES
(1, 'Java核心技术 卷I',    1, '9787111213826', '凯·霍斯特曼',   '机械工业出版社',   149.0, 5, '在架'),
(2, 'Head First Java',     1, '9787512334494', 'Kathy Sierra',  '中国电力出版社',   108.0, 3, '在架'),
(3, '深入理解Java虚拟机',  1, '9787111641247', '周志明',         '机械工业出版社',   129.0, 3, '在架'),
(4, '红楼梦',              2, '9787020002207', '曹雪芹',         '人民文学出版社',   59.7,  4, '在架'),
(5, '百年孤独',            2, '9787544253994', '加西亚·马尔克斯','南海出版公司',     39.5,  2, '在架'),
(6, '三体',                2, '9787229030933', '刘慈欣',         '重庆出版社',       93.0,  6, '在架'),
(7, '人类简史',            3, '9787508647357', '尤瓦尔·赫拉利',  '中信出版社',       68.0,  3, '在架'),
(8, '明朝那些事儿',        3, '9787213040322', '当年明月',       '浙江人民出版社',   358.0, 2, '在架'),
(9, '时间简史',            4, '9787535732309', '史蒂芬·霍金',    '湖南科学技术出版社',45.0,  2, '在架'),
(10, '小王子',             2, '9787020042494', '圣埃克苏佩里',   '人民文学出版社',   22.0,  1, '在架');

-- 读者
INSERT INTO reader (id, name, sex, tel, cardNo) VALUES
(1, '张伟', '男', '13800000001', 'R2026001'),
(2, '李娜', '女', '13800000002', 'R2026002'),
(3, '王芳', '女', '13800000003', 'R2026003'),
(4, '刘强', '男', '13800000004', 'R2026004');

-- 借阅记录：日期基于 CURDATE() 计算，保证任何时候导入都符合场景
-- （1 正常借出 / 2 即将到期 / 3 已逾期 / 4 已还且交过罚款）
INSERT INTO borrow (borrowNo, readerId, bookId, borrowDate, dueDate, returnDate, fine, status) VALUES
(CONCAT(DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2  DAY), '%Y%m%d'), '0001'),
 1, 6, DATE_SUB(CURDATE(), INTERVAL 2  DAY), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 2  DAY), INTERVAL 30 DAY), NULL, 0, '借出'),
(CONCAT(DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 29 DAY), '%Y%m%d'), '0002'),
 2, 1, DATE_SUB(CURDATE(), INTERVAL 29 DAY), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 29 DAY), INTERVAL 30 DAY), NULL, 0, '借出'),
(CONCAT(DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 40 DAY), '%Y%m%d'), '0003'),
 3, 7, DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 40 DAY), INTERVAL 30 DAY), NULL, 5.0, '逾期'),
(CONCAT(DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 35 DAY), '%Y%m%d'), '0004'),
 1, 4, DATE_SUB(CURDATE(), INTERVAL 35 DAY), DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 35 DAY), INTERVAL 30 DAY),
 DATE_SUB(CURDATE(), INTERVAL 3 DAY), 1.0, '已还');

-- 自检：导入后应输出 2/5/10/4/4
SELECT 'user'     AS tbl, COUNT(*) AS cnt FROM user
UNION ALL SELECT 'category', COUNT(*) FROM category
UNION ALL SELECT 'book',     COUNT(*) FROM book
UNION ALL SELECT 'reader',   COUNT(*) FROM reader
UNION ALL SELECT 'borrow',   COUNT(*) FROM borrow;

-- ============================================================
-- （可选）在服务器上为项目组创建专用账号
-- 把密码改成小组约定值、去掉每行开头的注释后单独执行；
-- 期末可整体 DROP USER 收回权限。
-- ============================================================
-- CREATE USER 'library'@'%' IDENTIFIED BY '小组约定的密码';
-- GRANT ALL PRIVILEGES ON library.* TO 'library'@'%';
-- FLUSH PRIVILEGES;
