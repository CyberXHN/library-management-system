package com.library.dao;

import com.library.entity.Category;

/**
 * 图书分类 DAO（负责人：C）
 */
public interface CategoryDao extends IBaseDao<Category> {

    /** 按分类名查询（图书录入下拉框用），不存在返回 null */
    Category getByName(String name);
}
