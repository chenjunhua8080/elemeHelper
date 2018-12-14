package com.elemeHelper.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table
public class Sign {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	//外键不能为空，默认sign_id
	@ManyToOne(optional = true)
	private SignInfo signInfo;
	private Date createDate;
	private Long creatorId;
	private int datalevel;
	private String value;
	private String text;
	
	@OneToMany(cascade=CascadeType.ALL,mappedBy="sign",fetch=FetchType.LAZY)
	private List<Prize> prizes;
	
	public Sign() {
	}
	public Sign( Long creatorId, SignInfo signInfo,String text) {
		this.creatorId = creatorId;
		this.signInfo = signInfo;
		this.createDate = new Date();
		this.datalevel = 0;
		this.text = text;
	}
	public Sign( Long creatorId, SignInfo signInfo,String text,String value) {
		this.creatorId = creatorId;
		this.signInfo = signInfo;
		this.createDate = new Date();
		this.datalevel = 0;
		this.text = text;
		this.value = value;
	}
	public Sign( Long creatorId, SignInfo signInfo) {
		this.creatorId = creatorId;
		this.signInfo = signInfo;
		this.createDate = new Date();
		this.datalevel = 0;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public SignInfo getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(SignInfo signInfo) {
		this.signInfo = signInfo;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public List<Prize> getPrizes() {
		return prizes;
	}

	public void setPrizes(List<Prize> prizes) {
		this.prizes = prizes;
	}
	
	
	
}
