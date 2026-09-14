package com.library.util;

import com.library.dao.BookDao;

/**
 * 图书 DAO 工厂（负责人：A）
 */
public class BookDaoFactory {

    public static BookDao getDao() {
        // 契约层占位：C 完成 BookDaoImpl 后，集成时由 A 改为 return new BookDaoImpl();
        throw new UnsupportedOperationException("BookDaoImpl 尚未实现（负责人：C）");
    }
}
