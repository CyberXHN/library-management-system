package com.library.util;

import com.library.dao.BorrowDao;
import com.library.util.BorrowDaoFactory;
import com.library.entity.Borrow;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OverdueTimer {
    private static Timer timer;

    public static void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BorrowDao borrowDao = BorrowDaoFactory.getDao();
                List<Borrow> borrowList = borrowDao.getBorrowingList();
                Date now = new Date();

                for (Borrow borrow : borrowList) {
                    //到期当天不算逾期，严格大于dueDate才算逾期
                    if (borrow.getDueDate().compareTo(now) < 0) {
                        //只有非逾期状态才更新，避免重复写库
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
