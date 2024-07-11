package com.example.seniorcompanion.DetailNewsData;
//新闻详情 json数据接受
public class detailNewsResponse {
    private String reason;
    private detailNewsResult result;
    private int error_code;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public detailNewsResult getResult() {
        return result;
    }

    public void setResult(detailNewsResult result) {
        this.result = result;
    }

}
