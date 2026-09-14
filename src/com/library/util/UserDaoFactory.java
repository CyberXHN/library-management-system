package com.library.util;

import com.library.dao.UserDao;
import com.library.dao.UserDaoImpl;

/**
 * 管理员 DAO 工厂（负责人：A）
 */
public class UserDaoFactory {

    public static UserDao getDao() {
        return new UserDaoImpl();
    }
}
