package com.library.entity;

import java.io.Serializable;

/**
 * 管理员实体（对应 user 表）
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（数据库自增，新增时为 0） */
    private int id;
    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
