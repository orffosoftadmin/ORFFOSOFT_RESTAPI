package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.CustomerCreditNoteDetail;
import co.orffosoft.entity.CustomerCreditNoteHeader;

public interface CustomerCreditNoteDetailRepository extends JpaRepository<CustomerCreditNoteDetail, Long> {
	
	@Query(nativeQuery = true , value ="select * from customer_credit_note_detail cd where cd.cus_credit_note_h_fk =:custmerHpk")
    List<CustomerCreditNoteDetail> getdetail(@Param("custmerHpk") Long custmerHpk);
	
	@Query(nativeQuery = true , value =" select * from customer_credit_note_detail cnd where cnd.cus_credit_note_h_fk =:id ")
    List<CustomerCreditNoteDetail> getdetailofPayment(@Param("id") Long custmerHpk);
	
	@Query(nativeQuery = true , value =" select * from customer_credit_note_detail cnd where cnd.cus_credit_note_h_fk =:id order by cnd.created_date desc limit 1  ")
    CustomerCreditNoteDetail updateLastPaidAmt(@Param("id") Long custmerHpk);
	
	
	@Query(nativeQuery = true , value =" select cnd.trnsaction_date,cnd.transaction_type,cnd.bill_h_bill_no,cnd.credit_amount from customer_credit_note_detail cnd where cnd.bill_h_bill_no=:billno ")
    List<Object> getdetailbybillno(@Param("billno") String billno);
	
	@Query(nativeQuery = true, value = " select ccd.bill_h_bill_no,ccd.trnsaction_date,ccd.credit_amount,ccd.paid_amount ,ccd.transaction_type,um.username  "
			+ "from customer_credit_note_detail ccd "
			+ " inner join user_master um on um.id=ccd.created_by "
			+ "where ccd.cus_credit_note_h_fk=:id  "
			+ "order by ccd.credit_amount desc")
    List<Object> getdetailbycredit_h_pk(@Param("id") Long id);
	
	@Query(value="select abs(sum(ccd.credit_amount)) as creditamt, abs(sum(ccd.paid_amount)) from customer_credit_note_detail ccd where ccd.entity_id =:id", nativeQuery = true)
	List<Object> getpaymentdetails(@Param("id") Long id);
	
}
