package com.witnsoft.interhis.db.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhengchengpeng on 2017/6/5.
 */

// 西药处方明细
@Table(name = "ASK_WESTERN_MX")
public class WesternDetailModel {
    // primary key
    @Column(name = "TIME",
            isId = true,
            autoGen = false)
    private String time;

    @Column(name = "ACCID")
    private String accid;

    //问诊id
    @Column(name = "AIID")
    private String aiid;

    @Column(name = "DOCID")
    private String docId;

    // 西药代码
    @Column(name = "AWDM")
    private String awDm;

    // 西药名称
    @Column(name = "CMC")
    private String cmc;

    // 西药规格代码
    @Column(name = "AWGGDM")
    private String awGgDm;

    // 西药规格名称
    @Column(name = "AWGGMC")
    private String awGgMc;

    // 西药数量
    @Column(name = "SL")
    private String sl;

    // 西药用法说明
    @Column(name = "AWSM")
    private String awSm;

    // 总金额
    @Column(name = "JE")
    private String je;

    // 单价
    @Column(name = "DJ")
    private String dj;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setAccid(String awmId) {
        this.accid = awmId;
    }

    public String getAccid() {
        return accid;
    }

    public void setAwDm(String awDm) {
        this.awDm = awDm;
    }

    public String getAwDm() {
        return awDm;
    }

    public void setCmc(String awMc) {
        this.cmc = awMc;
    }

    public String getCmc() {
        return cmc;
    }

    public void setAwGgDm(String awGgDm) {
        this.awGgDm = awGgDm;
    }

    public String getAwGgDm() {
        return awGgDm;
    }

    public void setAwGgMc(String awGgMc) {
        this.awGgMc = awGgMc;
    }

    public String getAwGgMc() {
        return awGgMc;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public void setAwSm(String awSm) {
        this.awSm = awSm;
    }

    public String getAwSm() {
        return awSm;
    }

    public void setJe(String je) {
        this.je = je;
    }

    public String getJe() {
        return je;
    }

    public void setDj(String dj) {
        this.dj = dj;
    }

    public String getDj() {
        return dj;
    }

    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public String getAiid() {
        return aiid;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }
}

