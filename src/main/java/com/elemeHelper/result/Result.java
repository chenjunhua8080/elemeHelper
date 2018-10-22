package com.elemeHelper.result;

import java.util.Date;

public class Result {

	private int code;

	private String msg;

	private Object data;

	private long date;

	public Result() {
	}

	public Result(int code, String msg) {
		this.code = code;
		this.msg = msg;
		this.data = null;
		this.date = new Date().getTime();
	}

	public Result(Object data) {
		this.code = 0;
		this.msg = null;
		this.data = data;
		this.date = new Date().getTime();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

}
