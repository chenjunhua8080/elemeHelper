package com.elemeHelper.dao;

import com.elemeHelper.entity.logRedpacket;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRedpacketDao extends JpaRepository<logRedpacket, Long> {

    List<logRedpacket> getListByRedpacketIdAndOpenId(String redpacketId, String openId);

}
