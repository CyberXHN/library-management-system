package com.library.dialog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.library.dao.ReaderDao;
import com.library.entity.Reader;
import com.library.util.ReaderDaoFactory;

/**
 * 读者新增 / 编辑对话框（板块负责人：B）
 * 教材风格：模态 JDialog + 表单输入 + 确认按钮。
 *
 * 借书证校验（B 的核心职责）：
 * 1. 姓名、借书证号不能为空；
 * 2. 电话必须为 11 位数字（可空）；
 * 3. 借书证号全库唯一——新增时调 getByCardNo 查重；
 *    编辑时排除自己（证号未改不算重复）。
 */
public class ReaderAddDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private ReaderDao readerDao = ReaderDaoFactory.getDao();

    private JTextField txtName;    // 姓名
    private JRadioButton rbMale;   // 性别：男
    private JRadioButton rbFemale; // 性别：女
    private JTextField txtTel;     // 电话
    private JTextField txtCardNo;  // 借书证号

    private Reader reader;  // 编辑模式时持有被编辑对象（id 保留）
    private boolean saved;  // 是否成功保存（父面板据此决定是否刷新表格）

    /** 新增模式 */
    public ReaderAddDialog(java.awt.Frame owner) {
        this(owner, null);
    }

    /** 编辑模式：传入被编辑的读者对象，表单回显其数据 */
    public ReaderAddDialog(java.awt.Frame owner, Reader reader) {
        super(owner, reader == null ? "新增读者" : "编辑读者", true);
        this.reader = reader;
        initForm();
        if (reader != null) {
            txtName.setText(reader.getName());
            if ("女".equals(reader.getSex())) {
                rbFemale.setSelected(true);
            } else {
                rbMale.setSelected(true);
            }
            txtTel.setText(reader.getTel());
            txtCardNo.setText(reader.getCardNo());
        }
        pack();
        setLocationRelativeTo(owner);
    }

    private void initForm() {
        setLayout(new GridLayout(5, 2, 10, 10));
        setPreferredSize(new Dimension(320, 220));

        add(new JLabel("姓名：", JLabel.RIGHT));
        txtName = new JTextField();
        add(txtName);

        add(new JLabel("性别：", JLabel.RIGHT));
        rbMale = new JRadioButton("男", true);
        rbFemale = new JRadioButton("女");
        ButtonGroup group = new ButtonGroup();
        group.add(rbMale);
        group.add(rbFemale);
        JPanel sexPanel = new JPanel();
        sexPanel.add(rbMale);
        sexPanel.add(rbFemale);
        add(sexPanel);

        add(new JLabel("电话：", JLabel.RIGHT));
        txtTel = new JTextField();
        add(txtTel);

        add(new JLabel("借书证号：", JLabel.RIGHT));
        txtCardNo = new JTextField();
        add(txtCardNo);

        add(new JLabel(""));
        JButton btnOk = new JButton("保存");
        add(btnOk);

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSave();
            }
        });
    }

    /** 保存：先校验，通过后新增或更新 */
    private void onSave() {
        String name = txtName.getText().trim();
        String sex = rbMale.isSelected() ? "男" : "女";
        String tel = txtTel.getText().trim();
        String cardNo = txtCardNo.getText().trim();

        String error = validate(name, tel, cardNo);
        if (error != null) {
            javax.swing.JOptionPane.showMessageDialog(this, error, "输入有误",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (reader == null) {
            // 新增：id=0 由数据库自增
            Reader r = new Reader(0, name, sex, tel, cardNo);
            readerDao.saveList(Collections.singletonList(r));
        } else {
            // 编辑：保留原 id，整条记录覆盖写
            reader.setName(name);
            reader.setSex(sex);
            reader.setTel(tel);
            reader.setCardNo(cardNo);
            readerDao.update(reader);
        }
        saved = true;
        dispose();
    }

    /**
     * 借书证校验（核心业务规则）
     * @return 校验失败返回错误提示，通过返回 null
     */
    private String validate(String name, String tel, String cardNo) {
        if (name.isEmpty()) {
            return "姓名不能为空";
        }
        if (cardNo.isEmpty()) {
            return "借书证号不能为空";
        }
        if (!tel.isEmpty() && !tel.matches("\\d{11}")) {
            return "电话必须为 11 位数字";
        }
        // 证号唯一性：查库防重复
        Reader exist = readerDao.getByCardNo(cardNo);
        if (exist != null) {
            // 编辑模式下证号没变不算重复
            boolean unchanged = reader != null && exist.getId() == reader.getId();
            if (!unchanged) {
                return "借书证号已存在：" + cardNo;
            }
        }
        return null;
    }

    /** 是否保存成功（父面板据此刷新表格） */
    public boolean isSaved() {
        return saved;
    }
}
