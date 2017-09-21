package com.xwkj.customer.dao.impl;

import com.xwkj.common.hibernate.BaseHibernateDaoSupport;
import com.xwkj.customer.dao.AssignDao;
import com.xwkj.customer.domain.Assign;
import org.springframework.stereotype.Repository;

@Repository
public class AssignDaoHibernate extends BaseHibernateDaoSupport<Assign> implements AssignDao {

    public AssignDaoHibernate() {
        super();
        setClass(Assign.class);
    }

}
