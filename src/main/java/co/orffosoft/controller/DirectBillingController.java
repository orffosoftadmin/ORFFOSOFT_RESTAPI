package co.orffosoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.DirectBillingDTO;
import co.orffosoft.service.DirectBillingService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/directBillingController")
public class DirectBillingController {
	
	@Autowired
	DirectBillingService directBillingService;
	
	@RequestMapping(method = RequestMethod.POST, path = "/save")
	public ResponseEntity<BaseDTO> save(@RequestBody List<DirectBillingDTO> directBillingDTO) {
		log.info("<--Starts DirectBillingController .save-->");
		BaseDTO baseDTO = directBillingService.save(directBillingDTO);
		log.info("<--Ends DirectBillingController .save-->");
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);

	}
	
	@RequestMapping(value = "/autocompletemobile/{mobileNo}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> autoCompleteMobileNo(@PathVariable String mobileNo) {
		log.info("generateATNumber Controller Receive ");
		BaseDTO baseDTO = directBillingService.autoCompleteMobileNo(mobileNo);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}

}
