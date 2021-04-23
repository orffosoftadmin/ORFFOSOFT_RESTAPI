package co.orffosoft.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.Bill_D;

public interface Bill_D_Repository extends JpaRepository<Bill_D, Long> {
	
	
	@Query(value = " SELECT bd from Bill_D bd where bd.bill_d_bill_h_fk =:billHPk ")
	List<Bill_D> getBillDPk(@Param("billHPk") Long billHPk);
	
	@Query(nativeQuery = true , value = " select  " + 
			" abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) * st.stock_tran_sellingprice) as TotalUnitRate " + 
			" from bill_h bilH  " + 
			" inner join bill_d bilD on bilH.bill_h_pk = bilD.bill_d_bill_h_fk  " + 
			" inner join stock_transaction st  on st.bill_d_bill_h_fk=bilH.bill_h_pk  " + 
			" where  bilH.bill_h_date =?1  and  bilH.bill_h_entity_fk =?2  " + 
			" group by bilH.bill_h_date, st.stock_tran_sellingprice limit 1")
	Double getTodaySoldAmount(Date date, Long entityFk);
	
	@Query(nativeQuery = true , value = " select pvm.name,  abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty )) as qnty,st.stock_tran_sellingprice," + 
			"  abs( sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty ) * st.stock_tran_sellingprice) as netprice," + 
			"  billd.bill_d_discount_value  ,billd.bill_d_cgst_value,billd.bill_d_sgst_value  from bill_h billh  " + 
			" left join customer_master cm on cm.id=billh.bill_h_customer_fk    inner join user_master um on um.id=billh.created_by  " + 
			" inner join stock_transaction st on st.bill_d_bill_h_fk=billh.bill_h_pk " + 
			" inner join bill_d billd on billd.bill_d_bill_h_fk=billh.bill_h_pk " + 
			" inner join product_variety_master pvm on pvm.id=billd.bill_d_sku_fk" + 
			" where billh.bill_h_pk=:pk " + 
			" group by st.stock_tran_sellingprice,   billd.bill_d_discount_value ," + 
			" billd.bill_d_cgst_value,billd.bill_d_sgst_value,pvm.name ")
       List<Object[]> viewbilld(@Param("pk") Long id);
	
   	@Query(nativeQuery = true , value = "select abs(sum(billh.bill_h_net_amount)) from bill_h billh where billh.bill_h_date=?1 and billh.bill_h_entity_fk=?2")
	Double TodaySoldAmount(Date date, Long entityFk);
   	
 	@Query(nativeQuery = true , value = "select billh.bill_h_net_amount from bill_h billh where billh.bill_h_date=?1 and billh.bill_h_entity_fk=?2")
	List<Double> totalbill(Date date, Long entityFk);
	
}
