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
import co.orffosoft.dto.CustomerCreditNoteDTO;
import co.orffosoft.service.CustomerCreditNoteService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/customerCreditNoteController")
public class CustomerCreditNoteController {
	
	@Autowired
	CustomerCreditNoteService customerCreditNoteService;
	
	@RequestMapping(method = RequestMethod.POST, path = "/PaymentRecive")
	public BaseDTO PaymentRecive(@RequestBody List<CustomerCreditNoteDTO> customerCreditNoteDTO) {
		log.info("<--Starts CustomerCreditNoteController .create-->");
		BaseDTO baseDTO = customerCreditNoteService.PaymentRecive(customerCreditNoteDTO);
		log.info("<--Ends CustomerCreditNoteController .create-->");
		return  baseDTO;

	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/getdetailbybillno")
	public ResponseEntity<BaseDTO> getdetailbyBillno(@RequestBody CustomerCreditNoteDTO customerCreditNoteDTO) {
		log.info("<--Starts CustomerCreditNoteController .update-->");
		BaseDTO baseDTO = customerCreditNoteService.getdetailbyBillno(customerCreditNoteDTO);
		log.info("<--Ends SupplierMasterController .update-->");
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST, path = "/save")
	public ResponseEntity<BaseDTO> create(@RequestBody CustomerCreditNoteDTO customerCreditNoteDTO) {
		log.info("<--Starts CustomerCreditNoteController .create-->");
		BaseDTO baseDTO = customerCreditNoteService.save(customerCreditNoteDTO);
		log.info("<--Ends CustomerCreditNoteController .create-->");
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.POST, path = "/search")
	public BaseDTO search(@RequestBody CustomerCreditNoteDTO customerCreditNoteDTO) {
		log.info("<< CustomerCreditNoteController.search >>" + customerCreditNoteDTO);
		return customerCreditNoteService.search(customerCreditNoteDTO);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/view/{id}")
	public BaseDTO get(@PathVariable Long id) {
		log.info("<< CustomerCreditNoteController.get >>" + id);
		return customerCreditNoteService.get(id);
	}

	@RequestMapping(method = RequestMethod.POST, path = "/update")
	public ResponseEntity<BaseDTO> update(@RequestBody CustomerCreditNoteDTO customerCreditNoteDTO) {
		log.info("<--Starts CustomerCreditNoteController .update-->");
		BaseDTO baseDTO = customerCreditNoteService.update(customerCreditNoteDTO);
		log.info("<--Ends SupplierMasterController .update-->");
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);

	}

	@RequestMapping(method = RequestMethod.GET, path = "/delete/{id}")
	public BaseDTO delete(@PathVariable Long id) {
		log.info("<< CustomerCreditNoteController.delete >>" + id);
		return customerCreditNoteService.delete(id);
		
		

	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/getdetail")
	public BaseDTO getdetail(@RequestBody CustomerCreditNoteDTO customerCreditNoteDTO) {
		log.info("<< CustomerCreditNoteController.getdetail >>" + customerCreditNoteDTO);
		BaseDTO baseDTO=customerCreditNoteService.getdetail(customerCreditNoteDTO);
		return baseDTO;
	}
	
//	@RequestMapping(method = RequestMethod.POST, path = "/addamount")
//	public BaseDTO addamount(@RequestBody CustomerCreditNoteDTO customerCreditNoteDTO) {
//		log.info("<< CustomerCreditNoteController.addamount >>" + customerCreditNoteDTO);
//		BaseDTO baseDTO=customerCreditNoteService.addamount(customerCreditNoteDTO);
//		return baseDTO;
//	}
	
	@RequestMapping(value = "/autocompleteitemName/{itemname}", method = RequestMethod.GET)
	public ResponseEntity<BaseDTO> autoCompleteItem(@PathVariable("itemname") String itemName) {
		log.info("autocompleteitemName Controller Receive ");
		BaseDTO baseDTO = customerCreditNoteService.autoCompleteItem(itemName);
		return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
	}
	
	
}
