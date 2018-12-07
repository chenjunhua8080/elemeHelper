package com.elemeHelper.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table
public class SignInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long cookieId;
	private Date createDate;
	private Long creatorId;
	private int datalevel;
	private int count;
	private boolean isAbort;
	private boolean isFinish;
	
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="signInfo",fetch=FetchType.LAZY)
	private List<Sign> signs;
	
	public SignInfo() {
	}

	public SignInfo(Long cookieId,Long creatorId) {
		this.cookieId=cookieId;
		this.creatorId = creatorId;
		this.createDate = new Date();
		this.datalevel = 0;
		this.count = 1;
		this.isAbort = false;
		this.isFinish = false;
	}



	public List<Sign> getSigns() {
		return signs;
	}

	public void setSigns(List<Sign> signs) {
		this.signs = signs;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getCookieId() {
		return cookieId;
	}

	public void setCookieId(Long cookieId) {
		this.cookieId = cookieId;
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public boolean isFinish() {
		return isFinish;
	}
	public boolean isAbort() {
		return isAbort;
	}
	public void setAbort(boolean isAbort) {
		this.isAbort = isAbort;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

	
}
