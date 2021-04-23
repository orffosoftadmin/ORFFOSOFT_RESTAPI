package co.orffosoft.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.orffosoft.entity.GoodsReceiptNote_D;
import co.orffosoft.entity.Goods_Change_History;

public interface Goods_Change_History_Repository extends JpaRepository<Goods_Change_History, Long> {
	
	@Query( nativeQuery = true , value = " select * from goods_change_history gch where gch.grn_id =:grnhid ")
	List<Goods_Change_History> basedOnGrnhid(@Param("grnhid") Long grnhid);

}
