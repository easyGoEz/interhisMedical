package com.witnsoft.interhis.db.model;


import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.io.Serializable;

/**
 * Created by zhengchengpeng on 2017/6/5.
 */

// 中药处方详细
@Table(name = "ASK_CHINESE_MX")
public class ChineseDetailModel {

    // primary key
    @Column(name = "Time",
            isId = true,
            autoGen = true)
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Column(name = "ACCID")
    private String accid;

    // 中药处方ID
    @Column(name = "ACID")
    private String sfxmbm;

    // 中药代码
    @Column(name = "CDM")
    private String cdm;

    // 中药名称
    @Column(name = "CMC")
    private String cmc;

    // 中药规格代码
    @Column(name = "CGGDM")
    private String cggDm;

    // 中药规格名称
    @Column(name = "CGGMC")
    private String cggMc;

    // 中药数量
    @Column(name = "SL")
    private String sl;

    // 总金额
    @Column(name = "JE")
    private String je;

    // 单价
    @Column(name = "DJ")
    private String bzjg;

    @Column(name = "DOCID")
    private String docId;

    @Column(name = "AIID")
    private String aiid;

    public ChineseModel getChinese(DbManager db) throws DbException {
        return db.findById(ChineseModel.class, accid);
    }

    public void setAccid(String acmId) {
        this.accid = acmId;
    }

    public String getAccid() {
        return accid;
    }

    public void setAcId(String acId) {
        this.sfxmbm = acId;
    }

    public String getAcId() {
        return sfxmbm;
    }

    public void setCdm(String cdm) {
        this.cdm = cdm;
    }

    public String getCdm() {
        return cdm;
    }

    public void setCmc(String cmc) {
        this.cmc = cmc;
    }

    public String getCmc() {
        return cmc;
    }

    public void setCggDm(String cggDm) {
        this.cggDm = cggDm;
    }

    public String getCggDm() {
        return cggDm;
    }

    public void setCggMc(String cggMc) {
        this.cggMc = cggMc;
    }

    public String getCggMc() {
        return cggMc;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getSl() {
        return sl;
    }

    public void setJe(String je) {
        this.je = je;
    }

    public String getJe() {
        return je;
    }

    public void setDj(String dj) {
        this.bzjg = dj;
    }

    public String getDj() {
        return bzjg;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocId() {
        return docId;
    }

    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public String getAiid() {
        return aiid;
    }
}