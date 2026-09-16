package com.library.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.library.dao.BookDao;
import com.library.entity.Book;
import com.library.pane.BookManagePane;
import com.library.pane.BorrowPane;
import com.library.pane.CategoryManagePane;
import com.library.pane.ReaderManagePane;
import com.library.pane.ReturnPane;
import com.library.pane.StatisticPane;
import com.library.util.BackupUtil;
import com.library.util.BookDaoFactory;

/**
 * 系统主界面（负责人：A）
 *
 * 教材同款结构：顶部菜单栏 + 中央 panel 切换（removeAll → add → validate → repaint），
 * 窗口规格按契约 1024×728，居中显示。
 *
 * 菜单与面板对应关系（契约第五节归属表，各模块交付后由 A 在集成阶段接入）：
 *   读者信息管理 → ReaderManagePane（B）
 *   分类管理 / 图书管理 → CategoryManagePane / BookManagePane（C）
 *   借书 / 还书 → BorrowPane / ReturnPane（D）
 *   借阅统计 → StatisticPane（E）
 *   数据备份 / 数据恢复 → BackupUtil（E 提供序列化工具，本类负责文件选择与写回交互）
 * 每个面板都在点击菜单时新建，面板构造器内部重新查库，因此展示的始终是最新数据。
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 1024;
    private static final int HEIGHT = 728;

    private JPanel panel; // 中央切换面板
    private JMenuBar bar;
    private JMenu readerMenu, bookMenu, borrowMenu, statisticMenu, systemMenu;
    private JMenuItem readerManage, categoryManage, bookManage,
            borrowManage, returnManage, statisticManage,
            backupItem, restoreItem, exitItem;

    public MainFrame() {
        setTitle("图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        // 窗口居中
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT) / 2;
        setLocation(x, y);

        init();
        setVisible(true);
    }

    private void init() {
        bar = new JMenuBar();
        readerMenu = new JMenu("读者管理");
        bookMenu = new JMenu("图书管理");
        borrowMenu = new JMenu("借阅管理");
        statisticMenu = new JMenu("统计查询");
        systemMenu = new JMenu("系统管理");

        readerManage = new JMenuItem("读者信息管理");
        categoryManage = new JMenuItem("分类管理");
        bookManage = new JMenuItem("图书管理");
        borrowManage = new JMenuItem("借书");
        returnManage = new JMenuItem("还书");
        statisticManage = new JMenuItem("借阅统计");
        backupItem = new JMenuItem("数据备份");
        restoreItem = new JMenuItem("数据恢复");
        exitItem = new JMenuItem("退出系统");

        bar.add(readerMenu);
        bar.add(bookMenu);
        bar.add(borrowMenu);
        bar.add(statisticMenu);
        bar.add(systemMenu);

        readerMenu.add(readerManage);
        bookMenu.add(categoryManage);
        bookMenu.add(bookManage);
        borrowMenu.add(borrowManage);
        borrowMenu.add(returnManage);
        statisticMenu.add(statisticManage);
        systemMenu.add(backupItem);
        systemMenu.add(restoreItem);
        systemMenu.addSeparator();
        systemMenu.add(exitItem);

        panel = new JPanel(new BorderLayout());
        panel.add(buildPlaceholder("欢迎使用图书管理系统"), BorderLayout.CENTER);
        add(panel);
        setJMenuBar(bar);

        // ===== 菜单项事件：切换中央面板 =====

        readerManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 集成（B）：读者信息管理面板
                switchPanel(new ReaderManagePane());
            }
        });

        categoryManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 集成（C）：分类管理面板
                switchPanel(new CategoryManagePane());
            }
        });

        bookManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 集成（C）：图书管理面板
                switchPanel(new BookManagePane());
            }
        });

        borrowManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 集成（D）：借书面板
                switchPanel(new BorrowPane());
            }
        });

        returnManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 集成（D）：还书面板
                switchPanel(new ReturnPane());
            }
        });

        statisticManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 集成（E）：借阅统计面板
                switchPanel(new StatisticPane());
            }
        });

        backupItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doBackup();
            }
        });

        restoreItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRestore();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    // ==================== 数据备份 / 数据恢复（序列化） ====================

    /**
     * 数据备份：把图书数据（List<Book>，含分类名）序列化写入用户选定的 .dat 文件。
     * 备份范围与《设计方案》中序列化知识点的示例口径一致（实体已 implements Serializable）。
     */
    private void doBackup() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择备份文件保存位置");
        chooser.setSelectedFile(new File("backup.dat"));
        chooser.setFileFilter(new FileNameExtensionFilter("备份文件(*.dat)", "dat"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        List<Book> books = BookDaoFactory.getDao().getList();
        try {
            BackupUtil.backup(file, books);
            JOptionPane.showMessageDialog(this,
                    "备份成功！共 " + books.size() + " 条图书数据。\n文件位置：" + file.getAbsolutePath(),
                    "数据备份", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "备份失败：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 数据恢复：反序列化备份文件中的图书数据并写回数据库。
     * 备份里的 id 在当前库中已存在则覆盖更新，不存在则按新增插入（id 归零交给自增），
     * 这样既能还原被误删的图书，也不会打断已有借阅记录对图书 id 的引用。
     */
    private void doRestore() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择要恢复的备份文件");
        chooser.setFileFilter(new FileNameExtensionFilter("备份文件(*.dat)", "dat"));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        int confirm = JOptionPane.showConfirmDialog(this,
                "将用备份文件中的图书数据覆盖/补全当前图书表，是否继续？",
                "数据恢复确认", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            List<Book> books = BackupUtil.restore(file);
            BookDao bookDao = BookDaoFactory.getDao();
            int updated = 0;
            int inserted = 0;
            for (Book b : books) {
                if (b == null) {
                    continue;
                }
                Book exist = bookDao.getById(b.getId());
                if (exist == null && b.getIsbn() != null) {
                    exist = bookDao.getByIsbn(b.getIsbn()); // 兜底：ISBN 相同视为同一本书
                }
                if (exist == null) {
                    b.setId(0); // 新增记录的主键交给数据库自增
                    bookDao.saveList(Collections.singletonList(b));
                    inserted++;
                } else {
                    b.setId(exist.getId());
                    bookDao.update(b);
                    updated++;
                }
            }
            JOptionPane.showMessageDialog(this,
                    "恢复完成！更新 " + updated + " 条，新增 " + inserted + " 条。",
                    "数据恢复", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "恢复失败，请确认选择的是本系统导出的备份文件：" + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 教材同款面板切换：清空 → 添加 → 重绘 */
    private void switchPanel(JPanel target) {
        panel.removeAll();
        panel.add(target, BorderLayout.CENTER);
        panel.validate();
        repaint();
    }

    /** 占位面板：各模块 Pane 交付前的临时展示 */
    private JPanel buildPlaceholder(String text) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        p.add(label, BorderLayout.CENTER);
        return p;
    }
}
