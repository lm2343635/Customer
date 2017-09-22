package com.xwkj.customer.service;

import com.xwkj.customer.bean.CustomerBean;
import com.xwkj.customer.bean.Result;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface CustomerManager {

    public static final int CustomerStateUndeveloped = 0;
    public static final int CustomerStateDeveloping = 1;
    public static final int CustomerStateDeveloped = 2;
    public static final int CustomerStateLost = 3;

    /**
     * Employee add a new undeveloped customer.
     *
     * @param name
     * @param capital
     * @param contact
     * @param session
     * @return
     */
    Result addUndeveloped(String name, int capital, String contact, HttpSession session);

    /**
     * Get customers by state for employees.
     *
     * @param state
     * @param session
     * @return
     */
    Result getByState(int state, HttpSession session);

    /**
     * Get a customer for a employee.
     *
     * @param cid
     * @param session
     * @return
     */
    Result get(String cid, HttpSession session);

    /**
     * Edit a customer.
     *
     * @param cid
     * @param name
     * @param capital
     * @param contact
     * @param items
     * @param money
     * @param remark
     * @param document
     * @param session
     * @return
     */
    Result edit(String cid, String name, int capital, String contact, String items, int money, String remark, String document, HttpSession session);

}