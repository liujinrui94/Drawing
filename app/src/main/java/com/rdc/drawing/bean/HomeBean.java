package com.rdc.drawing.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edianzu on 2018/4/18.
 */

public class HomeBean {

    Map<String, String>  map= new HashMap<String, String>();



    boolean select = false;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public String toString() {
        return "HomeBean{" +
                "map=" + map +
                ", select=" + select +
                '}';
    }
}
