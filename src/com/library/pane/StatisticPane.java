package com.library.pane;

import javax.swing.*;
import java.awt.*;

public class StatisticPane extends JPanel {

    private JLabel lblBorrowCount;
    private JLabel lblOverdueCount;

    public StatisticPane() {
        initUI();
    }

    private void initUI() {
        setLayout(new GridLayout(2, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblBorrowCount = new JLabel("借阅总数：126");
        lblOverdueCount = new JLabel("逾期数量：14");

        add(lblBorrowCount);
        add(lblOverdueCount);
    }

    public void refreshData() {
        // 当前使用模拟数据展示界面，后续对接真实 DAO 后再改为数据库查询
        lblBorrowCount.setText("借阅总数：126");
        lblOverdueCount.setText("逾期数量：14");
    }
}
