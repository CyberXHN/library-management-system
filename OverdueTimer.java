package com.library.util;

import com.library.dao.BorrowDao;
import com.library.dao.BorrowDaoFactory;
import com.library.entity.Borrow;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OverdueTimer {
    private static Timer timer;

    public static void start() {
        if(timer != null) return;
        timer = new Timer();
        //每60秒执行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                BorrowDao borrowDao = BorrowDaoFactory.getDao();
                List<Borrow> borrowingList = borrowDao.getBorrowingList();
                Calendar today = Calendar.getInstance();

                for(Borrow b : borrowingList){
                    //未归还，应还日期早于今天，设置逾期
                    if(b.getReturnDate() == null && b.getDueDate().before(today.getTime())){
                        if(!"逾期".equals(b.getStatus())){
                            b.setStatus("逾期");
                            borrowDao.update(b);
                        }
                    }
                }
            }
        },0, 60*1000);
    }

    public static void stop(){
        if(timer != null){
            timer.cancel();
        }
    }
}
