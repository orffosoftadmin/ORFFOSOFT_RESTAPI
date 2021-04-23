package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.orffosoft.entity.RoleMaster;

public interface RoleMasterRepository extends JpaRepository<RoleMaster, Long> {
	
	
	@Query("select r from RoleMaster r where r.status='true'")
	List<RoleMaster> findAllActiveRoleMaster();
	
	@Query("select r from RoleMaster r  order by r.createdDate desc,r.modifiedDate desc")
	List<RoleMaster> findAllRoleMaster();
	
	@Query("select r from RoleMaster r where r.status='true' and r.application.id = 2 and lower(r.roleName) = lower(?1)")
	RoleMaster findAllActiveRoleMaster(String userType);
	
	//@Query("select r from role_master r where r.status='true' and r.application_id = 2 and r.role_name=?1")
	//RoleMaster findAllActiveRoleMaster1(String userType);

}
