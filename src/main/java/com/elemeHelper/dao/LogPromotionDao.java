package com.elemeHelper.dao;

import com.elemeHelper.entity.LogPromotion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogPromotionDao extends JpaRepository<LogPromotion, Long> {

    List<LogPromotion> getListByDatalevelAndCreatorIdOrderByIdDesc(int datalevel, Long creatorId);

}
