package co.orffosoft.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.UserMaster;

public interface UserMasterRepository extends JpaRepository<UserMaster, Long> {

	/**
	 * @param username
	 * @return
	 */
	@Query("SELECT u FROM UserMaster u WHERE u.username = ?1")
	UserMaster findByUsername(String username);

	/**
	 * @return
	 */
	@Query("SELECT u FROM UserMaster u where u.entityId =:entityId  ORDER BY u.id DESC")
	List<UserMaster> findAllUserByEntity(@Param("entityId") Long entityId);

	
	/**
	 * @param id
	 * @return
	 */
	@Query(nativeQuery = true, value = "select um.id as userId , um.username as userName , em.id as entityId , em.code as entityCode, em.name as entityName from user_master um join entity_master em on um.entity_id = em.id where um.id =?1")
	List<Object> findUserNameById(Long id);

	@Modifying
	@Transactional
	@Query(value = "UPDATE UserMaster up SET up.status=?2  where up.id=?1")
	void updateUserStatus(Long headerPk, Boolean inAtive);
	
	@Query(nativeQuery = true, value = "select um.id , um.username as username , em.id as emid , um.created_date as createddate , um.status as status from user_master um inner join entity_master em on um.entity_id=em.id where em.parent_id =?1 or um.entity_id =?1")
	List<Object> findUserName(Long eid);
	
	@Query(nativeQuery = true, value = " select um.id as userId , um.username as userName , em.id as entityId , em.code as entityCode, "
										+ " em.name as entityName , um.user_type as usertype , um.status as Status , um.created_by as creatdby , "
										+ " um.created_date as createddate from user_master um join entity_master em on um.entity_id = em.id "
										+ " where um.entity_id=?1 and um.id=?2 ")
	List<Object> findUserNameById1(Integer id , Integer eid);
	
	@Query(nativeQuery = true, value = " select um.parent_id from user_master um where um.id=?1 ")
	Long findUserParentId(Long id);
	
	@Query(nativeQuery = true, value = "select * from user_master um where um.parent_id=?1")
	List<UserMaster> findUserNameByrecurstion(Long id);
	
	@Query(nativeQuery = true, value = "select * from user_master um where um.mobile_number =:number")
	UserMaster findUserNameByNumber(@Param("number") String number);
	
	
	@Query(nativeQuery = true, value = "select * from user_master um inner join entity_master em on em.id=um.entity_id where um.mobile_number =:number and um.entity_id=:entityid " + 
			"and em.code=:storecode  ")
	UserMaster findnumberbasedstorecode(@Param("number") String number , @Param("storecode") String storecode,@Param("entityid") Long entityid);
	
	
	@Query(nativeQuery = true, value = "select um.id, um.username , em.name from user_master um " + 
			" inner join entity_master em on em.id=um.entity_id " + 
			" where um.id=:id or um.parent_id=:id ")
	List<Object> getuserforcollectionAmt(@Param("id") Long id);
}
