package co.orffosoft.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.orffosoft.entity.SalesReturn;

public interface SalesReturnRepository extends JpaRepository<SalesReturn, Long>  {
	
   	@Query(nativeQuery = true , value = "select sum(sr.net_total) from sales_return sr where sr.return_date=?1 and sr.created_by=?2")
	Double TodayReturnAmount(Date date, Long userid);
   	
  	@Query(nativeQuery = true , value = " select pvm.name,sri.item_qty as returnqnty,sri.net_amount as returnamt,sri.created_date,um.username from bill_d billd      " + 
  			"  			  inner join sales_return_items sri on sri.bill_d_fk=billd.bill_d_pk      " + 
  			"  			  inner join product_variety_master pvm on pvm.id=bill_d_sku_fk      " + 
  			"  			  inner join user_master um on um.id=sri.created_by " + 
  			"  			  where billd.bill_d_bill_h_fk=?1")
	List<Object[]> ReturnItems( Long userid);
	
	
	
	@Query(nativeQuery = true, value = " select 'Sale Amount' as Rtype, billh.bill_h_date as transactionDate, um.username as billedUserName, sum(billh.bill_h_net_amount) as totalBilledAmouunt,  "
			+ "sum(billh.collect_amount) as collectedAmt from bill_h billh "
			+ "inner join user_master um on um.id = billh.created_by "
			+ "where billh.bill_h_entity_fk = ?2  and billh.created_by = ?1 and billh.bill_h_date between ?3 and ?4 "
			+ "group by um.username, billh.bill_h_date " + " " + "union all  "
			+ "select 'Returned Amount' as Rtype, salesR.return_date as transactionDate, um1.username as returnedUserName , '0' as billedAmt,  "
			+ "sum(salesR.net_total) as returnedAmt  " + "from sales_return salesR "
			+ "inner join user_master um1 on um1.id = salesR.created_by "
			+ "where salesR.entity_fk = ?2 and salesR.created_by = ?1 "
			+ "group by um1.username, salesR.return_date")
	List<Object[]> collectamount( Long userid,Long entityid,Date from,Date to);
}
