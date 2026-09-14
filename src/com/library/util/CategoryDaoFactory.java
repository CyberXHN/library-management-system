package com.library.util;

import com.library.dao.CategoryDao;

/**
 * 图书分类 DAO 工厂（负责人：A）
 */
public class CategoryDaoFactory {

    public static CategoryDao getDao() {
        // 契约层占位：C 完成 CategoryDaoImpl 后，集成时由 A 改为 return new CategoryDaoImpl();
        throw new UnsupportedOperationException("CategoryDaoImpl 尚未实现（负责人：C）");
    }
}
