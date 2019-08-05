package com.elemeHelper.result;

public class PageResult {

    private String page;

    private String msg;

    private Object data;

    public PageResult() {
    }

    public PageResult(String page, Object data) {
        this.page = page;
        this.data = data;
    }

    public PageResult(String page, String msg) {
        this.page = page;
        this.msg = msg;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}
