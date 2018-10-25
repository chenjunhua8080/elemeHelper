package com.elemeHelper.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.elemeHelper.entity.User;

public interface UserDao extends JpaRepository<User, Long>{
	
	@Modifying
	@Query("update User u set u.datalevel=-1 where u.id=:id")
	public int delete(int id);
	
	public User getByName(String name);
	
	public User getByNameAndPassAndType(String name,String pass,int type);
	
	public User getByNameAndPassAndTypeAndCreatorId(String name,String pass,int type,Long creatorId);
	
	public User getByCreatorIdAndType(Long creatorId,int type);

}
