package com.library.pane;

import com.library.dao.BookDao;
import com.library.dao.BorrowDao;
import com.library.dao.ReaderDao;
import com.library.entity.Book;
import com.library.entity.Borrow;
import com.library.entity.Reader;
import com.library.util.BookDaoFactory;
import com.library.util.BorrowDaoFactory;
import com.library.util.ReaderDaoFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

/**
 * 借书面板（负责人：D）
 * 功能：输入借书证号校验读者 → 搜索图书 → 选中图书 → 确认借书
 */
public class BorrowPane extends JPanel {

    private JTextField txtCardNo;
    private JTextField txtKeyword;
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JLabel lblReaderInfo;

    private Reader currentReader;
    private List<Book> currentBookList;

    public BorrowPane() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 顶部：读者校验区
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("读者信息"));
        topPanel.add(new JLabel("借书证号："));
        txtCardNo = new JTextField(15);
        topPanel.add(txtCardNo);
        JButton btnCheckReader = new JButton("校验读者");
        topPanel.add(btnCheckReader);
        lblReaderInfo = new JLabel("未校验读者");
        lblReaderInfo.setForeground(Color.GRAY);
        topPanel.add(lblReaderInfo);

        // 中部：图书搜索区
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("图书搜索"));
        searchPanel.add(new JLabel("关键字："));
        txtKeyword = new JTextField(20);
        searchPanel.add(txtKeyword);
        JButton btnSearch = new JButton("搜索图书");
        searchPanel.add(btnSearch);

        String[] columns = {"ID", "书名", "作者", "ISBN", "库存"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(bookTable);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // 底部：操作按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnBorrow = new JButton("确认借书");
        btnBorrow.setFont(new Font("微软雅黑", Font.BOLD, 14));
        JButton btnReset = new JButton("重置");
        bottomPanel.add(btnBorrow);
        bottomPanel.add(btnReset);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // 事件绑定
        btnCheckReader.addActionListener(e -> checkReader());
        btnSearch.addActionListener(e -> searchBooks());
        btnBorrow.addActionListener(e -> doBorrow());
        btnReset.addActionListener(e -> resetForm());
    }

    /** 校验读者 */
    private void checkReader() {
        String cardNo = txtCardNo.getText().trim();
        if (cardNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入借书证号！");
            return;
        }
        ReaderDao readerDao = ReaderDaoFactory.getDao();
        currentReader = readerDao.getByCardNo(cardNo);
        if (currentReader == null) {
            lblReaderInfo.setText("读者不存在，请检查借书证号");
            lblReaderInfo.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "读者不存在，请检查借书证号！");
        } else {
            lblReaderInfo.setText("读者：" + currentReader.getName() + "（证号：" + cardNo + "）");
            lblReaderInfo.setForeground(new Color(0, 128, 0));
        }
    }

    /** 搜索图书 */
    private void searchBooks() {
        String keyword = txtKeyword.getText().trim();
        BookDao bookDao = BookDaoFactory.getDao();
        currentBookList = bookDao.getByKeyword(keyword);
        tableModel.setRowCount(0);
        if (currentBookList == null || currentBookList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到匹配的图书！");
            return;
        }
        for (Book book : currentBookList) {
            Object[] row = {
                    book.getId(),
                    book.getName(),
                    book.getAuthor(),
                    book.getIsbn(),
                    book.getStock()
            };
            tableModel.addRow(row);
        }
    }

    /** 执行借书 */
    private void doBorrow() {
        if (currentReader == null) {
            JOptionPane.showMessageDialog(this, "请先校验读者！");
            return;
        }
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请选择要借阅的图书！");
            return;
        }
        Book selectedBook = currentBookList.get(selectedRow);

        BookDao bookDao = BookDaoFactory.getDao();
        boolean stockOk = bookDao.updateStock(selectedBook.getId(), -1);
        if (!stockOk) {
            JOptionPane.showMessageDialog(this, "图书库存不足，无法借阅！");
            return;
        }

        Borrow borrow = new Borrow();
        borrow.setReader(currentReader);
        borrow.setBook(selectedBook);
        borrow.setBorrowDate(new Date());
        long dueTime = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000;
        borrow.setDueDate(new Date(dueTime));
        borrow.setFine(0);
        borrow.setStatus("借出");

        // 使用工厂获取DAO，不再直接new实现类；借阅单号由DAO内部生成
        BorrowDao borrowDao = BorrowDaoFactory.getDao();
        boolean res = borrowDao.addBorrow(borrow);
        if (res) {
            JOptionPane.showMessageDialog(this,
                    "借书成功！\n借阅单号：" + borrow.getBorrowNo() +
                            "\n应还日期：" + borrow.getDueDate());
            searchBooks();
        } else {
            bookDao.updateStock(selectedBook.getId(), 1);
            JOptionPane.showMessageDialog(this, "借书失败，请重试！");
        }
    }

    /** 重置表单 */
    private void resetForm() {
        txtCardNo.setText("");
        txtKeyword.setText("");
        lblReaderInfo.setText("未校验读者");
        lblReaderInfo.setForeground(Color.GRAY);
        currentReader = null;
        currentBookList = null;
        tableModel.setRowCount(0);
    }
}
