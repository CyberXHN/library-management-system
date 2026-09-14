package com.library.entity;

import java.io.Serializable;

/**
 * 读者实体（对应 reader 表）
 */
public class Reader implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（数据库自增，新增时为 0） */
    private int id;
    /** 姓名 */
    private String name;
    /** 性别：男 / 女 */
    private String sex;
    /** 电话 */
    private String tel;
    /** 借书证号（唯一） */
    private String cardNo;

    public Reader() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }
}
