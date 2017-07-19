package com.witnsoft.interhis.db.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhengchengpeng on 2017/6/5.
 */

// 问诊西药处方
@Table(name = "ASK_WESTERN")
public class WesternModel {
    // primary key
    @Column(
            name = "TIME",
            isId = true,
            autoGen = false)
    private String time;

    @Column(name = "AWID")
    private String awId;

    // 问诊ID
    @Column(name = "AIID")
    private String aiId;

    //创建时间
    @Column(name = "AWTIME")
    private String awTime;

    // 处方编号
    @Column(name = "AWNO")
    private String awNo;

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
    @Column(name = "AWMXS")
    private String awmxs;

    // 处方使用说明(用法用量)
    @Column(name = "AWSM")
    private String awsm;

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

    @Column(name = "ASK_WESTERN_MX")
    private WesternDetailModel westernDetailModel;

    public void setAwId(String awId) {
        this.awId = awId;
    }

    public String getAwId() {
        return awId;
    }

    public void setAiId(String aiId) {
        this.aiId = aiId;
    }

    public String getAiId() {
        return aiId;
    }

    public void setAwTime(String awTime) {
        this.awTime = awTime;
    }

    public String getAwTime() {
        return awTime;
    }

    public void setAwNo(String awNo) {
        this.awNo = awNo;
    }

    public String getAwNo() {
        return awNo;
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

    public void setAwmxs(String awmxs) {
        this.awmxs = awmxs;
    }

    public String getAwmxs() {
        return awmxs;
    }

    public void setAwsm(String awsm) {
        this.awsm = awsm;
    }

    public String getAwsm() {
        return awsm;
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

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setWesternDetailModel(WesternDetailModel westernDetailModel) {
        this.westernDetailModel = westernDetailModel;
    }

    public WesternDetailModel getWesternDetailModel() {
        return westernDetailModel;
    }
}
