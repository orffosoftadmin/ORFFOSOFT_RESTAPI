package co.orffosoft.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.AppUtil;
import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.core.util.StockTransactionTypeIFC;
import co.orffosoft.entity.EntityMaster;
import co.orffosoft.entity.SequenceConfig;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SequenceConfigService {

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;
	
	@Autowired
	LoginService loginService;

	@Autowired
	EntityMasterRepository entityMasterRepository;
	
	public String generateNextSequenceByName(SequenceName sequenceName) {
		return this.generateNextSequence(sequenceName.toString(), null);
	}
	
	public String generateNextSequenceByName(SequenceName sequenceName, String additionalValue) {
		return this.generateNextSequence(sequenceName.toString(), additionalValue);
	}

	private String generateNextSequence(String sequenceName, String additionalValue) {

		log.info("Generating Sequence for [" + sequenceName + "]");

		String generatedSequence = null;

		try {

			SequenceConfig sequenceConfig = sequenceConfigRepository.findBySequenceName(SequenceName.valueOf(sequenceName),
					loginService.getCurrentUser().getEntityId());

			if (sequenceConfig == null) {
				throw new RestException(ErrorDescription.SEQUENCE_CONFIG_NOT_EXIST);
			}

			generatedSequence = prepareSequence(sequenceConfig, additionalValue);

			sequenceConfig.setCurrentValue(sequenceConfig.getCurrentValue() + 1);

			sequenceConfigRepository.save(sequenceConfig);

			log.info("Generated Sequence : [" + generatedSequence + "]");

		} catch (Exception exception) {
			log.error("Exception ", exception);
		}

		return generatedSequence;
	}
	
	private void append(StringBuffer generatedSequence, String separator, String value) {
		if (generatedSequence.length() == 0) {
			generatedSequence.append(value.trim());
		} else {
			generatedSequence.append(separator);
			generatedSequence.append(value.trim());
		}
	}

	private String prepareSequence(SequenceConfig sequenceConfig, String additionalValue) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd");

		String date = simpleDateFormat.format(new Date());

		log.info("Current Date [" + date + "]");

		StringBuffer generatedSequence = new StringBuffer();

		String separator = "";

		if (sequenceConfig.getSeparator() != null && !sequenceConfig.getSeparator().trim().isEmpty()) {
			separator = sequenceConfig.getSeparator();
		}

		if (sequenceConfig.getPrefix() != null && !sequenceConfig.getPrefix().trim().isEmpty()) {
				append(generatedSequence, separator, sequenceConfig.getPrefix());
		}
	
		if(additionalValue != null && !additionalValue.trim().isEmpty()) {
			append(generatedSequence, separator, additionalValue);
		}

		if (sequenceConfig.getIsMonthRequired() != null && sequenceConfig.getIsMonthRequired().equals(true)) {
			String currentMonth = date.substring(date.indexOf("-") + 1, date.lastIndexOf("-"));
			append(generatedSequence, separator, currentMonth.toUpperCase());
		}

		if (sequenceConfig.getIsYearRequired() != null && sequenceConfig.getIsYearRequired().equals(true)) {
			String currentYear = date.substring(0, date.indexOf("-"));
			append(generatedSequence, separator, currentYear);
		}

		String codeSeparator = "";

		if (sequenceConfig.getCodeSeparator() != null && !sequenceConfig.getCodeSeparator().trim().isEmpty()) {
			codeSeparator = sequenceConfig.getCodeSeparator();
		}

		append(generatedSequence, codeSeparator, sequenceConfig.getCurrentValue().toString());

		if (sequenceConfig.getSuffix() != null && !sequenceConfig.getSuffix().trim().isEmpty()) {
			append(generatedSequence, separator, sequenceConfig.getSuffix());
		}

		return generatedSequence.toString();
	}
	
	
	/**
	 * @param ent 
	 * 
	 */
	@Transactional
	public void configureSequenceConfig(Long entityId) {
		SequenceConfig sequenceConfig = new SequenceConfig();
		List<SequenceConfig> sequenceConfigList = new ArrayList<SequenceConfig>();
		
		List<SequenceName>  sequenceNameList = AppUtil.sequenceConfigList();
		for (SequenceName seqName : sequenceNameList) {
			sequenceConfig = new SequenceConfig();
			sequenceConfig.setSequenceName(seqName);
			sequenceConfig.setDescription(seqName.toString());
			sequenceConfig.setCurrentValue(1L);
			if (seqName.toString().equalsIgnoreCase(SequenceName.BILL_PAYEMENT.toString())) {
				sequenceConfig.setPrefix(StockTransactionTypeIFC.SALE);
			} else if (seqName.toString().equalsIgnoreCase(SequenceName.GRN.toString())) {
				sequenceConfig.setPrefix(StockTransactionTypeIFC.GRN);
			} else if (seqName.toString().equalsIgnoreCase(SequenceName.SALES_RETURN.toString())) {
				sequenceConfig.setPrefix(StockTransactionTypeIFC.SALE);
			} else if (seqName.toString().equalsIgnoreCase(SequenceName.CreditNote.toString())) {
				sequenceConfig.setPrefix("CN");
			} else if (seqName.toString().equalsIgnoreCase(SequenceName.INITIAL_STOCK_UPLOAD.toString())) {
				sequenceConfig.setPrefix(StockTransactionTypeIFC.STKUPD_GRN);
			} else if (seqName.toString().equalsIgnoreCase(SequenceName.QR_CODE.toString())) {
				sequenceConfig.setPrefix(StockTransactionTypeIFC.QR_CODE);
			}
			//sequenceConfig.setCreatedBy(loginService.getCurrentUser());
			sequenceConfig.setSuffix(null);
			sequenceConfig.setIsMonthRequired(true);
			sequenceConfig.setIsYearRequired(true);
			sequenceConfig.setCreatedDate(new Date());
			sequenceConfig.setEntityId(entityId);
			sequenceConfigList.add(sequenceConfig);
		}
		sequenceConfigRepository.save(sequenceConfigList);
	}

	@Transactional
	public List<String> generateBillNumberAndSave(SequenceName seqName) throws RestException {
		log.info(" Start Of generateBillNumber cal ");
		List<String> billNoAndStoreName = new ArrayList<>();
		SequenceConfig sequenceConfig = sequenceConfigRepository.findBySequenceName(seqName,
				loginService.getCurrentUser().getEntityId());
		
		if (sequenceConfig == null) {
			throw new RestException(" Sequence Name Not Found In SequenceConfig == "+seqName);
		}
		EntityMaster storeDetails = entityMasterRepository.findOne(loginService.getCurrentUser().getEntityId());
		if (storeDetails != null && sequenceConfig != null) {
			billNoAndStoreName.add(storeDetails.getCode() + "/" + sequenceConfig.getPrefix() + "/" + 
									sequenceConfig.getCurrentValue());
			billNoAndStoreName.add(storeDetails.getName());
		}
		sequenceConfig.setCurrentValue(sequenceConfig.getCurrentValue() + 1);
		sequenceConfigRepository.save(sequenceConfig);
		log.info(" End Of generateBillNumber cal and Generated Bill Number Is = "+billNoAndStoreName);
		return billNoAndStoreName;
	}
	
}
