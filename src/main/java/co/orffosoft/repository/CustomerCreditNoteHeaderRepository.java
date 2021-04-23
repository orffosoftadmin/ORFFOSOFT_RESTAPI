package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.CustomerCreditNoteHeader;

public interface CustomerCreditNoteHeaderRepository extends JpaRepository<CustomerCreditNoteHeader, Long> {

//	@Query(value="SELECT * FROM customer_credit_note_header ccnh WHERE ccnh.cus_mob_no=:customerMobileNo and ccnh.cus_name=:customerName and ccnh.created_by=:id", nativeQuery = true)
//	CustomerCreditNoteHeader checkDuplicateMobileNumber(@Param("customerMobileNo") Long customerMobileNo, @Param("customerName") String customerName,@Param("id") Long id);
//	
//	@Query("SELECT ccnh FROM CustomerCreditNoteHeader ccnh WHERE ccnh.cusCreditBillNo = :billno and ccnh.cusMobileNo= :customerMobileNo")
//	CustomerCreditNoteHeader getdetail(@Param("customerMobileNo") Long customerMobileNo, @Param("billno") String billno);
//	
//	@Query("SELECT ccnh FROM CustomerCreditNoteHeader ccnh WHERE ccnh.cus_Name = :name and ccnh.cusMobileNo= :customerMobileNo and ccnh.created_by=:id")
//	CustomerCreditNoteHeader PaymentRecive(@Param("customerMobileNo") Long customerMobileNo, @Param("name") String name, @Param("id") Long id);
// 
//	
//	@Query("SELECT ccnh FROM CustomerCreditNoteHeader ccnh WHERE ccnh.cusMobileNo = :customerMobileNo")
//	CustomerCreditNoteHeader findDuplicateMobileNumber(@Param("customerMobileNo") Long customerMobileNo);
	
	
	@Query(value="select * from customer_credit_note_header ch where ch.customer_fk=:id", nativeQuery = true)
	CustomerCreditNoteHeader checkCustomer(@Param("id") Long id);
	
//	
//	@Query(value=" select ccd.trnsaction_date,ccd.transaction_type,ccd.bill_h_bill_no,ccd.credit_amount ,ccd.paid_amount,ccd.cus_credit_note_d_pk ,ccd.bill_h_fk,ccd.cus_credit_note_h_fk" + 
//			" from customer_credit_note_detail ccd where ccd.cus_credit_note_h_fk= :id " + 
//			" group by ccd.trnsaction_date,ccd.transaction_type,ccd.bill_h_bill_no ,ccd.credit_amount ,ccd.paid_amount ,ccd.cus_credit_note_d_pk,ccd.bill_h_fk ,ccd.cus_credit_note_h_fk", nativeQuery = true)
//	List<Object> getdetail(@Param("id") Long id);
	
	
	@Query(value="  select t1.transaction_type,t1.bill_h_bill_no,t1.creditamnt , " + 
			" t1.paid ,t1.bill_h_fk,t1.cus_credit_note_h_fk from  " + 
			" ( select ccd.transaction_type,ccd.bill_h_bill_no,Sum(ccd.credit_amount) as creditamnt, " + 
			" sum(ccd.paid_amount) as paid ,ccd.bill_h_fk,ccd.cus_credit_note_h_fk " + 
			" from customer_credit_note_detail ccd where ccd.cus_credit_note_h_fk=:id " + 
			" group by ccd.transaction_type,ccd.bill_h_bill_no  " + 
			" ,ccd.bill_h_fk,ccd.cus_credit_note_h_fk ) as t1 " + 
			" group by  t1.transaction_type,t1.bill_h_bill_no, " + 
			" t1.paid ,t1.bill_h_fk,t1.cus_credit_note_h_fk,t1.creditamnt", nativeQuery = true)
	List<Object> getdetail(@Param("id") Long id);
	
	


}
