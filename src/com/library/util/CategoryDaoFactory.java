package com.library.util;

import com.library.dao.CategoryDao;
import com.library.dao.CategoryDaoImpl;

/**
 * 图书分类 DAO 工厂（负责人：A）
 */
public class CategoryDaoFactory {

    public static CategoryDao getDao() {
        return new CategoryDaoImpl();
    }
}
