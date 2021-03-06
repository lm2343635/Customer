package com.xwkj.customer.component;

import com.xwkj.common.util.Debug;
import com.xwkj.common.util.FileTool;
import com.xwkj.common.util.HTTPTool;
import com.xwkj.common.util.RuntimeTool;
import com.xwkj.customer.dao.CheckDao;
import com.xwkj.customer.dao.DomainDao;
import com.xwkj.customer.domain.Check;
import com.xwkj.customer.domain.Domain;
import com.xwkj.customer.domain.Server;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@EnableScheduling
public class DomainComponent {

    private static final int FixedRate = 1000 * 60 * 10;

    @Autowired
    private ConfigComponent config;

    @Autowired
    private DomainDao domainDao;

    @Autowired
    private CheckDao checkDao;


    private String rootPath;

    public String getRootPath() {
        if (rootPath == null) {
            rootPath = this.getClass().getClassLoader().getResource("/").getPath().split("WEB-INF")[0];
        }
        return rootPath;
    }

    /**
     * Monitoring domains every 10 minitus (FixedRate).
     */
    @Scheduled(fixedRate = FixedRate)
    @Transactional
    public void monitoring() {
        Long now = System.currentTimeMillis();
        String basePath = getRootPath() + config.PublicIndexFolder;
        NormalizedLevenshtein levenshtein = new NormalizedLevenshtein();

        List<Domain> domains = new ArrayList<Domain>();
        for (Domain domain : domainDao.findMonitoring()) {
            long interval = Math.round((now - domain.getCheckAt()) * 1.0 / 1000);
            if (interval < domain.getFrequency() * 60 * 10) {
                continue;
            }
            domains.add(domain);
        }

        int sleep = (int)(FixedRate * 0.9);
        if (domains.size() > 0) {
            sleep /= domains.size();
            if (sleep > 10 * 1000) {
                sleep = 10 * 1000;
            }
        }
        for (Domain domain : domains) {
            Check check = new Check();
            check.setCheckAt(System.currentTimeMillis());
            check.setDomain(domain);
            check.setReplaced(false);

            String site = domain.getDomains().split(",")[0];
            if (site == null || site.equals("")) {
                continue;
            }

            check.setUrl("http://" + site);
            String remote = HTTPTool.httpRequest(check.getUrl(), domain.getCharset());

            if (remote == null || domain.getPage() == null) {
                continue;
            }
            check.setSimilarity(levenshtein.similarity(remote, domain.getPage()));
            // Replace the index file if the similarity is smaller than the value set before.
            if (check.getSimilarity() * 100 < domain.getSimilarity()) {
                Server server = domain.getServer();
                String cmd = basePath + File.separator + domain.getDid() + File.separator + "index.html "
                        + server.getUser() + "@" + server.getAddress() + ":" + domain.getPath();
                if (server.getUsingPublicKey()) {
                    cmd = "scp -i " + getPublicKeyPath(server) + " " + cmd;
                } else {
                    cmd = "sshpass -p " + server.getCredential() + " scp -oStrictHostKeyChecking=no " + cmd;
                }
                check.setReplaced(RuntimeTool.run(cmd));
                domain.setAlert(true);
            }
            // Update check time.
            domain.setCheckAt(now);
            domainDao.update(domain);
            // Create a new check object.

            checkDao.save(check);

            Debug.log(site + " has been checked, similarity = " + check.getSimilarity());

            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPublicKeyPath(Server server) {
        String path = getRootPath() + config.PublicKeyFolder + File.separator + server.getSid();
        if (new File(path).exists()) {
            return path;
        }
        // If public key file is not existed, create a new one.
        if (!generatePublicKeyFile(server)) {
            return null;
        }
        return path;
    }

    public boolean generatePublicKeyFile(Server server) {
        if (!server.getUsingPublicKey()) {
            return false;
        }
        String path = getRootPath() + config.PublicKeyFolder;
        FileTool.createDirectoryIfNotExsit(path);
        String pathname = path + File.separator + server.getSid();
        File file = new File(pathname);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(server.getCredential().getBytes());
            out.close();
            Set<PosixFilePermission> permissions = new HashSet<PosixFilePermission>();
            permissions.add(PosixFilePermission.OWNER_READ);
            permissions.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(Paths.get(pathname), permissions);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return file.exists();
    }

}
