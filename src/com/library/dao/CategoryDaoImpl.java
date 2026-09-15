package com.library.dao;

import com.library.entity.Category;
import com.library.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书分类 DAO 实现（模块3，负责人：C）
 *
 * 实现接口：com.library.dao.CategoryDao（契约冻结，签名不可改）
 * 数据表：category（id, name, describ），结构见 sql/library.sql
 * 硬约束：所有 SQL 一律 PreparedStatement，禁止字符串拼接
 */
public class CategoryDaoImpl implements CategoryDao {

    /** 查询全部分类 */
    @Override
    public List<Category> getList() {
        String sql = "SELECT id, name, describ FROM category ORDER BY id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Category> list = new ArrayList<>();
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rowToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return list;
    }

    /** 按主键查询，不存在返回 null */
    @Override
    public Category getById(int id) {
        String sql = "SELECT id, name, describ FROM category WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rowToCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return null;
    }

    /** 批量新增（只处理 id = 0 的记录，id 由数据库自增） */
    @Override
    public void saveList(List<Category> list) {
        String sql = "INSERT INTO category (name, describ) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            for (Category c : list) {
                if (c.getId() != 0) {
                    continue;
                }
                ps.setString(1, c.getName());
                ps.setString(2, c.getDescrib());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    /** 批量删除 */
    @Override
    public void deleteList(List<Category> list) {
        String sql = "DELETE FROM category WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            for (Category c : list) {
                ps.setInt(1, c.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    /** 按主键整行更新（所有字段覆盖写） */
    @Override
    public void update(Category obj) {
        String sql = "UPDATE category SET name = ?, describ = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getDescrib());
            ps.setInt(3, obj.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    /** 按分类名查询（图书录入下拉框用），不存在返回 null */
    @Override
    public Category getByName(String name) {
        String sql = "SELECT id, name, describ FROM category WHERE name = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rowToCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return null;
    }

    /** 结果集当前行 → 分类对象 */
    private Category rowToCategory(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDescrib(rs.getString("describ"));
        return c;
    }
}
