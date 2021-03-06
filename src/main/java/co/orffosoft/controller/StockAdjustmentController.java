package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.StockAdjustmentDTO;
import co.orffosoft.entity.ProductVarietyMaster;
import co.orffosoft.service.StockAdjustmentService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/stockAdjustmentController")
@Log4j2
public class StockAdjustmentController {
	
	@Autowired
	StockAdjustmentService stockAdjustmentService;
	
	@RequestMapping(value = "/generateReport", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> autoCompleteMobileNo(@RequestBody ProductVarietyMaster dto) {
		log.info("StockAdjustmentController Controller Receive ");
		BaseDTO baseDTO = stockAdjustmentService.generateReport(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/generatStockAdjustment", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> autoCompleteMobileNo(@RequestBody StockAdjustmentDTO dto) {
		log.info("StockAdjustmentController Controller Receive ");
		BaseDTO baseDTO = stockAdjustmentService.generatStockAdjustment(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
}
