package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.library.entity.Reader;
import com.library.util.JDBCConnection;

/**
 * 读者 DAO 实现类（板块负责人：B）
 * 全部 SQL 使用 PreparedStatement 占位符传参，防止 SQL 注入（课程考核点）；
 * reader 表的 SQL 只允许出现在本类中（契约：每张表有唯一主拥有者）。
 */
public class ReaderDaoImpl implements ReaderDao {

    /** 查询结果行 → Reader 实体对象 */
    private Reader toReader(ResultSet rs) throws Exception {
        Reader r = new Reader();
        r.setId(rs.getInt("id"));
        r.setName(rs.getString("name"));
        r.setSex(rs.getString("sex"));
        r.setTel(rs.getString("tel"));
        r.setCardNo(rs.getString("cardNo"));
        return r;
    }

    @Override
    public List<Reader> getList() {
        List<Reader> list = new ArrayList<Reader>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            String sql = "SELECT id, name, sex, tel, cardNo FROM reader ORDER BY id";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(toReader(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return list;
    }

    @Override
    public Reader getById(int id) {
        Reader reader = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            String sql = "SELECT id, name, sex, tel, cardNo FROM reader WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                reader = toReader(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return reader;
    }

    @Override
    public void saveList(List<Reader> list) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            String sql = "INSERT INTO reader(name, sex, tel, cardNo) VALUES(?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            for (Reader r : list) {
                ps.setString(1, r.getName());
                ps.setString(2, r.getSex());
                ps.setString(3, r.getTel());
                ps.setString(4, r.getCardNo());
                ps.addBatch(); // 批量新增
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    @Override
    public void deleteList(List<Reader> list) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            String sql = "DELETE FROM reader WHERE id = ?";
            ps = conn.prepareStatement(sql);
            for (Reader r : list) {
                ps.setInt(1, r.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    @Override
    public void update(Reader obj) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = JDBCConnection.getConn();
            String sql = "UPDATE reader SET name = ?, sex = ?, tel = ?, cardNo = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, obj.getName());
            ps.setString(2, obj.getSex());
            ps.setString(3, obj.getTel());
            ps.setString(4, obj.getCardNo());
            ps.setInt(5, obj.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, null);
        }
    }

    @Override
    public Reader getByCardNo(String cardNo) {
        Reader reader = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBCConnection.getConn();
            String sql = "SELECT id, name, sex, tel, cardNo FROM reader WHERE cardNo = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cardNo);
            rs = ps.executeQuery();
            if (rs.next()) {
                reader = toReader(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCConnection.close(conn, ps, rs);
        }
        return reader;
    }
}
