package com.library.dao;

import java.util.List;

/**
 * 泛型 DAO 基础接口（契约冻结版）
 * 各实体 DAO 接口均继承本接口，消除各实现类中的重复方法定义与强转
 */
public interface IBaseDao<T> {

    /** 查询全部记录 */
    List<T> getList();

    /** 按主键查询，不存在返回 null */
    T getById(int id);

    /** 批量新增（list 中 id=0 的记录） */
    void saveList(List<T> list);

    /** 批量删除 */
    void deleteList(List<T> list);

    /** 按主键更新整条记录（对象所有字段覆盖写） */
    void update(T obj);
}
