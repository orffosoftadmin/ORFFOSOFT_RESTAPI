package co.orffosoft.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.GenerateProductQRCodeDetailsDTO;
import co.orffosoft.rest.util.QRCodeUtil;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class GenerateProductQRCodeService {

	@Autowired
	QRCodeUtil qrCodeUtil;
	
	
	@Transactional
	public BaseDTO generateItemQRCode(GenerateProductQRCodeDetailsDTO generateProductQRCodeDetailsDTO) {
		BaseDTO baseDTO = new BaseDTO();
//		List<ItemQRCodeDetailsDTO> itemQRCodeList = new ArrayList<>();
		Gson gson = new Gson();
		Double discountPercent = 0.0;
		try {

//			itemQRCodeList = getItemDetailsForQRCode(GenerateProductQRCodeDetailsDTO);
//			AppConfig appConfig = appConfigRepository.findByKey("PRODUCT_NET_PRICE_DISCOUNT_PERCENT");
//			if (appConfig != null) {
//				discountPercent = Double.parseDouble(appConfig.getAppValue());
//			}
//			for (ItemQRCodeDetailsDTO itemQRCodeDetailsDTO : itemQRCodeList) {
//				itemQRCodeDetailsDTO
//						.setQRCode(generateQRCodeNumber(GenerateProductQRCodeDetailsDTO.getWareHouseCode()));
//				String jsonText = gson.toJson(itemQRCodeDetailsDTO);
//
//				log.info("ProductQRCodeService getProductQRCodeItemDetailsList jsonText for qrCode", jsonText);

				//saveQrCode(itemQRCodeDetailsDTO, GenerateProductQRCodeDetailsDTO);
//			}
			//qrCodeUtil.createQrCodePdf(generateProductQRCodeDetailsDTO, itemQRCodeList, discountPercent);
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());

			log.info("ProductQRCodeService generateItemQRCode method completed");

		} catch (RestException exception) {

			baseDTO.setStatusCode(exception.getStatusCode());

			log.error("ProductQRCodeService generateItemQRCode method RestException ", exception);

		} catch (Exception exception) {

			log.error("Exception occurred in ProductQRCodeService generateItemQRCode method -:", exception);

			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}

		return baseDTO;

	}

}
