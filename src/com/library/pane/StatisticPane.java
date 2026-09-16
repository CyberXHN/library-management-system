package com.library.pane;

import com.library.dao.BorrowDao;
import com.library.dao.BorrowDaoFactory;
import com.library.entity.Borrow;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticPane extends JPanel {
    private JLabel lblBorrowCount;
    private JLabel lblOverdueCount;

    public StatisticPane() {
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new GridLayout(2,1,10,10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        lblBorrowCount = new JLabel("借阅总数：0");
        lblOverdueCount = new JLabel("逾期数量：0");
        add(lblBorrowCount);
        add(lblOverdueCount);
    }

    public void refreshData() {
        BorrowDao borrowDao = BorrowDaoFactory.getDao();
        List<Borrow> borrowList = borrowDao.getList();
        int total = borrowList.size();
        int overdueNum = 0;
        Map<String, Integer> bookBorrowRank = new HashMap<>();

        for(Borrow borrow : borrowList){
            //统计逾期
            if("逾期".equals(borrow.getStatus())){
                overdueNum++;
            }
            // ✅修复：空值保护，book为null时显示“无书籍”，防止空指针
            String bookName = borrow.getBook() == null ? "无书籍" : borrow.getBook().getName();
            bookBorrowRank.put(bookName, bookBorrowRank.getOrDefault(bookName,0)+1);
        }

        lblBorrowCount.setText("借阅总数：" + total);
        lblOverdueCount.setText("逾期数量：" + overdueNum);
        // bookBorrowRank 借阅排行Map，表格展示后续迭代实现
    }
}
