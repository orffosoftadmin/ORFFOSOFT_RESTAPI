package co.orffosoft.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.QrCodeItemDTO;
import co.orffosoft.entity.GoodsReceiptNote_D;
import co.orffosoft.entity.GoodsReceiptNote_H;
import co.orffosoft.repository.GoodsReceiptNote_D_Repository;
import co.orffosoft.repository.GoodsReceiptNote_H_Repository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class QRCodeGeneratorService {
	@Autowired
	LoginService loginService;
	
	@Autowired
	GoodsReceiptNote_H_Repository goodsReceiptNote_H_Repository;
	
	@Autowired
	GoodsReceiptNote_D_Repository goodsReceiptNote_D_Repository;
	
	
	
	public BaseDTO getGRNs(){
		BaseDTO basedto = new BaseDTO();
		try {
			long l=loginService.getCurrentUser().getEntityId();
			List<GoodsReceiptNote_H> grnh=new ArrayList();
			grnh=goodsReceiptNote_H_Repository.get_grn_based_on_entityid(l);
			
			if(!grnh.isEmpty()) {
				basedto.setResponseContents(grnh);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return basedto;
	}
	
	public BaseDTO getGRNdetail(QrCodeItemDTO qrCodeItemDTO){
		BaseDTO basedto = new BaseDTO();
		try {
			long l=loginService.getCurrentUser().getEntityId();
			List<GoodsReceiptNote_D> grnd=new ArrayList();
			grnd=goodsReceiptNote_D_Repository.get_grn_based_on_grnnumber(l, qrCodeItemDTO.getGoodsReceiptNote_H().getGrn_h_id());
			
			if(!grnd.isEmpty()) {
				basedto.setResponseContents(grnd);
			}
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return basedto;
	}

}
