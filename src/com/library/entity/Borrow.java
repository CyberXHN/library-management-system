package com.library.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 借阅记录实体（对应 borrow 表）
 */
public class Borrow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（数据库自增，新增时为 0） */
    private int id;
    /** 借阅单号：yyyyMMdd + 4 位序号（唯一） */
    private String borrowNo;
    /** 关联读者对象 */
    private Reader reader;
    /** 关联图书对象 */
    private Book book;
    /** 借出日期 */
    private Date borrowDate;
    /** 应还日期（借出 + 30 天） */
    private Date dueDate;
    /** 实际归还日期（未还 = null） */
    private Date returnDate;
    /** 逾期罚款（每逾期 1 天 0.5 元） */
    private double fine;
    /** 借出 / 已还 / 逾期 */
    private String status;

    public Borrow() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBorrowNo() {
        return borrowNo;
    }

    public void setBorrowNo(String borrowNo) {
        this.borrowNo = borrowNo;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(Date borrowDate) {
        this.borrowDate = borrowDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
