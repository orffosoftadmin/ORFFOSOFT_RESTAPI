package co.orffosoft.rest.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import co.orffosoft.core.util.RestException;
import co.orffosoft.entity.EntityMaster;
import co.orffosoft.entity.SequenceConfig;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import co.orffosoft.service.LoginService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AutoGenerateConstant implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 384368116695888735L;

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	EntityMasterRepository entityMasterRepository;

	@Transactional
	public List<String> generateBillNumberAndSasdsdve(SequenceName seqName) throws RestException {
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
