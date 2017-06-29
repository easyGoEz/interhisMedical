package com.witnsoft.interhis.db.model;


import org.xutils.DbManager;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * Created by zhengchengpeng on 2017/6/5.
 */

// 问诊中药处方
@Table(name = "ASK_CHINESE")
public class ChineseModel {

    // primary key
    @Column(name = "TIME",
    isId = true,
    autoGen = true)
    private String time;

    @Column(name = "ACID")
    private String acId;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // 问诊ID
    @Column(name = "AIID")
    private String aiId;

    // 创建时间
    @Column(name = "ACTIME")
    private String acTime;

    // 创建时间
    @Column(name = "ACNO")
    private String acNo;

    // 患者姓名
    @Column(name = "PATNAME")
    private String patName;

    // 患者性别
    @Column(name = "PATSEX")
    private String patSex;

    // 患者性别名称
    @Column(name = "PATSEXNAME")
    private String patSexName;

    // 患者身份证号
    @Column(name = "PATSFZH")
    private String patSfzh;

    // 患者年龄
    @Column(name = "PATNL")
    private String patNl;

    // 诊断说明
    @Column(name = "ZDSM")
    private String zdsm;

    // 处方明细数量
    @Column(name = "ACMXS")
    private String acMxs;

    // 处方使用说明
    @Column(name = "ACSM")
    private String acSm;

    // 医生签名图片url
    @Column(name = "DOCQM")
    private String docQm;

    // 处方金额
    @Column(name = "JE")
    private String je;

    // 创建人
    @Column(name = "ACOPER")
    private String acOper;

    // 创建人姓名
    @Column(name = "ACOPERNAME")
    private String acOperName;

    // 是否已取药 y已取药 n未取药
    @Column(name = "QYFLAG")
    private String qyFlag;

    // 药店id
    @Column(name = "YDID")
    private String ydId;

    // 药店名称
    @Column(name = "YDMC")
    private String ydMc;

    // 药店详细地址
    @Column(name = "YDDZ")
    private String ydDz;

    // 药店电话
    @Column(name = "YDDH")
    private String ydDh;

    //从表
    @Column(name = "ASK_CHINESE_MX")
    private List<ChineseDetailModel> chineseDetailModel;

    //判断是否上传服务器
    @Column(name ="isUploadSever")
    private boolean isUploadSever;

    public boolean isUploadSever() {
        return isUploadSever;
    }

    public void setUploadSever(boolean uploadSever) {
        isUploadSever = uploadSever;
    }

    public void setAcId(String acId) {
        this.acId = acId;
    }

    public String getAcId() {
        return acId;
    }

    public void setAiId(String aiId) {
        this.aiId = aiId;
    }

    public String getAiId() {
        return aiId;
    }

    public void setAcTime(String acTime) {
        this.acTime = acTime;
    }

    public String getAcTime() {
        return acTime;
    }

    public void setAcNo(String acNo) {
        this.acNo = acNo;
    }

    public String getAcNo() {
        return acNo;
    }

    public void setPatName(String patName) {
        this.patName = patName;
    }

    public String getPatName() {
        return patName;
    }

    public void setPatSex(String patSex) {
        this.patSex = patSex;
    }

    public String getPatSex() {
        return patSex;
    }

    public void setPatSexName(String patSexName) {
        this.patSexName = patSexName;
    }

    public String getPatSexName() {
        return patSexName;
    }

    public void setPatSfzh(String patSfzh) {
        this.patSfzh = patSfzh;
    }

    public String getPatSfzh() {
        return patSfzh;
    }

    public void setPatNl(String patNl) {
        this.patNl = patNl;
    }

    public String getPatNl() {
        return patNl;
    }

    public void setZdsm(String zdsm) {
        this.zdsm = zdsm;
    }

    public String getZdsm() {
        return zdsm;
    }

    public void setAcMxs(String acMxs) {
        this.acMxs = acMxs;
    }

    public String getAcMxs() {
        return acMxs;
    }

    public void setAcSm(String acSm) {
        this.acSm = acSm;
    }

    public String getAcSm() {
        return acSm;
    }

    public void setDocQm(String docQm) {
        this.docQm = docQm;
    }

    public String getDocQm() {
        return docQm;
    }

    public void setJe(String je) {
        this.je = je;
    }

    public String getJe() {
        return je;
    }

    public void setAcOper(String acOper) {
        this.acOper = acOper;
    }

    public String getAcOper() {
        return acOper;
    }

    public void setAcOperName(String acOperName) {
        this.acOperName = acOperName;
    }

    public String getAcOperName() {
        return acOperName;
    }

    public void setQyFlag(String qyFlag) {
        this.qyFlag = qyFlag;
    }

    public String getQyFlag() {
        return qyFlag;
    }

    public void setYdId(String ydId) {
        this.ydId = ydId;
    }

    public String getYdId() {
        return ydId;
    }

    public void setYdMc(String ydMc) {
        this.ydMc = ydMc;
    }

    public String getYdMc() {
        return ydMc;
    }

    public void setYdDz(String ydDz) {
        this.ydDz = ydDz;
    }

    public String getYdDz() {
        return ydDz;
    }

    public void setYdDh(String ydDh) {
        this.ydDh = ydDh;
    }

    public String getYdDh() {
        return ydDh;
    }

    public List<ChineseDetailModel> getChineseDetailModel() {
        return chineseDetailModel;
    }

    public void setChineseDetailModel(List<ChineseDetailModel> chineseDetailModel) {
        this.chineseDetailModel = chineseDetailModel;
    }

    public List<ChineseDetailModel> getChineseDetail(DbManager db) throws DbException {
        return db.selector(ChineseDetailModel.class).where("ACID", "=", this.acId).findAll();
    }

}
