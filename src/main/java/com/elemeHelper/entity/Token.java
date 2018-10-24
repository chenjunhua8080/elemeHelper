package com.elemeHelper.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Token {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String token;
	private Date createDate;
	private int type;
	private Date overDate;
	private Long creatorId;
	private int datalevel;
	
	public Token() {
	}
	
	public Token(String token, Date createDate, Long creatorId, int datalevel, int type) {
		this.token = token;
		this.createDate = createDate;
		this.creatorId = creatorId;
		this.datalevel = datalevel;
		this.type = type;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getOverDate() {
		return overDate;
	}
	public void setOverDate(Date overDate) {
		this.overDate = overDate;
	}
	public long getCreatorId() {
		return creatorId;
	}
	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}
	public int getDatalevel() {
		return datalevel;
	}
	public void setDatalevel(int datalevel) {
		this.datalevel = datalevel;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	
}
