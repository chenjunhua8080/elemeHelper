package com.elemeHelper.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elemeHelper.entity.Cookie;

public interface CookieDao extends JpaRepository<Cookie, Long>{

	List<Cookie> getAllByDatalevelNotAndCountLessThan(int datalevel,int count);
	
	List<Cookie> getAllByDatalevelNotAndCreatorId(int datalevel,Long creatorId);
}
