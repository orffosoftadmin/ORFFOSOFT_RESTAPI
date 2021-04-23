package co.orffosoft.repository;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.StockTransaction;


public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
	
	@Query( nativeQuery = true , value = "select * from stock_transaction st where st.stock_tran_sku_id_fk =:skuId order by stock_tran_pk desc limit 1")
	StockTransaction getSkuId(@Param("skuId") Long skuId);
	
	@Query(nativeQuery = true , value = "select stock_tran_supplier_id_fk ,st.stock_tran_sku_id_fk from stock_transaction st "
			                           + " where st.stock_tran_sku_id_fk =:itemId and st.created_by =:userId group by st.stock_tran_sku_id_fk , st.stock_tran_supplier_id_fk")
	List<Object[]> findAllSupplierIdAndItemId(@Param("itemId") Long itemId , @Param("userId") Long userId);
	
	@Query(nativeQuery = true , value = " select sum(st.stock_tran_received_qnty-st.stock_tran_issued_qnty) as closingBal "
			                           + " from stock_transaction st where st.stock_tran_sku_id_fk =:itemId and st.stock_tran_store_entity_fk =:tostore ")
	Long findClosingBalanceBasedOnSupplierIdAndItem(@Param("itemId") Long itemId, @Param("tostore") Long tostore);
	
	@Query( nativeQuery = true , value = "select * from stock_transaction st where st.stock_tran_sku_id_fk =:skuId")
	List<StockTransaction> getTransactionDetailsBySkuId(@Param("skuId") Long skuId);
	
//	@Query(nativeQuery = true , value = " select sum(st.stock_tran_received_qnty-st.stock_tran_issued_qnty) as closingBal "
//            + " from stock_transaction st where st.stock_tran_item_price_fk in (:itemPricePk) ")
//    Long findClosingBalanceBasedOnItemPricePk(@Param("itemPricePk") List<Long> itemPricePk);

	@Query(nativeQuery = true , value = " select item.item_closing_stock as closingBal "
            + " from itemprice item where item.itemprice_pk in (:itemPricePk) ")
    Long findClosingBalanceBasedOnItemPricePk(@Param("itemPricePk") List<Long> itemPricePk);
	
	@Query(nativeQuery = true, value = " select sum( st.stock_tran_received_qnty - st.stock_tran_issued_qnty ) QTY from stock_transaction st " + 
			" where st.stock_tran_item_price_fk =:itemPricePk ")
	Double findClosingQnty(@Param("itemPricePk") Long itemPricePk);

	@Query(nativeQuery = true, value = " select item.item_closing_stock as QTY from itemprice item where item.itemprice_pk in :list ")
	List<Object> findQTYByPrice(String list);
	
	@Query( nativeQuery = true , value = "select * from stock_transaction st where st.stock_tran_item_price_fk =:itemPriceFk and stock_tran_type_fk in (1,2)")
	StockTransaction getStockByItemPrice(@Param("itemPriceFk") Long itemPriceFk);
	
	@Query(nativeQuery = true, value = " select count(*) from  stock_transaction stock where stock.stock_tran_sku_id_fk =:skugid"
			+ " and stock.stock_tran_sellingprice =:sellingPrice and stock.stock_tran_type_fk != 1"
			+ " and stock.stock_tran_type_fk != 2 and stock.stock_tran_store_entity_fk =:toStore")
	Long checkItemMovementStatus(@Param("skugid") Long skuid, @Param("sellingPrice") Double sellingPrice, @Param("toStore") Long toStore);
	
	@Query(nativeQuery = true, value = " select pvm.name as ItemName, st.stock_tran_sellingprice as unitPrice, "
									   + " sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) as closingStk,st.stock_tran_purchaseprice   " 
									   + " from stock_transaction st   inner join  product_variety_master pvm on pvm.id=st.stock_tran_sku_id_fk "
									   + " where st.stock_tran_date <=:date and st.stock_tran_store_entity_fk = :entityId  "
									   + " group by pvm.name, st.stock_tran_sellingprice,st.stock_tran_purchaseprice  order by pvm.name ")
	List<Object[]> getClosingStockAsOn(@Param("date") Date date, @Param("entityId") Long entityId);

	
	@Query(nativeQuery = true, value = " select pvm.name as ItemName, st.stock_tran_sellingprice as unitPrice, "
			   + " sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) as closingStk,st.stock_tran_purchaseprice    " 
			   + " from stock_transaction st   inner join  product_variety_master pvm on pvm.id=st.stock_tran_sku_id_fk "
			   + " where st.stock_tran_date <=:date and st.stock_tran_store_entity_fk = :entityId and pvm.id =:skuId  "
			   + " group by pvm.name, st.stock_tran_sellingprice,st.stock_tran_purchaseprice   order by pvm.name ")
List<Object[]> getClosingStockAsOnWithName(@Param("date") Date date, @Param("entityId") Long entityId, @Param("skuId") Long skuId);
	
	
	@Query(nativeQuery = true, value = " select st.expiry_date from stock_transaction st where st.stock_tran_sku_id_fk =:id "
			+ " and  st.stock_tran_store_entity_fk =:entityId and st.expiry_date is not null group by st.expiry_date ")
	List<Date> getexpirydate(@Param("id") Long id, @Param("entityId") Long entityId);

	
	@Query(nativeQuery = true, value = " select pvm.name,abs(sum( st.stock_tran_issued_qnty - st.stock_tran_received_qnty )) as soldqty " + 
			" from stock_transaction st inner join product_variety_master pvm on pvm.id=st.stock_tran_sku_id_fk" + 
			" where st.stock_tran_type_fk=3 and st.stock_tran_store_entity_fk=:entityId group by  pvm.name order by soldqty desc limit 5")
List<Object[]> top_five_product( @Param("entityId") Long entityId);

@Query(nativeQuery = true, value = " select pvm.name,st.stock_tran_date ,st.expiry_date from stock_transaction st  " + 
		" inner join product_variety_master pvm on pvm.id=st.stock_tran_sku_id_fk  " + 
		" where st.stock_tran_store_entity_fk=:entityId and st.expiry_date is not null  " + 
		" and st.expiry_date >=:date and st.stock_tran_type_fk=1 " + 
		" group by  pvm.name,st.expiry_date,st.stock_tran_date " + 
		" order by st.expiry_date asc")
List<Object[]> expired_product( @Param("entityId") Long entityId,@Param("date") Date date);
	
}
