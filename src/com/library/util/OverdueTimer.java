package com.library.util;

import com.library.dao.BorrowDao;
import com.library.entity.Borrow;
import java.util.Date;
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
                    // 判断：截止日期早于当前时间 → 逾期
                    if (borrow.getDueDate().before(now)) {
                        borrow.setStatus("逾期");
                        borrowDao.update(borrow);
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
