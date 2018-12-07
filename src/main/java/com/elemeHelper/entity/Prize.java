package com.elemeHelper.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table
public class Prize {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date createDate;
	private Long creatorId;
	private int datalevel;
	private int sum_condition;
	private int amount;
	private int type;
	private String name;
	
	@JsonIgnore
	@ManyToOne(optional=true)
	private Sign sign;
	
	public Prize() {
	}

	public Prize(int sum_condition, int amount, int type, String name, Sign sign,Long creatorId) {
		this.sum_condition = sum_condition;
		this.amount = amount;
		this.type = type;
		this.name = name;
		this.sign = sign;
		this.createDate = new Date();
		this.creatorId = creatorId;
		this.datalevel = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public int getSum_condition() {
		return sum_condition;
	}

	public void setSum_condition(int sum_condition) {
		this.sum_condition = sum_condition;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sign getSign() {
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
	}



	
}
