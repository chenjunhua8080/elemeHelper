package com.elemeHelper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elemeHelper.entity.Token;


public interface TokenDao extends JpaRepository<Token, Long>{
	
	@Query(value="select t.* from Token t where t.id=(select max(a.id) from Token a where a.type=?1 and a.creator_id=?2)",nativeQuery=true)
	public Token getLastToken(int type,Long creatorId);

}
