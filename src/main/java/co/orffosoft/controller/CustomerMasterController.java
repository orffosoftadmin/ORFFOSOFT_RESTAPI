package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.CustomerMasterDTO;
import co.orffosoft.entity.CustomerMaster;
import co.orffosoft.service.CustomerMasterService;
import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@RequestMapping("/customerMasterController")
public class CustomerMasterController {

	@Autowired
	CustomerMasterService customerMasterService;
	
	
	
	@RequestMapping(value = "/getall", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> getall(@RequestBody CustomerMaster customerMaster) {
		log.info("<--- Starts CustomerMasterController update  --->");
		BaseDTO baseDto = customerMasterService.getall(customerMaster);
		log.info("<--- Ends CustomerMasterController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}
	
//	@RequestMapping(value = "/update", method = RequestMethod.POST)
//	public ResponseEntity<BaseDTO> update(@RequestBody CustomerMaster customerMaster) {
//		log.info("<--- Starts CustomerMasterController update  --->");
//		BaseDTO baseDto = customerMasterService.update(customerMaster);
//		log.info("<--- Ends CustomerMasterController create  --->");
//		if (baseDto != null) {
//			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
//		} else {
//			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
//		}
//	}
	
	@RequestMapping(value = "/checkmobileno/{mobno}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> checkmoblieno(@PathVariable String mobno) {
		log.info("<--- Starts CustomerMasterController update  --->");
		BaseDTO baseDto = customerMasterService.checkmobileno(mobno);
		log.info("<--- Ends CustomerMasterController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value = "/addorupdate", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> add(@RequestBody CustomerMaster customerMaster) {
		log.info("<--- Starts CustomerMasterController update  --->");
		BaseDTO baseDto = customerMasterService.addOrUpdate(customerMaster);
		log.info("<--- Ends CustomerMasterController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}

}
