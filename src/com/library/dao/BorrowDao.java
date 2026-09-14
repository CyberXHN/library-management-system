package com.library.dao;

import java.util.List;

import com.library.entity.Borrow;

/**
 * 借阅记录 DAO（负责人：D）
 */
public interface BorrowDao extends IBaseDao<Borrow> {

    /** 插入一条借阅记录（status='借出'）。不负责扣库存，库存由 BookDao.updateStock 处理 */
    boolean addBorrow(Borrow b);

    /** 还书：更新 returnDate=今天、status='已还'、fine=罚款。不负责恢复库存 */
    boolean returnBook(int borrowId, double fine);

    /** 查询未还记录（status='借出' 或 '逾期'），供还书列表和逾期线程使用 */
    List<Borrow> getBorrowingList();
}
