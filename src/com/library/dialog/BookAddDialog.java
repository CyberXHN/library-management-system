package com.library.dialog;

import com.library.dao.BookDao;
import com.library.dao.BookDaoImpl;
import com.library.dao.CategoryDao;
import com.library.dao.CategoryDaoImpl;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.pane.BookManagePane;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.List;

/**
 * 图书新增/编辑对话框（模块3，负责人：C）
 *
 * 用法：
 *   新增：new BookAddDialog(bookManagePane).setVisible(true);
 *   编辑：new BookAddDialog(bookManagePane, 选中Book对象).setVisible(true);
 * 保存成功后自动通知 BookManagePane 刷新表格。
 */
public class BookAddDialog extends JDialog {

    private BookDao bookDao = new BookDaoImpl();
    private CategoryDao categoryDao = new CategoryDaoImpl();

    private BookManagePane pane;
    private Book editBook;   // 非 null = 编辑模式

    private JTextField nameField;
    private JComboBox<Category> categoryCombo;
    private JTextField isbnField;
    private JTextField authorField;
    private JTextField publisherField;
    private JTextField priceField;
    private JSpinner stockSpinner;
    private JComboBox<String> statusCombo;

    /** 新增模式 */
    public BookAddDialog(BookManagePane pane) {
        this(pane, null);
    }

    /** 编辑模式 */
    public BookAddDialog(BookManagePane pane, Book editBook) {
        super(resolveOwner(pane), editBook == null ? "新增图书" : "修改图书", true);
        this.pane = pane;
        this.editBook = editBook;
        initUI();
        if (editBook != null) {
            fillFields(editBook);
        }
        pack();
        setLocationRelativeTo(getOwner());
    }

    /** 从面板解析父窗口，未挂载时返回 null（对话框仍可显示） */
    private static Frame resolveOwner(BookManagePane pane) {
        Component ancestor = SwingUtilities.getWindowAncestor(pane);
        return ancestor instanceof Frame ? (Frame) ancestor : null;
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 8, 6));
        formPanel.add(new JLabel("书名："));
        nameField = new JTextField(16);
        formPanel.add(nameField);

        formPanel.add(new JLabel("分类："));
        categoryCombo = new JComboBox<>();
        loadCategories();
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    setText(((Category) value).getName());
                }
                return this;
            }
        });
        formPanel.add(categoryCombo);

        formPanel.add(new JLabel("ISBN："));
        isbnField = new JTextField(16);
        formPanel.add(isbnField);

        formPanel.add(new JLabel("作者："));
        authorField = new JTextField(16);
        formPanel.add(authorField);

        formPanel.add(new JLabel("出版社："));
        publisherField = new JTextField(16);
        formPanel.add(publisherField);

        formPanel.add(new JLabel("价格（元）："));
        priceField = new JTextField(16);
        formPanel.add(priceField);

        formPanel.add(new JLabel("库存："));
        stockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 99999, 1));
        formPanel.add(stockSpinner);

        formPanel.add(new JLabel("状态："));
        statusCombo = new JComboBox<>(new String[]{"在架", "下架"});
        formPanel.add(statusCombo);

        // 底部：确定 / 取消
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton okBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        okBtn.addActionListener(e -> save());
        cancelBtn.addActionListener(e -> dispose());
    }

    /** 从 CategoryDao 加载分类下拉数据 */
    private void loadCategories() {
        List<Category> categories = categoryDao.getList();
        for (Category c : categories) {
            categoryCombo.addItem(c);
        }
        if (categories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "暂无分类，请先到「分类管理」新增分类");
        }
    }

    /** 编辑模式：把图书数据回填到表单 */
    private void fillFields(Book book) {
        nameField.setText(book.getName());
        selectCategory(book.getCategory());
        isbnField.setText(book.getIsbn());
        authorField.setText(book.getAuthor());
        publisherField.setText(book.getPublisher());
        priceField.setText(String.valueOf(book.getPrice()));
        stockSpinner.setValue(book.getStock());
        statusCombo.setSelectedItem(book.getStatus() == null ? "在架" : book.getStatus());
    }

    /** 按 id 选中下拉框中的对应分类 */
    private void selectCategory(Category target) {
        if (target == null) {
            return;
        }
        for (int i = 0; i < categoryCombo.getItemCount(); i++) {
            if (categoryCombo.getItemAt(i).getId() == target.getId()) {
                categoryCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    /** 保存：校验 → 新增（saveList）或修改（update） */
    private void save() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "书名不能为空");
            return;
        }
        Category category = (Category) categoryCombo.getSelectedItem();
        if (category == null) {
            JOptionPane.showMessageDialog(this, "请选择分类");
            return;
        }
        String isbn = isbnField.getText().trim();
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "价格必须是数字");
            return;
        }

        Book book = editBook != null ? editBook : new Book();
        book.setName(name);
        book.setCategory(category);
        book.setIsbn(isbn);
        book.setAuthor(authorField.getText().trim());
        book.setPublisher(publisherField.getText().trim());
        book.setPrice(price);
        book.setStock((Integer) stockSpinner.getValue());
        book.setStatus((String) statusCombo.getSelectedItem());

        if (editBook == null) {
            // 新增：ISBN 去重（契约：防止重复录入）
            if (!isbn.isEmpty() && bookDao.getByIsbn(isbn) != null) {
                JOptionPane.showMessageDialog(this, "ISBN「" + isbn + "」已存在，请勿重复录入");
                return;
            }
            bookDao.saveList(Collections.singletonList(book));
            JOptionPane.showMessageDialog(this, "新增成功");
        } else {
            bookDao.update(book);
            JOptionPane.showMessageDialog(this, "修改成功");
        }
        dispose();
        pane.refreshTable();
    }
}
