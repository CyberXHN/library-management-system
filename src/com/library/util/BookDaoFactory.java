package com.library.util;

import com.library.dao.BookDao;
import com.library.dao.BookDaoImpl;

/**
 * 图书 DAO 工厂（负责人：A）
 */
public class BookDaoFactory {

    public static BookDao getDao() {
        return new BookDaoImpl();
    }
}
