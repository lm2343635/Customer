package com.xwkj.customer.controller.common;

import com.aliyun.oss.model.OSSObject;
import com.xwkj.common.util.FileTool;
import com.xwkj.customer.bean.DocumentBean;
import com.xwkj.customer.component.AliyunOSSComponent;
import com.xwkj.customer.component.ConfigComponent;
import com.xwkj.customer.service.AdminManager;
import com.xwkj.customer.service.DeviceManager;
import com.xwkj.customer.service.DocumentManager;
import com.xwkj.customer.service.EmployeeManager;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ControllerTemplate {

    @Autowired
    protected ConfigComponent configComponent;

    @Autowired
    protected AliyunOSSComponent aliyunOSSComponent;

    @Autowired
    protected DocumentManager documentManager;

    @Autowired
    protected DeviceManager deviceManager;

    @Autowired
    protected EmployeeManager employeeManager;

    protected ResponseEntity generateOK(Map<String, Object> result) {
        return generateResponseEntity(result, HttpStatus.OK, null, null);
    }

    protected ResponseEntity generateBadRequest(int errorCode, String errorMessage) {
        return generateResponseEntity(null, HttpStatus.BAD_REQUEST, errorCode, errorMessage);
    }

    protected ResponseEntity generateBadRequest(ErrorCode errorCode) {
        return generateBadRequest(errorCode.code, errorCode.message);
    }

    private String rootPath;

    public String getRootPath() {
        if (rootPath == null) {
            rootPath = this.getClass().getClassLoader().getResource("/").getPath().split("WEB-INF")[0];
        }
        return rootPath;
    }

    protected ResponseEntity generateResponseEntity(Map<String, Object> result, HttpStatus status, Integer errCode, String errMsg) {
        Map<String, Object> data = new HashMap<String, Object>();
        if (result != null) {
            data.put("result", result);
        }
        data.put("status", status.value());
        if (errCode != null) {
            data.put("errorCode", errCode);
        }
        if (errMsg != null) {
            data.put("errorMessage", errMsg);
        }
        return new ResponseEntity(data, status);
    }

    public boolean checkAdminSession(HttpSession session) {
        return session.getAttribute(AdminManager.AdminFlag) != null;
    }

    public boolean checkEmployeeSession(HttpSession session) {
        return session.getAttribute(EmployeeManager.EmployeeFlag) != null;
    }

    /**
     * Create upload directory if it is not existed.
     *
     * @param directory
     * @return
     */
    public String createUploadDirectory(String directory) {
        String filepath = getRootPath() + configComponent.UploadFolder + File.separator + directory;
        // If directory is not existed, create the directory at first.
        FileTool.createDirectoryIfNotExsit(filepath);
        return filepath;
    }

    /**
     * Upload file to a file path.
     *
     * @param request
     * @param filepath
     * @return
     */
    public String upload(HttpServletRequest request, String filepath) {
        String fileName = null;
        // Create factory object.
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // Set cache size to 4KB.
        factory.setSizeThreshold(1024 * 4);
        // Set upload file path.
        factory.setRepository(new File(filepath));
        // Create servlet file upload object.
        ServletFileUpload upload = new ServletFileUpload(factory);
        // Set limitation of uploaded file.
        upload.setSizeMax(configComponent.FileMaxSize);
        upload.setHeaderEncoding("UTF-8");
        try {
            // Get all uploading files information.
            List<FileItem> list = upload.parseRequest(request);
            Iterator<FileItem> it = list.iterator();
            while (it.hasNext()) {
                FileItem item = it.next();
                if (item.isFormField() == false) {
                    fileName = item.getName();
                    fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1, fileName.length());
                    if (!fileName.equals("") && !(fileName == null)) {
                        // If file name is null, that means there is no uploading file.
                        File uploadedFile = new File(filepath, fileName);
                        try {
                            item.write(uploadedFile);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * Download file with data stream.
     *
     * @param path File store path
     * @param fileName
     * @param response
     * @throws IOException
     */
    public void download(String path, String fileName, HttpServletResponse response) throws IOException {
        FileInputStream in = null;
        ServletOutputStream out = null;
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes("UTF-8"), "iso8859-1"));
        try {
            in = new FileInputStream(path);
            out = response.getOutputStream();
            out.flush();
            int aRead = 0;
            while ((aRead = in.read()) != -1 & in != null) {
                out.write(aRead);
            }
            out.flush();
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void downloadFromAliyunOSS(DocumentBean document, HttpServletResponse response) throws IOException {
        Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
        OSSObject ossObject = aliyunOSSComponent.getOSSObject(document, expiration);
        if (ossObject == null) {
            return;
        }
        InputStream inputStream = ossObject.getObjectContent();
        int buffer = 1024 * 10;
        byte data[] = new byte[buffer];
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(document.getFilename().getBytes("UTF-8"), "iso8859-1"));
        int read;
        while ((read = inputStream.read(data)) != -1) {
            response.getOutputStream().write(data, 0, read);
        }
        response.getOutputStream().flush();
        response.getOutputStream().close();
        ossObject.close();
    }

    /**
     * Get device remote IP by HttpServletRequest.
     *
     * @param request
     * @return
     */
    protected static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
