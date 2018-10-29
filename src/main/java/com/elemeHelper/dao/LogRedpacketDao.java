package com.elemeHelper.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elemeHelper.entity.logRedpacket;

public interface LogRedpacketDao extends JpaRepository<logRedpacket	, Long>{

	List<logRedpacket> getListByRedpacketIdAndOpenId(String redpacketId,String openId);
	
}
