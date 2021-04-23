package co.orffosoft.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.CustomerCreditNoteHeader;
import co.orffosoft.entity.Expensive;
import co.orffosoft.entity.ProductVarietyMaster;

public interface ExpensiveRepository extends JpaRepository<Expensive, Long> {
	
	@Query(value="select * from expensive ex where  (ex.item_name ilike :itemname) and ex.date between :fromdate and :todate and ex.entity_id=:id", nativeQuery = true)
	List<Object> getdata(@Param("fromdate") Date fromdate,@Param("todate") Date todate,@Param("id") Long id,@Param("itemname") String itemname);
	
	@Query(value = "select ex.item_name from expensive ex where ex.item_name ilike :itemName and ex.entity_id=:entityId", nativeQuery = true)
	List<String> autoCompleteItem(@Param("itemName") String itemName, @Param("entityId") Long entityId);

}
