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
    @Column(name = "ACCID",
            isId = true,
            autoGen = false)
    private String accid;

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

    private String XMMC;//药名
    private String XMRJ;//药品拼音
    private int SFDLBM;//药品类别

    public String getXMMC() {
        return XMMC;
    }

    public void setXMMC(String XMMC) {
        this.XMMC = XMMC;
    }

    public String getXMRJ() {
        return XMRJ;
    }

    public void setXMRJ(String XMRJ) {
        this.XMRJ = XMRJ;
    }

    public int getSFDLBM() {
        return SFDLBM;
    }

    public void setSFDLBM(int SFDLBM) {
        this.SFDLBM = SFDLBM;
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
}

