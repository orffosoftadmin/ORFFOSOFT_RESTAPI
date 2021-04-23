package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.ItemPrice;

public interface ItemPriceRepository extends JpaRepository<ItemPrice, Long> {

	@Query(nativeQuery = true, value = "select case when itm.itemprice_new_price >= 0 then itm.itemprice_new_price else  itm.itemprice_old_price end as price, "
			+ " itm.itemprice_pk "
			+ " from itemPrice itm where itm.itemprice_sku_fk =:skuId group by itm.itemprice_pk order by price  desc")
	List<Object> findSellingPriceByItemId(@Param("skuId") Long skuId);
	
//	@Query(nativeQuery = true, value = "select case when itm.itemprice_new_price >= 0 then itm.itemprice_new_price else  itm.itemprice_old_price end as price , itm.itemprice_purchase_price, itemprice_pk "
//			+ " from itemPrice itm where itm.itemprice_sku_fk =:skuId and item_price_store_entity_fk =:tostore ")
//	List<Object> findSellingPriceByItemId(@Param("skuId") Long skuId, @Param("tostore") Long tostore);
//	
	@Query(nativeQuery = true, value = "select case when itm.itemprice_new_price >= 0 then itm.itemprice_new_price else  itm.itemprice_old_price end as price ,  " + 
			" itm.itemprice_purchase_price, itemprice_pk,  " + 
			" sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) as rateWiseStock  " + 
			" from itemPrice itm  " + 
			" left join stock_transaction st on itm.itemprice_pk = st.stock_tran_item_price_fk " + 
			" where itm.itemprice_sku_fk =:skuId and item_price_store_entity_fk =:tostore " + 
			" group by itemprice_pk ")
	List<Object> findSellingPriceByItemId(@Param("skuId") Long skuId, @Param("tostore") Long tostore);
	
	@Query(nativeQuery = true, value = " select * from itemprice ip where ip.itemprice_sku_fk =:skuId " + 
			" and ip.itemprice_purchase_price =:purchasePrice and ip.itemprice_selling_price =:sellingPrice and ip.item_price_store_entity_fk =:entityfk")
	ItemPrice getItemPriceBasedOnFilter(@Param("skuId") Long skuId, @Param("purchasePrice") Double purchasePrice, 
										@Param("sellingPrice") Double sellingPrice,@Param("entityfk") Long entityfk);
	
	
	@Query(nativeQuery = true, value = "  select * from  itemprice itemprice where itemprice.itemprice_grn_h_fk =:grngid and itemprice.itemprice_sku_fk =:skuid "
										+ " and itemprice.itemprice_selling_price =:sellingPrice and  itemprice.itemprice_purchase_price =:purchasePrice ")
	ItemPrice getItemPrice(@Param("grngid") Long grnhid , @Param("skuid") Long skuid, @Param("sellingPrice") Double sellingPrice, @Param("purchasePrice") Double purchasePrice);
	
//	@Query(value = "select case when itm.itemprice_new_price >= 0 then itm.itemprice_new_price else  itm.itemprice_old_price end as price ,  " + 
//			" itm.itemprice_purchase_price, itemprice_pk,  " + 
//			" sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) as rateWiseStock, pvm.name  " + 
//			" ,pvm.id as itemId, pvm.cgst_percentage as cgstper, pvm.sgst_percentage as sgstper,  " + 
//			" pvm.hsn_code as hsnCode  " + 
//			" from itemPrice itm   " + 
//			" left join stock_transaction st on itm.itemprice_pk = st.stock_tran_item_price_fk  " + 
//			" left join product_variety_master pvm on pvm.id = itm.itemprice_sku_fk  " + 
//			" where item_price_store_entity_fk =:entityId and (name ilike :itemName or code ilike :itemName)  " + 
//			" group by itm.itemprice_purchase_price, itemprice_pk, pvm.name, pvm.id  ", nativeQuery = true)
//	List<Object> autoCompleteItemNameWithPrice(@Param("itemName") String itemName, @Param("entityId") Long entityId);

	@Query(value = "select case when itm.itemprice_new_price >= 0 then itm.itemprice_new_price else  itm.itemprice_old_price end as price ,  " + 
			" itm.itemprice_purchase_price, itemprice_pk,  " + 
			" itm.item_closing_stock as rateWiseStock, pvm.name  " + 
			" ,pvm.id as itemId, pvm.cgst_percentage as cgstper, pvm.sgst_percentage as sgstper,  " + 
			" pvm.hsn_code as hsnCode  " + 
			" from itemPrice itm   " + 
			" left join stock_transaction st on itm.itemprice_pk = st.stock_tran_item_price_fk  " + 
			" left join product_variety_master pvm on pvm.id = itm.itemprice_sku_fk  " + 
			" where item_price_store_entity_fk =:entityId and (name ilike :itemName or code ilike :itemName)  " + 
			" group by itm.itemprice_purchase_price, itemprice_pk, pvm.name, pvm.id  ", nativeQuery = true)
	List<Object> autoCompleteItemNameWithPrice(@Param("itemName") String itemName, @Param("entityId") Long entityId);


    @Query(nativeQuery = true, value = "select * from itemprice ip where ip.itemprice_purchase_price=:pp  and ip.itemprice_selling_price=:sp  and ip.itemprice_sku_fk=:itemid and ip.item_price_store_entity_fk=:entityid")
  ItemPrice sellingprice_purchedprice(@Param("itemid") Long itemid, @Param("sp") Double sp, @Param("pp") Double pp, @Param("entityid") Long entityid);
}
