package co.orffosoft.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.GoodsChangesHistoryReportDTO;
import co.orffosoft.entity.GoodsReceiptNote_D;
import co.orffosoft.entity.GoodsReceiptNote_H;
import co.orffosoft.entity.Goods_Change_History;
import co.orffosoft.repository.GoodsReceiptNote_D_Repository;
import co.orffosoft.repository.GoodsReceiptNote_H_Repository;
import co.orffosoft.repository.Goods_Change_History_Repository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class GoodsChangesHistoryReportService {

	@Autowired
	LoginService loginService;

	@Autowired
	GoodsReceiptNote_H_Repository grn_h_reposetory;
	
	@Autowired
	Goods_Change_History_Repository goods_Change_History_Repository;

	public BaseDTO GeneretReport(GoodsChangesHistoryReportDTO dto) {
		BaseDTO baseDto = new BaseDTO();
		List<GoodsReceiptNote_H> listgrnh = null;
		try {

			if (dto != null) {
				listgrnh = new ArrayList();
				GoodsReceiptNote_H grnh = new GoodsReceiptNote_H();
				long entityid = loginService.getCurrentUser().getEntityId();

				if (dto.getGrnh() != null) {
					grnh = grn_h_reposetory.findOne(dto.getGrnh().getGrn_h_id());
					if (grnh != null) {
						listgrnh.add(grnh);
					}
				} else {
					if (dto.getFromdate() != null && dto.getTodate() != null) {
						listgrnh=grn_h_reposetory.basedondate(dto.getFromdate(), dto.getTodate(), entityid);
					}

				}
				
			}
			baseDto.setResponseContents(listgrnh);
		} catch (Exception e) {
			log.error("Exception Occured In GoodsChangesHistoryReportService Service GeneretReport", e);
		}

		return baseDto;
	}

	public BaseDTO Autosearch(String Name) {
		BaseDTO baseDto = new BaseDTO();
		try {
			List<GoodsReceiptNote_H> listgoodsrecepitnoteh = new ArrayList();
			if (!Name.isEmpty()) {
				Name = "%" + Name.trim() + "%";
				Long entityid = loginService.getCurrentUser().getEntityId();

				listgoodsrecepitnoteh = grn_h_reposetory.autocomplet(Name, entityid);
				baseDto.setResponseContents(listgoodsrecepitnoteh);
			}

		} catch (Exception e) {
			log.error("Exception Occured In GoodsChangesHistoryReportService Service Autosearch", e);
		}

		return baseDto;
	}
	
	public BaseDTO showViewForm(GoodsReceiptNote_H grnh) {
		BaseDTO baseDto = new BaseDTO();
		List<Goods_Change_History> listgoodchange=new ArrayList();
		
		long l= grnh.getGrn_h_id();
		try {
			if(grnh!=null && grnh.getGrn_h_id()!=null) {
				listgoodchange=goods_Change_History_Repository.basedOnGrnhid( grnh.getGrn_h_id());
			}
			baseDto.setResponseContents(listgoodchange);
		}catch (Exception e) {
			log.error("Exception Occured In GoodsChangesHistoryReportService Service showViewForm", e);
		}
		return baseDto;
	}
}
