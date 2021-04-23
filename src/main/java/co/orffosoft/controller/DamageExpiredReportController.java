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
import co.orffosoft.dto.DamageExpiredReportDto;
import co.orffosoft.dto.itemWiseClosingStockDTO;
import co.orffosoft.service.DamageExpiredReportService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/damageexpiredreportController")
@Log4j2
public class DamageExpiredReportController {
	
	@Autowired
	DamageExpiredReportService damageExpiredReportService;
	
	@RequestMapping(value = "/itemAutoSearch/{itemname}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> itemAutoSearch(@PathVariable String itemname) {
		log.info("itemautosearch Controller Receive ");
		BaseDTO baseDTO = damageExpiredReportService.itemAutoSearch(itemname);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/generetreport", method = RequestMethod.POST)
	public BaseDTO create(@RequestBody DamageExpiredReportDto dto) {
		log.info("ItemWiseClosingStockController  create---------#Start");
		BaseDTO baseDto = damageExpiredReportService.generetReport(dto);
		
		return baseDto;

	}

}
