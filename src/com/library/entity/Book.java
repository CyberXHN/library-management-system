package com.library.entity;

import java.io.Serializable;

/**
 * 图书实体（对应 book 表）
 */
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键（数据库自增，新增时为 0） */
    private int id;
    /** 书名 */
    private String name;
    /** 关联分类对象（含分类 id 与名称） */
    private Category category;
    /** ISBN 号（唯一） */
    private String isbn;
    /** 作者 */
    private String author;
    /** 出版社 */
    private String publisher;
    /** 价格 */
    private double price;
    /** 可借库存 */
    private int stock;
    /** 在架 / 下架（下架不可借） */
    private String status;

    public Book() {
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
