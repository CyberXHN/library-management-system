package com.library.util;

import com.library.dao.BorrowDao;

/**
 * 借阅记录 DAO 工厂（负责人：A）
 */
public class BorrowDaoFactory {

    public static BorrowDao getDao() {
        // 契约层占位：D 完成 BorrowDaoImpl.java 后，集成时由 A 改为 return new BorrowDaoImpl.java();
        throw new UnsupportedOperationException("BorrowDaoImpl.java 尚未实现（负责人：D）");
    }
}
