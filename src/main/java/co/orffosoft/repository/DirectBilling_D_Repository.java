package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.DirectBilling_D;
import co.orffosoft.entity.GoodsReceiptNote_D;

public interface DirectBilling_D_Repository  extends JpaRepository<DirectBilling_D, Long> { 
	
	@Query( nativeQuery = true , value = " select * from direct_billing_d dbd where dbd.direct_billing_h_fk =:grnhid ")
	List<DirectBilling_D> getdata(@Param("grnhid") Long grnhid);

}
