package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.ProfitAndLossReportDTO;
import co.orffosoft.dto.QrCodeItemDTO;
import co.orffosoft.service.QRCodeGeneratorService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/qrCodeGeneratorController")
public class QRCodeGeneratorController {
	
	@Autowired
	QRCodeGeneratorService qrCodeGeneratorService;
	
	@RequestMapping(value = "/getgrns", method = RequestMethod.GET)
	public BaseDTO getGRNs() {
		log.info("QRCodeGeneratorController  genereat---------#Start");
		BaseDTO baseDto = qrCodeGeneratorService.getGRNs();
		
		return baseDto;

	}
	

	@RequestMapping(value = "/getgrndetail", method = RequestMethod.POST)
	public BaseDTO getGRNdetail(@RequestBody QrCodeItemDTO qrCodeItemDTO) {
		log.info("QRCodeGeneratorController  genereat---------#Start");
		BaseDTO baseDto = qrCodeGeneratorService.getGRNdetail(qrCodeItemDTO);
		
		return baseDto;

	}

}
