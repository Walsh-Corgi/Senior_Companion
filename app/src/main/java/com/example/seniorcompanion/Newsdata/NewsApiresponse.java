package com.example.seniorcompanion.Newsdata;
import com.google.gson.Gson;

//json数据接收
public class NewsApiresponse {
    private String reason;
    private Result result;
    private  int error_code;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    // Method to convert the object to a JSON string
    public String toJson() {
        return new Gson().toJson(this);
    }

}
