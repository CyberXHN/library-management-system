package com.library.util;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class OverdueTimer {

    private Timer timer;

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 逾期扫描逻辑暂未实现，等待 DAO 接口完成后再接入
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null, "逾期扫描异常：" + e.getMessage());
                    });
                }
            }
        }, 0, 1000 * 60 * 30);
    }

    public void stopCheck() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
