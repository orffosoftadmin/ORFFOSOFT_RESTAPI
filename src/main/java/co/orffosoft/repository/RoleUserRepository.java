package co.orffosoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.RoleUser;

public interface RoleUserRepository extends JpaRepository<RoleUser,Long> {
	
	@Query(value="select cust from RoleUser cust where cust.userMaster.id =?1")
	RoleUser findById(Long id);
	
	@Query( nativeQuery = true , value = " select * from role_user rs where rs.user_id =:userid ")
	RoleUser findbyidd(@Param("userid") Long userid);
	

}
