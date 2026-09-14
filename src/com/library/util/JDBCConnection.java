package com.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具（教材同款结构，库名改为 library）
 *
 * 注意：USER / PWD 按各自本地环境修改，改完不要提交（见协作规范第 7 节）；
 * 连接服务器时把 localhost 换成服务器 IP。
 */
public class JDBCConnection {

    /** MySQL 8.x 驱动；若用 5.x 驱动改为 com.mysql.jdbc.Driver */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    /** 如遇时区报错，在 URL 末尾追加 &serverTimezone=Asia/Shanghai */
    private static final String URL = "jdbc:mysql://localhost:3306/library?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PWD = "123456";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 获取数据库连接 */
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PWD);
    }

    /** 依次判空关闭 rs、st、conn */
    public static void close(Connection conn, Statement st, ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (st != null) st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
