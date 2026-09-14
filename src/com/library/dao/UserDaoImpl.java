package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.library.entity.User;
import com.library.util.JDBCConnection;

/**
 * 管理员 DAO 实现（负责人：A）
 *
 * 本类是 DAO 层的标准写法示范，B/C/D 实现各自 XxxDaoImpl 时请保持相同结构：
 * 1. 连接统一从 JDBCConnection 获取，finally 中统一关闭；
 * 2. 所有 SQL 一律 PreparedStatement 占位符传参，禁止字符串拼接；
 * 3. 每个类自带一个 private mapRow(ResultSet)，把一行结果集组装成实体对象；
 * 4. SQLException 先 printStackTrace 保留现场，方法返回空集合或 null，不向界面抛。
 */
public class UserDaoImpl implements UserDao {

    @Override
    public List<User> getList() {
        String sql = "SELECT id, username, password FROM user ORDER BY id";
        List<User> list = new ArrayList<User>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return list;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT id, username, password FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return null;
    }

    @Override
    public void saveList(List<User> list) {
        String sql = "INSERT INTO user(username, password) VALUES(?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            for (User u : list) {
                if (u.getId() != 0) {
                    continue; // 契约约定：只新增 id=0 的记录
                }
                ps.setString(1, u.getUsername());
                ps.setString(2, u.getPassword());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    @Override
    public void deleteList(List<User> list) {
        String sql = "DELETE FROM user WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            for (User u : list) {
                ps.setInt(1, u.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    @Override
    public void update(User obj) {
        String sql = "UPDATE user SET username = ?, password = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, obj.getUsername());
            ps.setString(2, obj.getPassword());
            ps.setInt(3, obj.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT id, username, password FROM user WHERE username = ? AND password = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return null;
    }

    /** 把一行结果集组装成 User 对象 */
    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        return u;
    }
}
