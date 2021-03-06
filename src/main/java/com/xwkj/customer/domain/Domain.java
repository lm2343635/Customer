package com.xwkj.customer.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_domain")
public class Domain implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String did;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String domains;

    @Column(nullable = false)
    private String language;

    @Column(nullable = false)
    private String resolution;

    /**
     * SQL to update path.

     update customer_domain set path = concat('/data/wwwroot/', path) where language like '%PHP%';
     update customer_domain set path = concat('/data/webapps/', path) where language like '%Java%';
     update customer_domain set path = concat('C:\\home\\root\\', path) where language like '%ASP%';
     */
    @Column(nullable = false)
    private String path;

    @Column
    private String remark;

    @Column(nullable = false)
    private Boolean highlight;

    @Column(nullable = false)
    private Integer state;

    @Column(nullable = false)
    private Long createAt;

    @Column(nullable = false)
    private Long updateAt;

    @Column(nullable = false)
    private Boolean grabbed;

    @Column
    private String charset;

    @Column(columnDefinition = "LONGTEXT")
    private String page;

    @Column(nullable = false)
    private Boolean monitoring;

    @Column(nullable = false)
    private Long checkAt;

    @Column(nullable = false)
    private Boolean alert;

    /**
     update customer_domain set frequency = 1, similarity = 100;
     */
    @Column(nullable = false)
    private Integer similarity;

    @Column(nullable = false)
    private Integer frequency;

    @ManyToOne
    @JoinColumn(name = "sid", nullable = false)
    private Server server;

    /**
     update customer_domain set cid = '2c9f42425f6b8f7d015f7004bac3004e';
     */
    @ManyToOne
    @JoinColumn(name = "cid", nullable = false)
    private Customer customer;

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomains() {
        return domains;
    }

    public void setDomains(String domains) {
        this.domains = domains;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getHighlight() {
        return highlight;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public Boolean getGrabbed() {
        return grabbed;
    }

    public void setGrabbed(Boolean grabbed) {
        this.grabbed = grabbed;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Boolean getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Boolean monitoring) {
        this.monitoring = monitoring;
    }

    public Long getCheckAt() {
        return checkAt;
    }

    public void setCheckAt(Long checkAt) {
        this.checkAt = checkAt;
    }

    public Boolean getAlert() {
        return alert;
    }

    public void setAlert(Boolean alert) {
        this.alert = alert;
    }

    public Integer getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Integer similarity) {
        this.similarity = similarity;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
