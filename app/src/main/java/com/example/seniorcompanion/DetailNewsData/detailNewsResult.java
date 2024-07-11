package com.example.seniorcompanion.DetailNewsData;

public class detailNewsResult {
    private String uniquekey;
    private detailNewsthing detail;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public detailNewsthing getDetail() {
        return detail;
    }

    public void setDetail(detailNewsthing detail) {
        this.detail = detail;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

}

