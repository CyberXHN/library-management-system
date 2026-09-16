package com.library.util;

import com.library.dao.BorrowDao;
import com.library.dao.BorrowDaoImpl;

/**
 * 借阅记录 DAO 工厂（负责人：A）
 */
public class BorrowDaoFactory {

    public static BorrowDao getDao() {
        return new BorrowDaoImpl();
    }
}
