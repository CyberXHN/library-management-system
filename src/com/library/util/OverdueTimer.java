package com.library.util;

import com.library.dao.BorrowDao;
import com.library.dao.BorrowDaoFactory;
import com.library.entity.Borrow;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OverdueTimer {
    private static Timer timer;

    // 契约入口：public static void start()
    public static void start() {
        timer = new Timer();
        // 每60秒执行一次
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BorrowDao borrowDao = BorrowDaoFactory.getDao();
                List<Borrow> borrowList = borrowDao.getBorrowingList();
                Date now = new Date();

                for (Borrow borrow : borrowList) {
                    // 修复：到期当天不算逾期，超过截止日期才标记逾期
                    if (borrow.getDueDate().compareTo(now) < 0) {
                        // 修复：只有非逾期状态才更新，避免重复update数据库
                        if (!"逾期".equals(borrow.getStatus())) {
                            borrow.setStatus("逾期");
                            borrowDao.update(borrow);
                        }
                    }
                }
            }
        }, 0, 1000 * 60);
    }

    public static void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
