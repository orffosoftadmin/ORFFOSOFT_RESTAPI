package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.itemWiseClosingStockDTO;
import co.orffosoft.service.ItemwiseClosingsStockService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/itemWiseClosingStockController")
public class ItemWiseClosingStockController {
	
	@Autowired
	ItemwiseClosingsStockService itemwiseClosingsStockService;
	
	@RequestMapping(value = "/generetreport", method = RequestMethod.POST)
	public BaseDTO create(@RequestBody itemWiseClosingStockDTO dto) {
		log.info("ItemWiseClosingStockController  create---------#Start");
		BaseDTO baseDto = itemwiseClosingsStockService.generetReport(dto);
		
		return baseDto;

	}

}
