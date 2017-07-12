package com.witnsoft.interhis.setting.myhistory.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/7/12.
 */

public class SerializableMap implements Serializable {

    private Map<String, String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
