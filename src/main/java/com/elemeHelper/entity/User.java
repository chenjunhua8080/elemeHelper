package com.elemeHelper.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String pass;
	private int type;
	private Date createDate;
	private int creatorId;
	private int datalevel;

	public User() {
	}

	public User(String name, String pass) {
		this.name = name;
		this.pass = pass;
	}

	public User(Long id, String name, String pass) {
		this.id = id;
		this.name = name;
		this.pass = pass;
	}

	public User(String name, String pass, int type) {
		this.name = name;
		this.pass = pass;
		this.type = type;
	}

	public User(Long id, String name, String pass, int type) {
		this.id = id;
		this.name = name;
		this.pass = pass;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	public int getDatalevel() {
		return datalevel;
	}

	public void setDatalevel(int datalevel) {
		this.datalevel = datalevel;
	}

}
