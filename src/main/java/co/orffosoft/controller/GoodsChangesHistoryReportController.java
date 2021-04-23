package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.GoodsChangesHistoryReportDTO;
import co.orffosoft.entity.GoodsReceiptNote_H;
import co.orffosoft.service.GoodsChangesHistoryReportService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/goodschangeshistoryReportcontroller")
@Log4j2
public class GoodsChangesHistoryReportController {
	
	@Autowired
	GoodsChangesHistoryReportService goodsChangesHistoryReportService;
	
	@RequestMapping(value = "/generetreport", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> GeneretReport(@RequestBody GoodsChangesHistoryReportDTO dto) {
		log.info("generateATNumber Controller Receive ");
		BaseDTO baseDTO = goodsChangesHistoryReportService.GeneretReport(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/autosearch/{itemname}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> Autosearch(@PathVariable("itemname") String Name) {
		log.info("generateATNumber Controller Receive ");
		BaseDTO baseDTO = goodsChangesHistoryReportService.Autosearch(Name);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/showviewform", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> showViewForm(@RequestBody GoodsReceiptNote_H dto) {
		log.info("generateshowviewfrom Controller Receive ");
		BaseDTO baseDTO = goodsChangesHistoryReportService.showViewForm(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
}
