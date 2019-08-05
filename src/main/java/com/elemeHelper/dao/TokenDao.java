package com.elemeHelper.dao;

import com.elemeHelper.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface TokenDao extends JpaRepository<Token, Long> {

    @Query(value = "select t.* from token t where t.id=(select max(a.id) from token a where a.type=?1 and a.creator_id=?2)", nativeQuery = true)
    Token getLastToken(int type, Long creatorId);

}
