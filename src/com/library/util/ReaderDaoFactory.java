package com.library.util;

import com.library.dao.ReaderDao;

/**
 * 读者 DAO 工厂（负责人：A）
 */
public class ReaderDaoFactory {

    public static ReaderDao getDao() {
        // 契约层占位：B 完成 ReaderDaoImpl 后，集成时由 A 改为 return new ReaderDaoImpl();
        throw new UnsupportedOperationException("ReaderDaoImpl 尚未实现（负责人：B）");
    }
}
