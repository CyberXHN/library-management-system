package com.library.pane;

import com.library.dao.BookDao;
import com.library.dao.BorrowDao;
import com.library.entity.Borrow;
import com.library.util.BookDaoFactory;
import com.library.util.BorrowDaoFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 还书面板（负责人：D）
 * 功能：展示未归还借阅记录 → 选中记录 → 计算逾期罚款 → 确认还书
 */
public class ReturnPane extends JPanel {

    private JTable borrowTable;
    private DefaultTableModel tableModel;
    private List<Borrow> borrowingList;

    private static final double DAILY_FINE = 0.5; // 每日逾期罚款 0.5 元

    public ReturnPane() {
        initUI();
        loadBorrowingList();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("未归还借阅记录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));

        String[] columns = {"ID", "借阅单号", "读者姓名", "借书证号", "书名",
                "借出日期", "应还日期", "状态"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        borrowTable = new JTable(tableModel);
        borrowTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(borrowTable);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnReturn = new JButton("执行还书");
        btnReturn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JButton btnRefresh = new JButton("刷新列表");
        bottomPanel.add(btnReturn);
        bottomPanel.add(btnRefresh);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        btnReturn.addActionListener(e -> doReturn());
        btnRefresh.addActionListener(e -> loadBorrowingList());
    }

    /** 加载未归还借阅记录 */
    private void loadBorrowingList() {
        BorrowDao borrowDao = BorrowDaoFactory.getDao();
        borrowingList = borrowDao.getBorrowingList();
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (borrowingList == null || borrowingList.isEmpty()) {
            return;
        }
        for (Borrow b : borrowingList) {
            Object[] row = {
                    b.getId(),
                    b.getBorrowNo(),
                    b.getReader() != null ? b.getReader().getName() : "",
                    b.getReader() != null ? b.getReader().getCardNo() : "",
                    b.getBook() != null ? b.getBook().getName() : "",
                    b.getBorrowDate() != null ? sdf.format(b.getBorrowDate()) : "",
                    b.getDueDate() != null ? sdf.format(b.getDueDate()) : "",
                    b.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    /** 执行还书 */
    private void doReturn() {
        int selectedRow = borrowTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要归还的记录！");
            return;
        }
        Borrow borrow = borrowingList.get(selectedRow);

        Date today = new Date();
        long diffMs = today.getTime() - borrow.getDueDate().getTime();
        long overdueDays = diffMs / (1000 * 60 * 60 * 24);
        double fine = 0;
        if (overdueDays > 0) {
            fine = overdueDays * DAILY_FINE;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String msg = String.format(
                "借阅单号：%s\n读者：%s\n书名：%s\n应还日期：%s\n逾期天数：%d 天\n应缴罚款：%.2f 元\n\n确认还书？",
                borrow.getBorrowNo(),
                borrow.getReader() != null ? borrow.getReader().getName() : "",
                borrow.getBook() != null ? borrow.getBook().getName() : "",
                borrow.getDueDate() != null ? sdf.format(borrow.getDueDate()) : "",
                Math.max(overdueDays, 0),
                fine
        );
        int opt = JOptionPane.showConfirmDialog(this, msg, "还书确认",
                JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION) {
            return;
        }

        BorrowDao borrowDao = BorrowDaoFactory.getDao();
        boolean ret = borrowDao.returnBook(borrow.getId(), fine);
        if (!ret) {
            JOptionPane.showMessageDialog(this, "还书失败，数据库更新异常！");
            return;
        }

        if (borrow.getBook() != null) {
            BookDao bookDao = BookDaoFactory.getDao();
            bookDao.updateStock(borrow.getBook().getId(), 1);
        }

        JOptionPane.showMessageDialog(this,
                "还书完成！\n逾期罚款：" + String.format("%.2f", fine) + " 元");
        loadBorrowingList();
    }
}
