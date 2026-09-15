package com.library.dao;

import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.Reader;
import com.library.util.JDBCConnection;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 借阅管理 DAO 实现类（负责人：D）
 * 仅操作 borrow 表，关联查询 reader、book 表填充实体
 * 不直接修改 book、reader 表数据
 */
public class BorrowDaoImpl implements BorrowDao {

    // ========== IBaseDao 接口实现 ==========

    @Override
    public List<Borrow> getList() {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT b.*, r.id AS rid, r.name AS rname, r.cardNo, " +
                "k.id AS bid, k.title, k.author, k.isbn " +
                "FROM borrow b " +
                "LEFT JOIN reader r ON b.readerId = r.id " +
                "LEFT JOIN book k ON b.bookId = k.id " +
                "ORDER BY b.id DESC";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rowToBorrow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Borrow getById(int id) {
        String sql = "SELECT b.*, r.id AS rid, r.name AS rname, r.cardNo, " +
                "k.id AS bid, k.title, k.author, k.isbn " +
                "FROM borrow b " +
                "LEFT JOIN reader r ON b.readerId = r.id " +
                "LEFT JOIN book k ON b.bookId = k.id " +
                "WHERE b.id = ?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rowToBorrow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveList(List<Borrow> list) {
        if (list == null || list.isEmpty()) return;
        String sql = "INSERT INTO borrow(borrowNo, readerId, bookId, borrowDate, dueDate, returnDate, fine, status) " +
                "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Borrow b : list) {
                pstmt.setString(1, b.getBorrowNo());
                pstmt.setInt(2, b.getReader().getId());
                pstmt.setInt(3, b.getBook().getId());
                pstmt.setTimestamp(4, new Timestamp(b.getBorrowDate().getTime()));
                pstmt.setTimestamp(5, new Timestamp(b.getDueDate().getTime()));
                pstmt.setTimestamp(6, b.getReturnDate() != null ?
                        new Timestamp(b.getReturnDate().getTime()) : null);
                pstmt.setDouble(7, b.getFine());
                pstmt.setString(8, b.getStatus());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteList(List<Borrow> list) {
        if (list == null || list.isEmpty()) return;
        StringBuilder sql = new StringBuilder("DELETE FROM borrow WHERE id IN (");
        for (int i = 0; i < list.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
        }
        sql.append(")");
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < list.size(); i++) {
                pstmt.setInt(i + 1, list.get(i).getId());
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Borrow b) {
        String sql = "UPDATE borrow SET borrowNo=?, readerId=?, bookId=?, " +
                "borrowDate=?, dueDate=?, returnDate=?, fine=?, status=? WHERE id=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, b.getBorrowNo());
            pstmt.setInt(2, b.getReader().getId());
            pstmt.setInt(3, b.getBook().getId());
            pstmt.setTimestamp(4, new Timestamp(b.getBorrowDate().getTime()));
            pstmt.setTimestamp(5, new Timestamp(b.getDueDate().getTime()));
            pstmt.setTimestamp(6, b.getReturnDate() != null ?
                    new Timestamp(b.getReturnDate().getTime()) : null);
            pstmt.setDouble(7, b.getFine());
            pstmt.setString(8, b.getStatus());
            pstmt.setInt(9, b.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== BorrowDao 扩展接口实现 ==========

    @Override
    public boolean addBorrow(Borrow b) {
        String sql = "INSERT INTO borrow(borrowNo, readerId, bookId, borrowDate, dueDate, fine, status) " +
                "VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, b.getBorrowNo());
            pstmt.setInt(2, b.getReader().getId());
            pstmt.setInt(3, b.getBook().getId());
            pstmt.setTimestamp(4, new Timestamp(b.getBorrowDate().getTime()));
            pstmt.setTimestamp(5, new Timestamp(b.getDueDate().getTime()));
            pstmt.setDouble(6, b.getFine());
            pstmt.setString(7, b.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean returnBook(int borrowId, double fine) {
        String sql = "UPDATE borrow SET returnDate=?, fine=?, status='已还' WHERE id=?";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setDouble(2, fine);
            pstmt.setInt(3, borrowId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Borrow> getBorrowingList() {
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT b.*, r.id AS rid, r.name AS rname, r.cardNo, " +
                "k.id AS bid, k.title, k.author, k.isbn " +
                "FROM borrow b " +
                "LEFT JOIN reader r ON b.readerId = r.id " +
                "LEFT JOIN book k ON b.bookId = k.id " +
                "WHERE b.status IN ('借出','逾期') " +
                "ORDER BY b.borrowDate DESC";
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(rowToBorrow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ========== 工具方法 ==========

    /**
     * 生成借阅单号：yyyyMMdd + 4位当日自增序号
     */
    public String generateBorrowNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        String prefix = "JY" + datePart;
        String sql = "SELECT COUNT(*) AS cnt FROM borrow WHERE borrowNo LIKE ?";
        int count = 0;
        try (Connection conn = JDBCConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prefix + String.format("%04d", count + 1);
    }

    /**
     * 将 ResultSet 一行映射为 Borrow 对象（含关联 Reader、Book）
     */
    private Borrow rowToBorrow(ResultSet rs) throws SQLException {
        Borrow b = new Borrow();
        b.setId(rs.getInt("id"));
        b.setBorrowNo(rs.getString("borrowNo"));
        b.setBorrowDate(rs.getTimestamp("borrowDate"));
        b.setDueDate(rs.getTimestamp("dueDate"));
        b.setReturnDate(rs.getTimestamp("returnDate"));
        b.setFine(rs.getDouble("fine"));
        b.setStatus(rs.getString("status"));

        // 关联读者
        Reader reader = new Reader();
        reader.setId(rs.getInt("rid"));
        reader.setName(rs.getString("rname"));
        reader.setCardNo(rs.getString("cardNo"));
        b.setReader(reader);

        // 关联图书
        Book book = new Book();
        book.setId(rs.getInt("bid"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        b.setBook(book);

        return b;
    }
}
