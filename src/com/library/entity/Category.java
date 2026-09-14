package com.library.entity;

import java.io.Serializable;

/**
 * 图书分类实体（对应 category 表）
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（数据库自增，新增时为 0） */
    private int id;
    /** 分类名，如 计算机 / 文学 */
    private String name;
    /** 描述 */
    private String describ;

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescrib() {
        return describ;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }
}
