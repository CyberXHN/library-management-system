package com.library.dao;

import java.util.List;

import com.library.entity.Book;

/**
 * 图书 DAO（负责人：C）
 */
public interface BookDao extends IBaseDao<Book> {

    /** 按 ISBN 查询（防止重复录入），不存在返回 null */
    Book getByIsbn(String isbn);

    /** 按 书名/作者/出版社 模糊检索（借书选书、统计用），无结果返回空 List */
    List<Book> getByKeyword(String keyword);

    /**
     * 库存增减（借书传 -1，还书传 +1）。
     * 库存不足时返回 false 且不改变数据。
     * 【实现要求】必须保证并发安全：建议用原子 SQL
     *   UPDATE book SET stock = stock + ? WHERE id = ? AND stock + ? >= 0
     * 或方法体 synchronized。book 表 SQL 只允许出现在本方法所属的 BookDaoImpl 中。
     */
    boolean updateStock(int bookId, int delta);
}
