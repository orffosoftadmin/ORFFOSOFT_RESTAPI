package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.GoodsReceiptNote_D;
import co.orffosoft.entity.GoodsReceiptNote_H;

public interface GoodsReceiptNote_D_Repository extends JpaRepository<GoodsReceiptNote_D, Long>  {
	
	@Query( nativeQuery = true , value = "select * from stock_transaction st where st.stock_tran_sku_id_fk =:skuId order by stock_tran_pk desc limit 1")
	GoodsReceiptNote_D getSkuId(@Param("skuId") Long skuId);
	
	@Query( "select gd  from GoodsReceiptNote_D gd where gd.goodsReceiptNote_H.grn_h_id =:grnHid")
	List<GoodsReceiptNote_D> getGoodsReceipt_D_byHID(@Param("grnHid") Long grnHids);
	
	
	@Query( nativeQuery = true , value = " select * from  goods_receipt_note_d grnd where grnd.grn_d_grn_h_id_fk =:grnhid ")
	List<GoodsReceiptNote_D> getdata(@Param("grnhid") Long grnhid);
	
	 @Query(nativeQuery = true , value ="select * from goods_receipt_note_d grnd where grnd.grn_store_entity_fk =:entityid and grnd.grn_d_grn_h_id_fk =:grnhid")
	   List<GoodsReceiptNote_D> get_grn_based_on_grnnumber(@Param("entityid") Long entityid ,@Param("grnhid") Long grnhid);
	   
	   @Query(nativeQuery = true , value =" select pvm.name,grnd.grn_d_item_qnty as orderqnty,grnd.grn_d_accepted_qnty as acceptqnty, grnd.grn_d_purchase_amount,grnd.grn_d_selling_amount,grnd.grn_d_discount_amount,grnd.grn_d_mrp"
	   		+ " from goods_receipt_note_d grnd inner join product_variety_master pvm on pvm.id=grnd.grn_d_sku_id_fk " + 
	   		" where grnd.grn_d_grn_h_id_fk=:grnhid and grnd.grn_store_entity_fk=:entityid")
	   List<Object[]> getOrder_qnty(@Param("entityid") Long entityid ,@Param("grnhid") Long grnhid);
}
