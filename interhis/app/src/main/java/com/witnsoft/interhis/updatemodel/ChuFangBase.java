package com.witnsoft.interhis.updatemodel;


import com.witnsoft.interhis.db.model.ChineseDetailModel;

import java.util.List;

/**
 * Created by ${liyan} on 2017/6/14.
 */

public class ChuFangBase {

    //患者的id
    private String helperId;
    //医生的id
    private String doctorId;
    //药的种类
    private String yftype;
    //问诊id
    private String aiid;
    //症状
    private String zdsm;
    //数量
    private String acmxs;
    //医嘱
    private String acsm;
    //价格
    private String je;

    private List<ChineseDetailModel> list;

    public List<ChineseDetailModel> getList() {
        return list;
    }

    public void setList(List<ChineseDetailModel> list) {
        this.list = list;
    }

    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getYftype() {
        return yftype;
    }

    public void setYftype(String yftype) {
        this.yftype = yftype;
    }

    public String getAiid() {
        return aiid;
    }

    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public String getZdsm() {
        return zdsm;
    }

    public void setZdsm(String zdsm) {
        this.zdsm = zdsm;
    }

    public String getAcmxs() {
        return acmxs;
    }

    public void setAcmxs(String acmxs) {
        this.acmxs = acmxs;
    }

    public String getAcsm() {
        return acsm;
    }

    public void setAcsm(String acsm) {
        this.acsm = acsm;
    }

    public String getJe() {
        return je;
    }

    public void setJe(String je) {
        this.je = je;
    }
}
