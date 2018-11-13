package com.elemeHelper.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.elemeHelper.entity.Cookie;

public interface CookieDao extends JpaRepository<Cookie, Long>{

	List<Cookie> getAllByDatalevelAndTypeAndCountLessThan(int datalevel,int type,int count);
	
	List<Cookie> getAllByDatalevelAndCreatorIdAndType(int datalevel,Long creatorId,int type);
	
	List<Cookie> getByDatalevelAndUserId(int datalevel,String userId);
	
	List<Cookie> getAllByDatalevelAndPhone(int datalevel,String phone);
	
	@Transactional
	@Modifying
	@Query("update Cookie c set c.datalevel=-1 where c.id=?1")
	int del(Long id);
	
	@Transactional
	@Modifying
	@Query("update Cookie c set c.count=c.count+1 where c.id=?1")
	int use(Long id);
}
