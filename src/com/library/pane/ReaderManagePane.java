package com.library.pane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.library.dao.ReaderDao;
import com.library.dialog.ReaderAddDialog;
import com.library.entity.Reader;
import com.library.util.BaseTableModel;
import com.library.util.ReaderDaoFactory;

/**
 * 读者管理面板（板块负责人：B）
 * 教材风格：上方按钮栏 + 中间 JTable 列表。
 * 表格统一使用 BaseTableModel<Reader>（反射取值），不手写 TableModel。
 */
public class ReaderManagePane extends JPanel {

    private static final long serialVersionUID = 1L;

    private ReaderDao readerDao = ReaderDaoFactory.getDao();

    private JTable table;
    private BaseTableModel<Reader> tableModel;
    private JTextField txtCardNo; // 按借书证号查询

    /** 列名与实体属性路径一一对应（BaseTableModel 反射取值） */
    private static final String[] COLUMNS = {"编号", "姓名", "性别", "电话", "借书证号"};
    private static final String[] PROPS = {"id", "name", "sex", "tel", "cardNo"};

    public ReaderManagePane() {
        setLayout(new BorderLayout());
        add(initButtonBar(), BorderLayout.NORTH);
        add(initTable(), BorderLayout.CENTER);
        refreshTable();
    }

    /** 上方按钮栏：新增 / 编辑 / 删除 / 按证号查询 / 刷新 */
    private JPanel initButtonBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton btnAdd = new JButton("新增读者");
        JButton btnEdit = new JButton("编辑");
        JButton btnDelete = new JButton("删除");
        JButton btnQuery = new JButton("按证号查询");
        JButton btnRefresh = new JButton("刷新");
        txtCardNo = new JTextField(10);

        bar.add(btnAdd);
        bar.add(btnEdit);
        bar.add(btnDelete);
        bar.add(txtCardNo);
        bar.add(btnQuery);
        bar.add(btnRefresh);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openDialog(null);
            }
        });
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onEdit();
            }
        });
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });
        btnQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onQueryByCardNo();
            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTable();
            }
        });
        return bar;
    }

    /** 中间表格：BaseTableModel + JTable */
    private JScrollPane initTable() {
        tableModel = new BaseTableModel<Reader>(null, COLUMNS, PROPS);
        table = new JTable(tableModel);
        // 双击行 = 编辑
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onEdit();
                }
            }
        });
        return new JScrollPane(table);
    }

    /** 重新查询全部读者并刷新表格 */
    private void refreshTable() {
        tableModel.setList(readerDao.getList());
    }

    /** 打开新增 / 编辑对话框，保存成功后刷新表格 */
    private void openDialog(Reader reader) {
        ReaderAddDialog dialog = new ReaderAddDialog(null, reader);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshTable();
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一位读者", "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        openDialog(tableModel.getRow(row));
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先在表格中选择一位读者", "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Reader reader = tableModel.getRow(row);
        int choice = JOptionPane.showConfirmDialog(this,
                "确定删除读者「" + reader.getName() + "」（" + reader.getCardNo() + "）吗？",
                "删除确认", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            readerDao.deleteList(Collections.singletonList(reader));
            refreshTable();
        }
    }

    /** 按借书证号查询（借书证校验入口，供借书时快速定位读者） */
    private void onQueryByCardNo() {
        String cardNo = txtCardNo.getText().trim();
        if (cardNo.isEmpty()) {
            refreshTable();
            return;
        }
        Reader reader = readerDao.getByCardNo(cardNo);
        if (reader == null) {
            JOptionPane.showMessageDialog(this, "未找到借书证号为 " + cardNo + " 的读者",
                    "查询结果", JOptionPane.WARNING_MESSAGE);
        }
        // 表格只显示查到的一条；查不到也清空提示
        tableModel.setList(reader == null
                ? Collections.<Reader>emptyList()
                : Collections.singletonList(reader));
    }
}
