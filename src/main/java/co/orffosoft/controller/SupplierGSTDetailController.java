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
import co.orffosoft.dto.SupplierGST_Detail_DTO;
import co.orffosoft.service.SupplierGSTDetailSrevice;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("SupplierGSTdetailcontroller")
@Log4j2
public class SupplierGSTDetailController {
	
	@Autowired
	SupplierGSTDetailSrevice supplierGSTDetailSrevice;
	
	@RequestMapping(value = "/suplierAutoSearch/{supliername}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> suplierAutoSearch(@PathVariable String supliername) {
		log.info("supplierautosearch Controller Receive ");
		BaseDTO baseDTO = supplierGSTDetailSrevice.suplierAutoSearch(supliername);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> generate(@RequestBody SupplierGST_Detail_DTO dto) {
		log.info("generate control Receive in SupplierGSTdetailcontroller ");
		BaseDTO baseDTO = supplierGSTDetailSrevice.generate(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/viewbill", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> viewBill(@RequestBody SupplierGST_Detail_DTO dto) {
		log.info("generate control Receive in SupplierGSTdetailcontroller ");
		BaseDTO baseDTO = supplierGSTDetailSrevice.viewBillDetails(dto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}

}
