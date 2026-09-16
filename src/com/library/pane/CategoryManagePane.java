package com.library.pane;

import com.library.dao.BookDao;
import com.library.dao.CategoryDao;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.util.BaseTableModel;
import com.library.util.BookDaoFactory;
import com.library.util.CategoryDaoFactory;

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
 * 分类管理面板（模块3，负责人：C）
 *
 * 功能：分类的增删改查（图书分类管理界面）
 * 依赖说明：
 *   1. BaseTableModel 由 E 负责实现，尚未推送；E 完成后本文件即可编译；
 *   2. 集成完成：本面板统一走 CategoryDaoFactory / BookDaoFactory 获取 DAO（A 负责回填）；
 *   3. 删除分类前调用 BookDao 接口检查是否有图书引用（跨模块只调 DAO 接口，契约第七节口径）。
 */
public class CategoryManagePane extends JPanel {

    private CategoryDao categoryDao = CategoryDaoFactory.getDao();
    private BookDao bookDao = BookDaoFactory.getDao();

    private BaseTableModel<Category> tableModel;
    private JTable table;
    private JTextField nameField;
    private JTextField describField;

    public CategoryManagePane() {
        initUI();
        refreshTable();
    }

    private void initUI() {
        // 顶部：输入区 + 操作按钮
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        topPanel.add(new JLabel("分类名："));
        nameField = new JTextField(8);
        topPanel.add(nameField);
        topPanel.add(new JLabel("描述："));
        describField = new JTextField(12);
        topPanel.add(describField);

        JButton addBtn = new JButton("新增");
        JButton updateBtn = new JButton("修改");
        JButton deleteBtn = new JButton("删除");
        topPanel.add(addBtn);
        topPanel.add(updateBtn);
        topPanel.add(deleteBtn);

        // 中间：分类表格（契约统一要求 BaseTableModel + JTable）
        tableModel = new BaseTableModel<>(new ArrayList<Category>(),
                new String[]{"编号", "分类名", "描述"},
                new String[]{"id", "name", "describ"});
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // 事件绑定
        addBtn.addActionListener(e -> addCategory());
        updateBtn.addActionListener(e -> updateCategory());
        deleteBtn.addActionListener(e -> deleteCategory());
        table.getSelectionModel().addListSelectionListener(e -> fillFields());
    }

    /** 新增分类 */
    private void addCategory() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "分类名不能为空");
            return;
        }
        Category c = new Category();
        c.setName(name);
        c.setDescrib(describField.getText().trim());
        categoryDao.saveList(java.util.Collections.singletonList(c));
        JOptionPane.showMessageDialog(this, "新增成功");
        refreshTable();
        nameField.setText("");
        describField.setText("");
    }

    /** 修改选中的分类 */
    private void updateCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要修改的分类");
            return;
        }
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "分类名不能为空");
            return;
        }
        Category c = tableModel.getRow(row);
        c.setName(name);
        c.setDescrib(describField.getText().trim());
        categoryDao.update(c);
        JOptionPane.showMessageDialog(this, "修改成功");
        refreshTable();
    }

    /** 删除选中的分类（有图书引用时禁止删除） */
    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选中要删除的分类");
            return;
        }
        Category c = tableModel.getRow(row);
        // 跨模块只调 DAO 接口：检查是否被图书引用
        List<Book> books = bookDao.getList();
        for (Book b : books) {
            if (b.getCategory() != null && b.getCategory().getId() == c.getId()) {
                JOptionPane.showMessageDialog(this, "该分类下仍有图书（如《" + b.getName() + "》），请先处理图书再删除分类");
                return;
            }
        }
        int ok = JOptionPane.showConfirmDialog(this, "确定删除分类「" + c.getName() + "」吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        categoryDao.deleteList(java.util.Collections.singletonList(c));
        JOptionPane.showMessageDialog(this, "删除成功");
        refreshTable();
    }

    /** 选中行数据回填输入框 */
    private void fillFields() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Category c = tableModel.getRow(row);
        nameField.setText(c.getName());
        describField.setText(c.getDescrib());
    }

    /** 刷新表格数据 */
    public void refreshTable() {
        tableModel.setList(categoryDao.getList());
    }
}
