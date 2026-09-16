package com.library.util;

import com.library.dao.BorrowDao;
import com.library.util.BorrowDaoFactory;
import com.library.entity.Borrow;

import java.util.Calendar;
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

                // 把当前时间 清零时分秒，只保留日期
                Calendar calNow = Calendar.getInstance();
                calNow.setTime(now);
                calNow.set(Calendar.HOUR_OF_DAY,0);
                calNow.set(Calendar.MINUTE,0);
                calNow.set(Calendar.SECOND,0);
                calNow.set(Calendar.MILLISECOND,0);
                Date today = calNow.getTime();

                for (Borrow borrow : borrowList) {
                    Date dueDate = borrow.getDueDate();
                    if(dueDate == null) continue;

                    // 截止日期也清零时分秒
                    Calendar calDue = Calendar.getInstance();
                    calDue.setTime(dueDate);
                    calDue.set(Calendar.HOUR_OF_DAY,0);
                    calDue.set(Calendar.MINUTE,0);
                    calDue.set(Calendar.SECOND,0);
                    calDue.set(Calendar.MILLISECOND,0);
                    Date pureDueDate = calDue.getTime();

                    // ✅核心判断：今天 > 归还日 → 才算逾期！到期当天不逾期
                    if(today.after(pureDueDate)){
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
