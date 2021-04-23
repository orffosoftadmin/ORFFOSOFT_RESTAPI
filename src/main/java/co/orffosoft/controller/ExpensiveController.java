package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.ExpensiveDTO;
import co.orffosoft.entity.CustomerMaster;
import co.orffosoft.service.ExpensiveService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/expensiveController")
public class ExpensiveController {
	
	@Autowired
	ExpensiveService expensiveService;
	
	
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> save(@RequestBody ExpensiveDTO expensivedto) {
		log.info("<--- Starts expensiveController update  --->");
		BaseDTO baseDto = expensiveService.save(expensivedto);
		log.info("<--- Ends expensiveController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> update(@RequestBody ExpensiveDTO expensivedto) {
		log.info("<--- Starts expensiveController update  --->");
		BaseDTO baseDto = expensiveService.update(expensivedto);
		log.info("<--- Ends expensiveController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}
	
	
	@RequestMapping(value = "/searchData", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> searchData(@RequestBody ExpensiveDTO expensivedto) {
		log.info("<--- Starts expensiveController update  --->");
		BaseDTO baseDto = expensiveService.searchData(expensivedto);
		log.info("<--- Ends expensiveController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}

}
