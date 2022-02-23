
package com.itsoft.site.blocker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KeywordModel {

    @SerializedName("keywords")
    @Expose
    private String keywords;

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
