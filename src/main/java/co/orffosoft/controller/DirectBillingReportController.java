package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.DirectBillingReportDTO;
import co.orffosoft.dto.GoodsChangesHistoryReportDTO;
import co.orffosoft.entity.DirectBilling_H;
import co.orffosoft.service.DirectBillingReportSrevice;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/directBillingReportController")
@Log4j2
public class DirectBillingReportController {
	
	@Autowired
	DirectBillingReportSrevice directBillingReportSrevice;
	
	@RequestMapping(value = "/generetreport", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> GeneretReport(@RequestBody DirectBillingReportDTO dto) {
		log.info("generateATNumber Controller Receive ");
		BaseDTO baseDTO = directBillingReportSrevice.GeneretReport(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/showViewForm", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> showViewForm(@RequestBody DirectBilling_H directbill) {
		log.info("generateATNumber Controller Receive ");
		BaseDTO baseDTO = directBillingReportSrevice.showViewForm(directbill);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}

}
