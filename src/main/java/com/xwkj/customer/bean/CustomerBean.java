package com.xwkj.customer.bean;

import com.xwkj.customer.domain.Customer;
import org.directwebremoting.annotations.DataTransferObject;

import java.util.Date;

@DataTransferObject
public class CustomerBean {

    private String cid;
    private Date createAt;
    private Date updateAt;
    private int state;
    private String name;
    private int capital;
    private String contact;
    private String items;
    private int money;
    private String remark;
    private String document;

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapital() {
        return capital;
    }

    public void setCapital(int capital) {
        this.capital = capital;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public CustomerBean(Customer customer, boolean full) {
        this.cid = customer.getCid();
        this.createAt = new Date(customer.getCreateAt());
        this.updateAt = new Date(customer.getUpdateAt());
        this.state = customer.getState();
        this.name = customer.getName();
        this.contact = customer.getContact();
        if (full) {
            this.capital = customer.getCapital();
            this.items = customer.getItems();
            this.money = customer.getMoney() == null ? 0 : customer.getMoney();
            this.document = customer.getDocument();
            this.remark = customer.getRemark();
        }
    }
}