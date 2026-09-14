package com.library.dao;

import com.library.entity.Reader;

/**
 * 读者 DAO（负责人：B）
 */
public interface ReaderDao extends IBaseDao<Reader> {

    /** 按借书证号查询（借书选读者、办证防重复），不存在返回 null */
    Reader getByCardNo(String cardNo);
}
