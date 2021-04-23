package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.ExpensiveDTO;
import co.orffosoft.entity.EntityMaster;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.ExpensiveRepository;
import co.orffosoft.repository.UserMasterRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ExpensiveReportService {

	@Autowired
	EntityMasterRepository entityMasterRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	ExpensiveRepository ExpensiveRepository;

	public BaseDTO generateReport(ExpensiveDTO expensivedto) {
		BaseDTO baseDTO = new BaseDTO();
		Iterator<?> it = null;
		List<ExpensiveDTO> listexpensive = new ArrayList();
		try {
			Long id;
			String itemname=null;
			if(!expensivedto.getItemname().isEmpty()) {
				itemname="%"+expensivedto.getItemname().trim()+"%";
			}else {
				itemname="%%";
			}
			if (expensivedto.getEntitymaster() != null && expensivedto.getEntitymaster().getId() != null) {
				id = expensivedto.getEntitymaster().getId();
			} else {
				id = loginService.getCurrentUser().getEntityId();
			}

			List<Object> data = ExpensiveRepository.getdata(expensivedto.getFromdate(), expensivedto.getTodate(), id,itemname);

			if (!data.isEmpty()) {
				it = data.iterator();
			}
			if (it != null) {
				while (it.hasNext()) {
					Object ob[] = (Object[]) it.next();
					ExpensiveDTO expensive = new ExpensiveDTO();
					
				
					if (ob[0] != null) {
						expensive.setItemname(ob[0].toString());
					}
					if (ob[1] != null) {
						expensive.setAmount(Double.parseDouble(ob[1].toString()));
					}
					if (ob[2] != null) {
						expensive.setGstamount(Double.parseDouble(ob[2].toString()));
					}
					if (ob[3] != null) {
						expensive.setGstno(ob[3].toString());
					}
					if (ob[4] != null) {
						expensive.setVendorname(ob[4].toString());
					}
					if (ob[5] != null) {
						expensive.setDate((Date)ob[5]);
					}
					if (ob[6] != null) {
						
					}
					if (ob[7] != null) {

					}
					if (ob[8] != null) {
						expensive.setModifydate((Date)ob[8] );
					}
					if (ob[10] != null) {
						
					}
					if (ob[11] != null) {
						
					}
					listexpensive.add(expensive);
				}
				baseDTO.setResponseContents(listexpensive);
				baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			}
		} catch (Exception e) {
			log.info("Exception in service" + e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
	}

	public BaseDTO getShopNames(Long id, Long pid) {
		BaseDTO respons = new BaseDTO();
		List<EntityMaster> entityList = entityMasterRepository.findParentEntityMaster(id);

		respons.setParentIdOfUserMaster(userMasterRepository.findUserParentId(pid));
		respons.setResponseContents(entityList);
		respons.setStatusCode(0);

		return respons;

	}

	public BaseDTO autoCompleteItem(String itemName) {
		BaseDTO baseDTO=new BaseDTO();
		try {
			itemName = "%" + itemName.trim() + "%";
			Long entityId = loginService.getCurrentUser().getEntityId();
			
			List<String> itemnames=ExpensiveRepository.autoCompleteItem(itemName, entityId);
			
			baseDTO.setResponseContents(itemnames);
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			
		} catch (Exception e) {
			log.info("Exception in service" + e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
	}

}
