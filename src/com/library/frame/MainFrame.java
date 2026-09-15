package com.library.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 系统主界面（负责人：A）
 *
 * 教材同款结构：顶部菜单栏 + 中央 panel 切换（removeAll → add → validate → repaint），
 * 窗口规格按契约 1024×728，居中显示。
 *
 * 各菜单项目前挂占位面板，第 2–3 周集成各成员的 Pane 类（契约第五节归属表）：
 *   读者信息管理 → ReaderManagePane（B）
 *   分类管理 / 图书管理 → CategoryManagePane / BookManagePane（C）
 *   借书 / 还书 → BorrowPane / ReturnPane（D）
 *   借阅统计 → StatisticPane（E）
 *   数据备份 / 数据恢复 → BackupUtil（E，第 4 周集成）
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
                // TODO 集成（B）：换成 new ReaderManagePane()
                switchPanel(buildPlaceholder("读者管理（成员 B 开发中，第 2-3 周集成）"));
            }
        });

        categoryManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（C）：换成 new CategoryManagePane()
                switchPanel(buildPlaceholder("分类管理（成员 C 开发中，第 2-3 周集成）"));
            }
        });

        bookManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（C）：换成 new BookManagePane()
                switchPanel(buildPlaceholder("图书管理（成员 C 开发中，第 2-3 周集成）"));
            }
        });

        borrowManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（D）：换成 new BorrowPane()
                switchPanel(buildPlaceholder("借书（成员 D 开发中，第 3 周集成）"));
            }
        });

        returnManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（D）：换成 new ReturnPane()
                switchPanel(buildPlaceholder("还书（成员 D 开发中，第 3 周集成）"));
            }
        });

        statisticManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（E）：换成 new StatisticPane()
                switchPanel(buildPlaceholder("借阅统计（成员 E 开发中，第 4 周集成）"));
            }
        });

        backupItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（E，第 4 周）：选择文件后调用 BackupUtil.backup(file, list)
                JOptionPane.showMessageDialog(MainFrame.this,
                        "数据备份将在第 4 周集成（成员 E 的 BackupUtil）", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        restoreItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 集成（E，第 4 周）：选择文件后调用 BackupUtil.restore(file)
                JOptionPane.showMessageDialog(MainFrame.this,
                        "数据恢复将在第 4 周集成（成员 E 的 BackupUtil）", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
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
