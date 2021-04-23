package co.orffosoft.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.CollectAmountDTO;
import co.orffosoft.entity.UserMaster;
import co.orffosoft.repository.SalesReturnRepository;
import co.orffosoft.repository.UserMasterRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CollectAmountService {

	@Autowired
	SalesReturnRepository salesReturnRepository;
	
	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	LoginService loginService;

	public BaseDTO genereatreport(CollectAmountDTO collectamountdto) {
		BaseDTO baseDTO = new BaseDTO();
		List<CollectAmountDTO> listcollection = new ArrayList();
		CollectAmountDTO dto;
		try {
			Long id;
			if(collectamountdto.getUserid()==null) {
				id=loginService.getCurrentUser().getId();
			}else {
				id=collectamountdto.getUserid();
			}
			List<Object[]> obj = salesReturnRepository.collectamount(id,
					loginService.getCurrentUser().getEntityId(), collectamountdto.getFromdate(),
					collectamountdto.getTodate());

			if (obj != null) {

				Iterator<?> it = obj.iterator();
				while (it.hasNext()) {
					dto = new CollectAmountDTO();
					Object ob[] = (Object[]) it.next();

					if (ob[0] != null) {
						dto.setType(ob[0].toString());
					}
					if (ob[1] != null) {
						dto.setTransactiondate((Date)ob[1]);
					}
					if (ob[2] != null) {
						dto.setUsername(ob[2].toString());
					}
					if (ob[3] != null) {
						dto.setBilledamount(Double.parseDouble(ob[3].toString()));
					}
					if (ob[4] != null) {
						dto.setCollectamount(Double.parseDouble(ob[4].toString()));
					}
					listcollection.add(dto);
				}
				baseDTO.setResponseContents(listcollection);
				baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			}

		} catch (Exception e) {
			log.info("Exception in service", e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getCode());
		}
		return baseDTO;
	}

	public BaseDTO getusers() {
		BaseDTO response =new BaseDTO();
		CollectAmountDTO dto;
		List<Object> listusermaster =new ArrayList();
		List<CollectAmountDTO> listcollection = new ArrayList();
		try {
			
			listusermaster=userMasterRepository.getuserforcollectionAmt(loginService.getCurrentUser().getId());
			
			if (listusermaster != null) {

				Iterator<?> it = listusermaster.iterator();
				while (it.hasNext()) {
					Object ob[] = (Object[]) it.next();
					dto = new CollectAmountDTO();
					if (ob[0] != null) {
						dto.setUserid(Long.parseLong(ob[0].toString()));
					}
					if (ob[1] != null) {
						dto.setUsername(ob[1].toString());
					}
					if (ob[2] != null) {
						dto.setStorename(ob[2].toString());
					}
					listcollection.add(dto);
				}
				}
			
			response.setResponseContents(listcollection);
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			
		}catch (Exception e) {
			log.info("Exception in service", e);
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getCode());
		}
		return response;
	}

}
