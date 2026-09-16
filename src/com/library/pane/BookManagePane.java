package com.library.pane;

import com.library.dao.BookDao;
import com.library.dialog.BookAddDialog;
import com.library.entity.Book;
import com.library.util.BaseTableModel;
import com.library.util.BookDaoFactory;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书管理面板（模块3，负责人：C）
 *
 * 功能：图书的增删改查、模糊检索、上下架管理
 * 依赖说明：
 *   1. BaseTableModel 由 E 负责实现，尚未推送；E 完成后本文件即可编译；
 *   2. 集成完成：本面板统一走 BookDaoFactory 获取 DAO（A 负责回填）；
 *   3. 图书新增/编辑弹窗见 BookAddDialog（同属模块3）。
 */
public class BookManagePane extends JPanel {

    private BookDao bookDao = BookDaoFactory.getDao();

    private BaseTableModel<Book> tableModel;
    private JTable table;
    private JTextField keywordField;

    public BookManagePane() {
        initUI();
        refreshTable();
    }

    private void initUI() {
        // 顶部：检索区 + 操作按钮
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        topPanel.add(new JLabel("关键字："));
        keywordField = new JTextField(12);
        topPanel.add(keywordField);

        JButton searchBtn = new JButton("查询");
        JButton addBtn = new JButton("新增图书");
        JButton updateBtn = new JButton("修改");
        JButton offBtn = new JButton("下架");
        JButton onBtn = new JButton("上架");
        JButton refreshBtn = new JButton("刷新");
        topPanel.add(searchBtn);
        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        topPanel.add(offBtn);
        topPanel.add(onBtn);
        topPanel.add(refreshBtn);

        // 中间：图书表格（契约统一要求 BaseTableModel + JTable，支持 category.name 嵌套属性）
        tableModel = new BaseTableModel<>(new ArrayList<Book>(),
                new String[]{"编号", "书名", "分类", "ISBN", "作者", "出版社", "价格", "库存", "状态"},
                new String[]{"id", "name", "category.name", "isbn", "author", "publisher", "price", "stock", "status"});
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 事件绑定
        searchBtn.addActionListener(e -> search());
        addBtn.addActionListener(e -> new BookAddDialog(this).setVisible(true));
        updateBtn.addActionListener(e -> editSelected());
        offBtn.addActionListener(e -> changeStatus("下架"));
        onBtn.addActionListener(e -> changeStatus("上架"));
        refreshBtn.addActionListener(e -> refreshTable());
    }

    /** 按 书名/作者/出版社 关键字检索，关键字为空时显示全部 */
    private void search() {
        String keyword = keywordField.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable();
            return;
        }
        tableModel.setList(bookDao.getByKeyword(keyword));
    }

    /** 编辑选中的图书（打开 BookAddDialog 编辑模式） */
    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的图书");
            return;
        }
        Book book = tableModel.getRow(row);
        new BookAddDialog(this, book).setVisible(true);
    }

    /** 上下架：修改选中图书的 status 后整行更新 */
    private void changeStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要" + status + "的图书");
            return;
        }
        Book book = tableModel.getRow(row);
        book.setStatus(status);
        bookDao.update(book);
        JOptionPane.showMessageDialog(this, "已" + status);
        refreshTable();
    }

    /** 刷新表格数据（新增/修改/删除后由 BookAddDialog 或本类调用） */
    public void refreshTable() {
        tableModel.setList(bookDao.getList());
    }
}
