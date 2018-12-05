package com.elemeHelper.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table
public class SignPrize {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//外键不能为空，默认sign_id
	@ManyToOne(optional = true)
	@JoinColumn(name = "sign_id")
	private Sign sign;
	private Date createDate;
	private Long creatorId;
	private int datalevel;
	private String value;
	private String text;
	
	
	public SignPrize() {
	}

	public SignPrize( Long creatorId, Sign sign, String value, String text) {
		this.creatorId = creatorId;
		this.sign = sign;
		this.createDate = new Date();
		this.datalevel = 0;
		this.value = value;
		this.text = text;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Sign getSign() {
		return sign;
	}
	public void setSign(Sign sign) {
		this.sign = sign;
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
	
	
	
}
