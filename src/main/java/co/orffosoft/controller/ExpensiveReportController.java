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
import co.orffosoft.dto.ExpensiveDTO;
import co.orffosoft.service.ExpensiveReportService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("/expensivereportcontroller")
@Log4j2
public class ExpensiveReportController {
	
	@Autowired
	ExpensiveReportService expensiveReportService;
	
	@RequestMapping(value = "/generate", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> generate(@RequestBody ExpensiveDTO expensivedto) {
		log.info("<--- Starts expensiveController update  --->");
		BaseDTO baseDto = expensiveReportService.generateReport(expensivedto);
		log.info("<--- Ends expensiveController create  --->");
		if (baseDto != null) {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDto, HttpStatus.NO_CONTENT);
		}
	}
	
	@RequestMapping(value = "/getshopname/{id}/{pid}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> getShopName(@PathVariable("id") Long id , @PathVariable("pid") Long pid) {
		log.info("get Shopes names list Controller recive ");
		BaseDTO baseDTO = expensiveReportService.getShopNames(id,pid);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/autocompleteitemName/{itemname}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> autoCompleteItem(@PathVariable("itemname") String itemName) {
		log.info("generateATNumber Controller Receive ");
		BaseDTO baseDTO = expensiveReportService.autoCompleteItem(itemName);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}

}
