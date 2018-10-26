package com.elemeHelper.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Cookie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String cookie;
	private int type;
	private String account;
	private String phone;
	private Date createDate;
	private Long creatorId;
	private int datalevel;
	private int count;
	public Cookie() {
	}
	public Cookie(String cookie, int type, String account, String phone, Date createDate, Long creatorId,
			int datalevel) {
		this.cookie = cookie;
		this.type = type;
		this.account = account;
		this.phone = phone;
		this.createDate = createDate;
		this.creatorId = creatorId;
		this.datalevel = datalevel;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	public int getDatalevel() {
		return datalevel;
	}
	public void setDatalevel(int datalevel) {
		this.datalevel = datalevel;
	}

	
}
