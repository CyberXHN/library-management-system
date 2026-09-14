package com.library.frame;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.library.dao.UserDao;
import com.library.entity.User;
import com.library.util.UserDaoFactory;

/**
 * 登录界面（负责人：A）
 *
 * 教材同款结构：网格组布局的居中小窗口，用户名/密码与 user 表比对。
 * 登录流程按契约第七节：userDao.findByUsernameAndPassword 校验，
 * 通过则打开 MainFrame 并关闭本窗口，否则弹窗提示。
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final int WIDTH = 380;
    private static final int HEIGHT = 230;

    private JPanel panel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;

    public LoginFrame() {
        setTitle("欢迎进入图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        // 窗口居中
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - WIDTH) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - HEIGHT) / 2;
        setLocation(x, y);
        setResizable(false);

        init();
        setVisible(true);
    }

    private void init() {
        panel = new JPanel(new GridBagLayout());
        setContentPane(panel);

        JLabel lblTitle = new JLabel("欢迎进入图书管理系统");
        JLabel lblName = new JLabel("用户名：");
        JLabel lblPwd = new JLabel("密  码：");
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        JButton btnOk = new JButton("登录");
        JButton btnReset = new JButton("重置");
        JButton btnExit = new JButton("退出");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.CENTER;

        // 教材同款坐标式添加：x 列 / y 行 / w 跨列数 / h 跨行数
        add(lblTitle, gbc, 0, 0, 3, 1);
        add(lblName, gbc, 0, 1, 1, 1);
        add(txtUsername, gbc, 1, 1, 2, 1);
        add(lblPwd, gbc, 0, 2, 1, 1);
        add(txtPassword, gbc, 1, 2, 2, 1);
        add(btnOk, gbc, 0, 3, 1, 1);
        add(btnReset, gbc, 1, 3, 1, 1);
        add(btnExit, gbc, 2, 3, 1, 1);

        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        // 密码框回车直接登录
        txtPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtUsername.setText("");
                txtPassword.setText("");
                txtUsername.requestFocus();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    /** 教材同款辅助方法：按网格组坐标添加组件 */
    private void add(Component c, GridBagConstraints gbc, int x, int y, int w, int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        panel.add(c, gbc);
    }

    /** 登录校验（契约第七节登录流程） */
    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.length() == 0 || password.length() == 0) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UserDao userDao = UserDaoFactory.getDao();
        User user = userDao.findByUsernameAndPassword(username, password);
        if (user != null) {
            new MainFrame();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "用户名或密码错误", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
