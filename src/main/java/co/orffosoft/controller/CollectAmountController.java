package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.CollectAmountDTO;
import co.orffosoft.dto.CustomerBillingDTO;
import co.orffosoft.service.CollectAmountService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/collectamountcontroller")
@Log4j2
public class CollectAmountController {
	
	@Autowired
	CollectAmountService collectAmountService;
	
	@RequestMapping(value="/genereatreport",method=RequestMethod.POST)
	public ResponseEntity<BaseDTO> genereatReport(@RequestBody CollectAmountDTO collectamountdto){
		log.info("genereatReport Controller Receive ");
		BaseDTO baseDTO=collectAmountService.genereatreport(collectamountdto);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value="/getuser",method=RequestMethod.GET)
	public ResponseEntity<BaseDTO> getuser(){
		log.info("genereatReport Controller Receive ");
		BaseDTO baseDTO=collectAmountService.getusers();
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
}
