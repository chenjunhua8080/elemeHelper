package com.elemeHelper.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.elemeHelper.entity.Sign;

public interface SignDao extends JpaRepository<Sign, Long>{
	
	@Query(value="select t.* from Sign t "
			+ "where t.creator_id=?1 "
			+ "and t.cookie_id=?2 "
			+ "and t.datalevel=0 "
			+ "and t.is_abort =false "
			+ "and t.is_finish =false "
			+ "order by id desc",nativeQuery=true)
	List<Sign> getAllSigning(Long creatorId,Long cookieId);

	List<Sign> getAllByCreatorIdOrderByIdDesc(Long creatorId);
}
