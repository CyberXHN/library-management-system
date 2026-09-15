package com.library.pane;

import com.library.dao.BorrowDao;
import com.library.dao.BorrowDaoFactory;
import com.library.entity.Borrow;
import com.library.util.BaseTableModel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticPane extends JPanel {
    private BaseTableModel<Borrow> overdueModel;

    public StatisticPane() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("📊图书借阅排行"));
        JTable rankTable = new JTable();
        topPanel.add(new JScrollPane(rankTable));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("⚠️逾期未还清单"));
        String[] overdueCols = {"借阅单号","读者姓名","书名","借出日期","应还日期","罚款","状态"};
        String[] overdueProps = {"borrowNo","reader.name","book.name","borrowDate","dueDate","fine","status"};
        overdueModel = new BaseTableModel<>(new ArrayList<>(), overdueCols, overdueProps);
        JTable overdueTable = new JTable(overdueModel);
        bottomPanel.add(new JScrollPane(overdueTable));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        split.setDividerLocation(280);
        add(split, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("🔄刷新统计");
        add(refreshBtn, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshData(rankTable));
        refreshData(rankTable);
    }

    private void refreshData(JTable rankTable){
        BorrowDao borrowDao = BorrowDaoFactory.getDao();
        List<Borrow> allBorrow = borrowDao.getList();

        //借阅排行，HashMap统计次数
        Map<String,Integer> countMap = new HashMap<>();
        for(Borrow b : allBorrow){
            if(b.getBook() != null){
                String bookName = b.getBook().getName();
                if(countMap.containsKey(bookName)){
                    countMap.put(bookName, countMap.get(bookName)+1);
                }else{
                    countMap.put(bookName,1);
                }
            }
        }

        //组装排行榜表格数据
        Object[][] data = new Object[countMap.size()][2];
        int index = 0;
        for(Map.Entry<String,Integer> entry : countMap.entrySet()){
            data[index][0] = entry.getKey();
            data[index][1] = entry.getValue();
            index++;
        }
        String[] rankHeader = {"书名","借阅次数"};
        rankTable.setModel(new javax.swing.table.DefaultTableModel(data,rankHeader){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        //筛选逾期记录
        List<Borrow> borrowingList = borrowDao.getBorrowingList();
        List<Borrow> overdueList = new ArrayList<>();
        for(Borrow b : borrowingList){
            if("逾期".equals(b.getStatus())){
                overdueList.add(b);
            }
        }
        overdueModel.setList(overdueList);
    }
}
