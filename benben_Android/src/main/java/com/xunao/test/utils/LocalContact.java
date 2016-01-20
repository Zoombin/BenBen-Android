package com.xunao.test.utils;

import java.util.List;

/**
 * Created by ltf on 2015/11/16.
 */
public class LocalContact {
    private String name;
    private List<LocalPhone> localPhones;
    private boolean isMatch;
    private String pinyin;
    private boolean checked = false;
    private boolean hasPinYin; // 记录拼音出现的位置

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocalPhone> getLocalPhones() {
        return localPhones;
    }

    public void setLocalPhones(List<LocalPhone> localPhones) {
        this.localPhones = localPhones;
    }

    public boolean isMatch() {
        return isMatch;
    }

    public void setMatch(boolean isMatch) {
        this.isMatch = isMatch;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isHasPinYin() {
        return hasPinYin;
    }

    public void setHasPinYin(boolean hasPinYin) {
        this.hasPinYin = hasPinYin;
    }
}
