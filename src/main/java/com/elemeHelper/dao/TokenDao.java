package com.elemeHelper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elemeHelper.entity.Token;


public interface TokenDao extends JpaRepository<Token, Long>{
	
	@Query(value="select t.* from Token t where t.id=(select max(a.id) from Token a where a.type=:type and a.creator:userId)",nativeQuery=true)
	public Token getLastToken(int type,int userId);

}
