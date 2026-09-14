package com.library.dao;

import com.library.entity.User;

/**
 * 管理员 DAO（负责人：A）
 */
public interface UserDao extends IBaseDao<User> {

    /** 登录校验：用户名密码都匹配返回 User 对象，否则返回 null */
    User findByUsernameAndPassword(String username, String password);
}
