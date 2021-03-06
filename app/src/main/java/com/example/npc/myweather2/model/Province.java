package com.example.npc.myweather2.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by npc on 3-13 0013.
 */

public class Province extends DataSupport{
    private int id;
    private String provinceName;
    @Column(unique =true)
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }
}
