package com.library.dao;

import com.library.entity.Book;
import com.library.entity.Category;
import com.library.util.JDBCConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书 DAO 实现（模块3，负责人：C）
 *
 * 实现接口：com.library.dao.BookDao（契约冻结，签名不可改）
 * 数据表：book（id, name, categoryId, isbn, author, publisher, price, stock, status），结构见 sql/library.sql
 * 硬约束：
 *   1. 所有 SQL 一律 PreparedStatement，禁止字符串拼接；
 *   2. book 表 SQL 只允许出现在本文件（契约第三节）；
 *   3. updateStock 必须并发安全（原子 SQL，契约第五节明确要求）。
 */
public class BookDaoImpl implements BookDao {

    /** 查询全部图书（LEFT JOIN 分类取分类名，组装进 Book.category） */
    @Override
    public List<Book> getList() {
        String sql = "SELECT b.id, b.name, b.categoryId, b.isbn, b.author, b.publisher, "
                + "b.price, b.stock, b.status, c.name AS categoryName "
                + "FROM book b LEFT JOIN category c ON b.categoryId = c.id ORDER BY b.id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Book> list = new ArrayList<>();
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rowToBook(rs));
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
    public Book getById(int id) {
        String sql = "SELECT b.id, b.name, b.categoryId, b.isbn, b.author, b.publisher, "
                + "b.price, b.stock, b.status, c.name AS categoryName "
                + "FROM book b LEFT JOIN category c ON b.categoryId = c.id WHERE b.id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rowToBook(rs);
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
    public void saveList(List<Book> list) {
        String sql = "INSERT INTO book (name, categoryId, isbn, author, publisher, price, stock, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            for (Book b : list) {
                if (b.getId() != 0) {
                    continue;
                }
                ps.setString(1, b.getName());
                ps.setInt(2, b.getCategory() == null ? 0 : b.getCategory().getId());
                ps.setString(3, b.getIsbn());
                ps.setString(4, b.getAuthor());
                ps.setString(5, b.getPublisher());
                ps.setDouble(6, b.getPrice());
                ps.setInt(7, b.getStock());
                ps.setString(8, b.getStatus());
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
    public void deleteList(List<Book> list) {
        String sql = "DELETE FROM book WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            for (Book b : list) {
                ps.setInt(1, b.getId());
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
    public void update(Book obj) {
        String sql = "UPDATE book SET name = ?, categoryId = ?, isbn = ?, author = ?, publisher = ?, "
                + "price = ?, stock = ?, status = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, obj.getName());
            ps.setInt(2, obj.getCategory() == null ? 0 : obj.getCategory().getId());
            ps.setString(3, obj.getIsbn());
            ps.setString(4, obj.getAuthor());
            ps.setString(5, obj.getPublisher());
            ps.setDouble(6, obj.getPrice());
            ps.setInt(7, obj.getStock());
            ps.setString(8, obj.getStatus());
            ps.setInt(9, obj.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    /** 按 ISBN 查询（防止重复录入），不存在返回 null */
    @Override
    public Book getByIsbn(String isbn) {
        String sql = "SELECT b.id, b.name, b.categoryId, b.isbn, b.author, b.publisher, "
                + "b.price, b.stock, b.status, c.name AS categoryName "
                + "FROM book b LEFT JOIN category c ON b.categoryId = c.id WHERE b.isbn = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setString(1, isbn);
            rs = ps.executeQuery();
            if (rs.next()) {
                return rowToBook(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return null;
    }

    /** 按 书名/作者/出版社 模糊检索（借书选书、统计用），无结果返回空 List */
    @Override
    public List<Book> getByKeyword(String keyword) {
        String sql = "SELECT b.id, b.name, b.categoryId, b.isbn, b.author, b.publisher, "
                + "b.price, b.stock, b.status, c.name AS categoryName "
                + "FROM book b LEFT JOIN category c ON b.categoryId = c.id "
                + "WHERE b.name LIKE ? OR b.author LIKE ? OR b.publisher LIKE ? ORDER BY b.id";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Book> list = new ArrayList<>();
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rowToBook(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return list;
    }

    /**
     * 库存增减（借书传 -1，还书传 +1）。
     * 并发安全：原子 SQL `UPDATE book SET stock = stock + ? WHERE id = ? AND stock + ? >= 0`，
     * 库存不足时影响行数为 0 → 返回 false 且数据不变。
     */
    @Override
    public boolean updateStock(int bookId, int delta) {
        String sql = "UPDATE book SET stock = stock + ? WHERE id = ? AND stock + ? >= 0";
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, delta);
            ps.setInt(2, bookId);
            ps.setInt(3, delta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    /** 结果集当前行 → 图书对象（含分类对象） */
    private Book rowToBook(ResultSet rs) throws SQLException {
        Book b = new Book();
        b.setId(rs.getInt("id"));
        b.setName(rs.getString("name"));
        Category c = new Category();
        c.setId(rs.getInt("categoryId"));
        c.setName(rs.getString("categoryName"));
        b.setCategory(c);
        b.setIsbn(rs.getString("isbn"));
        b.setAuthor(rs.getString("author"));
        b.setPublisher(rs.getString("publisher"));
        b.setPrice(rs.getDouble("price"));
        b.setStock(rs.getInt("stock"));
        b.setStatus(rs.getString("status"));
        return b;
    }
}
