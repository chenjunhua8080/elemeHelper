package com.elemeHelper.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elemeHelper.entity.LogPromotion;

public interface LogPromotionDao extends JpaRepository<LogPromotion	, Long>{
	
	List<LogPromotion> getListByDatalevelAndCreatorIdOrderByIdDesc(int datalevel,Long creatorId);
	
}
