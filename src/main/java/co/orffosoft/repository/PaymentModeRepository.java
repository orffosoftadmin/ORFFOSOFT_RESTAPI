package co.orffosoft.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.dto.PaymentMode;

public interface PaymentModeRepository extends JpaRepository<PaymentMode, Long> {
	
	   @Query(nativeQuery = true , value =" select pm.id from payment_mode  pm where pm.payment_mode=:mode ")
	  Integer paymentmode(@Param("mode") String mode);

}
