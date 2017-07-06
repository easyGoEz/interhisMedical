package com.witnsoft.interhis.db.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhengchengpeng on 2017/7/6.
 */

@Table(name = "DIAGNOSIS")
public class DiagnosisModel {

    // primary key
    @Column(name = "Time",
            isId = true,
            autoGen = true)
    private String time;

    @Column(name = "DESCRIBE")
    private String describe;

    @Column(name = "ACCID")
    private String accId;

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getAccId() {
        return accId;
    }
}
