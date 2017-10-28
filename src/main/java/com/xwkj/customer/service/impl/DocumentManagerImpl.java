package com.xwkj.customer.service.impl;

import com.xwkj.common.util.Debug;
import com.xwkj.common.util.FileTool;
import com.xwkj.customer.bean.DocumentBean;
import com.xwkj.customer.bean.Result;
import com.xwkj.customer.domain.Customer;
import com.xwkj.customer.domain.Document;
import com.xwkj.customer.domain.Employee;
import com.xwkj.customer.service.DocumentManager;
import com.xwkj.customer.service.RoleManager;
import com.xwkj.customer.service.common.ManagerTemplate;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.UUID;

@Service
@RemoteProxy(name = "DocumentManager")
public class DocumentManagerImpl extends ManagerTemplate implements DocumentManager {

    @Transactional
    public Result handleCustomerDocument(String cid, String fileName, HttpSession session) {
        String path = configComponent.rootPath + File.separator + configComponent.UploadFolder + File.separator + cid;
        File file = new File(path + File.separator + fileName);
        Employee employee = getEmployeeFromSession(session);
        if (employee == null) {
            if (file.exists()) {
                file.delete();
            }
            return Result.NoSession();
        }
        Customer customer = customerDao.get(cid);
        if (customer == null) {
            Debug.error("Cannot find a customer by this cid.");
            if (file.exists()) {
                file.delete();
            }
            return Result.WithData(null);
        }
        switch (getPrivilegeForEmployee(employee, customer.getState(), RoleManager.PrivilegeWrite)) {
            case RoleManager.RolePrivilgeAssign:
                if (!assignWritePrivilege(customer, employee)) {
                    if (file.exists()) {
                        file.delete();
                    }
                    return Result.NoPrivilege();
                }
                break;
            case RoleManager.RolePrivilgeNone:
                if (file.exists()) {
                    file.delete();
                }
                return Result.NoPrivilege();
            default:
                break;
        }
        Document document = new Document();
        document.setFilename(fileName);
        document.setUploadAt(System.currentTimeMillis());
        document.setStore(UUID.randomUUID().toString());
        document.setSize(file.length());
        document.setCustomer(customer);
        // Save to persistent store.
        if (documentDao.save(document) == null) {
            if (file.exists()) {
                file.delete();
            }
            return Result.WithData(null);
        }
        // Modify file name.
        FileTool.modifyFileName(path, fileName, document.getStore());
        return Result.WithData(new DocumentBean(document, false));
    }

}
