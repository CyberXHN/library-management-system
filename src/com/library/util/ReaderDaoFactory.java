package com.library.util;

import com.library.dao.ReaderDao;
import com.library.dao.ReaderDaoImpl;

/**
 * 读者 DAO 工厂（负责人：A）
 */
public class ReaderDaoFactory {

    public static ReaderDao getDao() {
        return new ReaderDaoImpl();
    }
}
