package com.elemeHelper.result;

public class PageResult {

	private String page;

	private Object data;

	public PageResult() {
	}

	public PageResult(String page, Object data) {
		this.page = page;
		this.data = data;
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

}
