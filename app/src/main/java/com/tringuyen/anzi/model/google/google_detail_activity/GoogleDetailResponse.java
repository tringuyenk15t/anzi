
package com.tringuyen.anzi.model.google.google_detail_activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GoogleDetailResponse {

    @SerializedName("html_attributions")
    @Expose
    private List<Object> htmlAttributions = new ArrayList<Object>();
    @SerializedName("result")
    @Expose
    private DetailResult result;
    @SerializedName("status")
    @Expose
    private String status;

    /**
     * 
     * @return
     *     The htmlAttributions
     */
    public List<Object> getHtmlAttributions() {
        return htmlAttributions;
    }

    /**
     * 
     * @param htmlAttributions
     *     The html_attributions
     */
    public void setHtmlAttributions(List<Object> htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    /**
     * 
     * @return
     *     The result
     */
    public DetailResult getResult() {
        return result;
    }

    /**
     * 
     * @param result
     *     The result
     */
    public void setResult(DetailResult result) {
        this.result = result;
    }

    /**
     * 
     * @return
     *     The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

}
