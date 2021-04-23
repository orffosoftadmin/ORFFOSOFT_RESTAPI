package co.orffosoft.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.ProductVarietyMaster;

public interface ProductVarietyMasterRepository extends JpaRepository<ProductVarietyMaster, Long> {
	
	@Query(nativeQuery = true, value = " select * from product_variety_master em where em.user_id =?2 and  em.name =?1 or em.code =?3")
	ProductVarietyMaster findByItemCodeNameId(String name, Long userId, String skuid);

//	@Query("SELECT em FROM ProductVarietyMaster em WHERE lower(em.name) = lower(?1) and em.userId =?2")
//	ProductVarietyMaster findByName(String name, Long userId);
	
	@Query("SELECT em FROM ProductVarietyMaster em WHERE lower(em.name) = lower(?1) and em.createdBy.entityId =?2")
	ProductVarietyMaster findNameByEntity(String name, Long entityId);
	
	@Query("SELECT em FROM ProductVarietyMaster em WHERE lower(em.code) = lower(?1) and em.createdBy.entityId =?2")
	ProductVarietyMaster findCodeByEntity(String code, Long entityId);

	@Query("SELECT em FROM ProductVarietyMaster em WHERE lower(em.name) = lower(?1) and userId =?2")
	ProductVarietyMaster findNameByUser(String name, Long userId);

	@Query("SELECT em FROM ProductVarietyMaster em WHERE lower(em.code) = lower(?1) and em.userId=?2")
	ProductVarietyMaster findCodeByUser(String code , long id);

	@Query("Select s from ProductVarietyMaster s WHERE s.activeStatus = true ORDER BY s.id desc")
	List<ProductVarietyMaster> getProductList();

	@Query("SELECT em FROM ProductVarietyMaster em WHERE lower(em.lname) = lower(?1)")
	ProductVarietyMaster findByLname(String lname);

	@Query(nativeQuery = true, value = "select * from product_variety_master  where id=?1 and active_status=true;")
	ProductVarietyMaster findByIdAndStatus(Long id);

	@Query("SELECT dm FROM ProductVarietyMaster dm WHERE dm.id IN :inclList")
	List<ProductVarietyMaster> findByVarityList(List<Long> varityList);

	@Query(nativeQuery = true, value = "select * from product_variety_master where id in (select product_id from retail_procurement_order_details where input_form_id in (select id from retail_procurement_order where id=?1))")
	List<ProductVarietyMaster> findByRetailProcurementOrderId(Long id);

	@Query(nativeQuery = true, value = "select * from product_variety_master where group_id in (select id from product_group_master where category_id=?1)")
	List<ProductVarietyMaster> getItemCodeName(Long id);

	@Query(nativeQuery = true, value = "select * from product_variety_master prov inner join product_group_master progro on "
			+ "  prov.group_id = progro.category_id inner join product_category proC on "
			+ "  progro.category_id = proC.category_group_id inner join product_category_group proCG on "
			+ "  proC.category_group_id = proCG.id where prov.active_status=true")
	List<ProductVarietyMaster> getProductVarietyItemInward();

	@Query(nativeQuery = true, value = "select distinct p.* from product_variety_master p,govt_scheme_plan_items s where s.product_id=p.id and p.group_id=?1 and p.active_status=true;")
	List<ProductVarietyMaster> findLoadProductByGroup(Long groupId);

	@Query(nativeQuery = true, value = "select * from product_variety_master p where p.active_status=true and p.group_id=:id")
	List<ProductVarietyMaster> getAllProductVarietyMaster(@Param("id") Long id);

	@Query(nativeQuery = true, value = "select * from product_variety_master p where p.active_status=true and p.group_id=:id order by p.name")
	List<ProductVarietyMaster> getAllProductVarietyMasterAlphabetically(@Param("id") Long id);

	@Query(nativeQuery = true, value = "select * from product_variety_master p where p.id=:id")
	ProductVarietyMaster getSelectedProductVarietyMaster(@Param("id") Long id);

	@Query(nativeQuery = true, value = "select * from product_variety_master where id in (select product_variety_id from inventory_items) and group_id=:grp and active_status=true")
	List<ProductVarietyMaster> findByGroupAndInventoryItems(@Param("grp") Long groupId);

	@Query(nativeQuery = true, value = "select pm.* from  product_variety_master pm join product_group_master pg on pg.id=pm.group_id \n"
			+ "	join product_category pc on pg.category_id=pc.id where pc.id=?1 and pg.id=?2")
	List<ProductVarietyMaster> getProductVarietyByCategoryAndGroup(Long productcat, Long productgroup);

	@Query(value = "SELECT pvm.id FROM product_variety_master pvm WHERE pvm.code = :itemCode", nativeQuery = true)
	Long getProductIdByCode(@Param("itemCode") String itemCode);

	/*
	 * select pvm.code from product_variety_master pvm, ecomm_user_cart euc,
	 * ecomm_product_config epc where pvm.id=epc.product_id and
	 * epc.id=euc.product_config_id and euc.product_config_id=62 limit 1;
	 */

	@Query(value = "SELECT pvm.code FROM product_variety_master pvm, ecomm_user_cart euc, ecomm_product_config epc WHERE pvm.id=epc.product_id AND epc.id=euc.product_config_id AND euc.product_config_id=:itemConfigId limit 1", nativeQuery = true)
	String getProductCodeByItemConfigId(@Param("itemConfigId") Long itemConfigId);

	@Query(value = "select * from product_variety_master where (name ilike :itemName or code ilike :itemName)  and entityid = :entityId", nativeQuery = true)
	List<ProductVarietyMaster> autoCompleteItem(@Param("itemName") String itemName, @Param("entityId") Long entityId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE ProductVarietyMaster up SET up.activeStatus=?2  where up.id=?1")
	void updateItemStatus(Long detilsPk, boolean status);

	@Query("SELECT pm FROM ProductVarietyMaster pm where lower(pm.code) = lower(?1) and pm.createdBy.entityId=?2")
	List<ProductVarietyMaster> checkDuplicateItemCode(String lowerCase, Long entityId);

	@Query("SELECT sm FROM ProductVarietyMaster sm where sm.name=?1 and sm.createdBy.entityId=?2")
	List<ProductVarietyMaster> checkDuplicateItmName(String lowerCase, Long entityId);
//	
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
	

}
