package co.orffosoft.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import co.orffosoft.entity.GoodsReceiptNote_H;

public interface GoodsReceiptNote_H_Repository extends JpaRepository<GoodsReceiptNote_H, Long>  {
	
	@Query(nativeQuery = true , value =" select * from  goods_receipt_note_h grnh where grnh.grn_h_id =:grnHph")
    GoodsReceiptNote_H getdetail(@Param("grnHph") Long grnHpk);
	
	@Query(nativeQuery = true , value ="select * from goods_receipt_note_h grnh where grnh.grn_h_number ilike :name and grnh.grn_store_entity_fk =:entityid")
   List<GoodsReceiptNote_H> autocomplet(@Param("name") String name ,  @Param("entityid") Long entityid);
   
   @Query(nativeQuery = true , value ="select * from goods_receipt_note_h grnh where grnh.grn_h_date between :fromdate and :todate and grnh.grn_store_entity_fk =:entityid")
   List<GoodsReceiptNote_H> basedondate(@Param("fromdate") Date fromdate ,@Param("todate") Date todate , @Param("entityid") Long entityid);
   
   @Query(nativeQuery = true , value ="select * from goods_receipt_note_h grnh where grnh.grn_store_entity_fk =:entityid")
   List<GoodsReceiptNote_H> get_grn_based_on_entityid(@Param("entityid") Long entityid);

   
   @Query(nativeQuery = true , value =" select grnh.grn_h_id,grnh.grn_h_number,sm.code,sm. name ,grnh.grn_h_status,grnh.grn_h_date,um.username, "  + 
   		" grnh.modified_date,um.modified_date as md,em.code as codee,em.name as namee ,grnh.grn_h_gross_amount "  + 
   		" from product_variety_master pvm "  + 
   		" inner join goods_receipt_note_d grnd on grnd.grn_d_sku_id_fk=pvm.id "  + 
   		" inner join  goods_receipt_note_h grnh on grnh.grn_h_id=grnd.grn_d_grn_h_id_fk "  + 
   		" inner join supplier_master sm on sm.id=grnh.grn_h_supplier_id_fk "  + 
   		" inner join user_master um on um.id=grnh.grn_h_user_id "  + 
   		" inner join entity_master em on em.id=grnh.grn_store_entity_fk "  + 
   		" where pvm. name =:name and pvm.entityid=:entityid " )
   List<?> getgrn_itemname(@Param("entityid") Long entityid,@Param("name") String name);
   
  
}
