package co.orffosoft.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.itemWiseClosingStockDTO;
import co.orffosoft.entity.StockTransaction;
import co.orffosoft.repository.StockTransactionRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Configuration
@EnableTransactionManagement
public class ItemwiseClosingsStockService {

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	LoginService loginService;

	public BaseDTO generetReport(itemWiseClosingStockDTO dto) {

		BaseDTO baseDTO = new BaseDTO();
		List<StockTransaction> stockTransaction = null;
		List<itemWiseClosingStockDTO> itemWiseClosingStockDTOList = null;
		itemWiseClosingStockDTO csDTO = null;
		List<Object[]> stockTransactionObj;
		log.info("ItemwiseClosingsStockService  generetReport---------#Start");
		try {
			if (dto != null && dto.getToDate() != null) {

				if (dto.getProductVarietyMaster()!=null && dto.getProductVarietyMaster().getId() != null) {
					stockTransactionObj = stockTransactionRepository.getClosingStockAsOnWithName(dto.getToDate(),
							loginService.getCurrentUser().getEntityId(), dto.getProductVarietyMaster().getId());
				} else {
					stockTransactionObj = stockTransactionRepository.getClosingStockAsOn(dto.getToDate(),
							loginService.getCurrentUser().getEntityId());
				}

				if (stockTransactionObj != null) {
					itemWiseClosingStockDTOList = new ArrayList<>();
					for (Object[] st : stockTransactionObj) {
						csDTO = new itemWiseClosingStockDTO();
						csDTO.setItemName(st[0] == null ? "" : st[0].toString().trim());
						csDTO.setItemPrice(Double.parseDouble(st[1] == null ? "0" : st[1].toString()));
						csDTO.setStockTransactionDate(dto.getToDate());
						csDTO.setTotalClosingStockQnty(Double.parseDouble(st[2] == null ? "0.0" : st[2].toString()));
						csDTO.setItemPurchesPrice(Double.parseDouble(st[3] == null ? "0.0" : st[3].toString()));
						itemWiseClosingStockDTOList.add(csDTO);
					}
				}

			}

			baseDTO.setResponseContents(itemWiseClosingStockDTOList);
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
		} catch (Exception e) {
			log.error("Exception Occured In ItemwiseClosingsStockService generetReport", e);
		}

		return baseDTO;

	}

}
