package com.library.util;

import com.library.util.BorrowDaoFactory;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class OverdueTimer {

    private Timer timer;

    public void startCheck(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 【注释：D模块暂未实现该接口，暂时不调用】
                    // BorrowDaoFactory.getDao().scanOverdue();
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,"逾期扫描异常："+e.getMessage());
                    });
                }
            }
        },0,1000*60*30);
    }

    public void stopCheck(){
        if(timer!=null){
            timer.cancel();
        }
    }
}
