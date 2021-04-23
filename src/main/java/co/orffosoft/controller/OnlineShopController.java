package co.orffosoft.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.OnlineShopResponse;
import co.orffosoft.dto.ProductVariryMastersearchDTO;
import lombok.extern.log4j.Log4j2;
import co.orffosoft.service.OnlineShopService;


@Log4j2
@RestController
@RequestMapping("/onlineShopController")
public class OnlineShopController {
	
	@Autowired
	OnlineShopService onlineShopService;
	
	@RequestMapping(value = "/searchvaritys", method = RequestMethod.POST)
	public ResponseEntity<BaseDTO> searchVarities(@RequestBody ProductVariryMastersearchDTO productVariryMastersearchDTO) {
		
		log.info("<< == Start Of Retail Society Plan Search == >>");
		
		BaseDTO baseDTO = new BaseDTO();
		baseDTO = onlineShopService.searchProductVarities(productVariryMastersearchDTO);

		if (baseDTO != null) {
			return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<BaseDTO>(baseDTO, HttpStatus.NO_CONTENT);
	}

}
	
}
