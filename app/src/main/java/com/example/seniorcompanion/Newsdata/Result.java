package com.example.seniorcompanion.Newsdata;

import java.util.List;

public class Result {

    private String stat;
    private List<Data> data;
    private String page;
    private String pageSize;

    public List<Data> getData() {
        return data;
    }

    public void setDa0ta(List<Data> data) {
        this.data = data;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }


    }
