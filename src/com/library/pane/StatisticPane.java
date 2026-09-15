package com.library.pane;

import com.library.util.BorrowDaoFactory;

import javax.swing.*;
import java.awt.*;

public class StatisticPane extends JPanel {

    private JLabel lblBorrowCount;
    private JLabel lblOverdueCount;

    public StatisticPane(){
        initUI();
    }

    private void initUI(){
        setLayout(new GridLayout(2,1,10,10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        lblBorrowCount = new JLabel("借阅总数：--");
        lblOverdueCount = new JLabel("逾期数量：--");

        add(lblBorrowCount);
        add(lblOverdueCount);

        refreshData();
    }

    public void refreshData(){
        try {
            int borrowTotal = BorrowDaoFactory.getDao().getBorrowTotal();
            int overdueTotal = BorrowDaoFactory.getDao().getOverdueCount();
            lblBorrowCount.setText("借阅总数："+borrowTotal);
            lblOverdueCount.setText("逾期数量："+overdueTotal);
        }catch (Exception e){
            lblBorrowCount.setText("借阅总数：获取失败");
            lblOverdueCount.setText("逾期数量：获取失败");
        }
    }
}
