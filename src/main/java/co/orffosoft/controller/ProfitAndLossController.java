package co.orffosoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.ProfitAndLossReportDTO;
import co.orffosoft.service.ProfitAndLossReportService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/profitAndLossController")
public class ProfitAndLossController {
	
	@Autowired
	ProfitAndLossReportService profitAndLossReportService;

	@RequestMapping(value = "/genereat", method = RequestMethod.POST)
	public BaseDTO genereat(@RequestBody ProfitAndLossReportDTO profitAndLossReportDTO) {
		log.info("profitAndLossController  genereat---------#Start");
		BaseDTO baseDto = profitAndLossReportService.generate(profitAndLossReportDTO);
		
		return baseDto;

	}
}
