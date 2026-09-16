package com.library.pane;

import com.library.dao.BorrowDao;
import com.library.util.BorrowDaoFactory;
import com.library.entity.Borrow;
import com.library.util.BaseTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class StatisticPane extends JPanel {
    private JLabel lblBorrowCount;
    private JLabel lblOverdueCount;
    private JTable tableRank;
    private BaseTableModel<BookRankItem> rankTableModel;

    // 内部实体：给借阅排行表格用
    public static class BookRankItem{
        private String bookName;
        private Integer borrowCount;
        public BookRankItem(String bookName, Integer borrowCount) {
            this.bookName = bookName;
            this.borrowCount = borrowCount;
        }
        public String getBookName() { return bookName; }
        public Integer getBorrowCount() { return borrowCount; }
    }

    public StatisticPane() {
        initUI();
        refreshData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        // 顶部计数区域
        JPanel panelTop = new JPanel(new GridLayout(2,1,10,10));
        lblBorrowCount = new JLabel("借阅总数：0");
        lblOverdueCount = new JLabel("逾期数量：0");
        panelTop.add(lblBorrowCount);
        panelTop.add(lblOverdueCount);
        add(panelTop, BorderLayout.NORTH);

        // 底部：借阅排行表格
        String[] colNames = {"书名", "借阅次数"};
        String[] colProps = {"bookName","borrowCount"};
        rankTableModel = new BaseTableModel<>(new ArrayList<>(), colNames, colProps);
        tableRank = new JTable(rankTableModel);
        JScrollPane scrollPane = new JScrollPane(tableRank);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refreshData() {
        BorrowDao borrowDao = BorrowDaoFactory.getDao();
        List<Borrow> borrowList = borrowDao.getList();
        int total = borrowList.size();
        int overdueNum = 0;
        Map<String, Integer> bookBorrowRank = new HashMap<>();

        for(Borrow borrow : borrowList){
            if("逾期".equals(borrow.getStatus())){
                overdueNum++;
            }
            // 空值保护
            String bookName = borrow.getBook() == null ? "无书籍" : borrow.getBook().getName();
            bookBorrowRank.put(bookName, bookBorrowRank.getOrDefault(bookName,0)+1);
        }

        lblBorrowCount.setText("借阅总数：" + total);
        lblOverdueCount.setText("逾期数量：" + overdueNum);

        // ✅把Map转成列表，灌入BaseTableModel，表格直接展示借阅排行
        List<BookRankItem> rankList = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : bookBorrowRank.entrySet()){
            rankList.add(new BookRankItem(entry.getKey(), entry.getValue()));
        }
        rankTableModel.setList(rankList);
    }
}
