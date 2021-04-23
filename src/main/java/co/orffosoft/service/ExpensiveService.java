package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.ExpensiveDTO;
import co.orffosoft.entity.Expensive;
import co.orffosoft.entity.GoodsReceiptNote_H;
import co.orffosoft.repository.ExpensiveRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ExpensiveService {

	@Autowired
	ExpensiveRepository expensiveRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	EntityManager entityManager;

	public BaseDTO save(ExpensiveDTO expensivedto) {
		BaseDTO baseDTO = new BaseDTO();
		try {
			Expensive expensive = new Expensive();
			if (expensivedto != null) {
				expensive.setItemname(expensivedto.getItemname());
				expensive.setAmount(expensivedto.getAmount());
				expensive.setGstamount(expensivedto.getGstamount());
				expensive.setGstnumber(expensivedto.getGstno());
				expensive.setVendorname(expensivedto.getVendorname());
				expensive.setDate(expensivedto.getDate());
				expensive.setCreatedby(loginService.getCurrentUser().getId());
				expensive.setCreateddate(new Date());
				expensive.setEntityid(loginService.getCurrentUser().getEntityId());

				expensiveRepository.saveAndFlush(expensive);
				baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());

			}

		} catch (Exception e) {
			log.info("Exception in ExpensiveService at save()" + e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}

		return baseDTO;

	}

	public BaseDTO searchData(ExpensiveDTO expensivedto) {
		BaseDTO baseDTO = new BaseDTO();
		List<?> resultList = null;
		List<ExpensiveDTO> listexpensive = new ArrayList();
		try {

			Session session = entityManager.unwrap(Session.class);
			Criteria criteria = session.createCriteria(Expensive.class, "Expensive");
			// criteria.createAlias("UserMaster", "usermaster",
			// CriteriaSpecification.LEFT_JOIN);
			
			if (expensivedto.getPaginationDTO().getFilters() != null) {

				if (expensivedto.getPaginationDTO().getFilters().get("itemname") != null) {
					String name =  expensivedto.getPaginationDTO().getFilters().get("itemname").toString();
					log.info(" Nmae Filter : " + name);
					criteria.add(Restrictions.like("Expensive.itemname", "%" + name + '%').ignoreCase());
				}
				
//				if (expensivedto.getPaginationDTO().getFilters().get("amount") != null) {
//					Double amount =Double.valueOf(expensivedto.getPaginationDTO().getFilters().get("amount").toString()) ;
//					//String amount =expensivedto.getPaginationDTO().getFilters().get("amount").toString() ;
//					
//					log.info(" Nmae Filter : " + amount);
//					criteria.add(Restrictions.like("Expensive.amount",  + amount ).ignoreCase());
//				}
				
				if (expensivedto.getPaginationDTO().getFilters().get("gstno") != null) {
					String gstno =  expensivedto.getPaginationDTO().getFilters().get("gstno").toString();
					log.info(" Nmae Filter : " + gstno);
					criteria.add(Restrictions.like("Expensive.gstnumber", "%" + gstno + '%').ignoreCase());
				}
				
				if (expensivedto.getPaginationDTO().getFilters().get("vendorname") != null) {
					String vendorname =  expensivedto.getPaginationDTO().getFilters().get("vendorname").toString();
					log.info(" Nmae Filter : " + vendorname);
					criteria.add(Restrictions.like("Expensive.vendorname", "%" + vendorname + '%').ignoreCase());
				}
				
				if (expensivedto.getPaginationDTO().getFilters().get("date") != null) {
					Date date = (Date) expensivedto.getPaginationDTO().getFilters().get("date");
					log.info(" Nmae Filter : " + date);
					criteria.add(Restrictions.like("Expensive.vendorname", date ).ignoreCase());
				}
			
			}
			

			ProjectionList projectionList = Projections.projectionList();

			projectionList.add(Projections.property("Expensive.id"));
			projectionList.add(Projections.property("Expensive.itemname"));
			projectionList.add(Projections.property("Expensive.amount"));
			projectionList.add(Projections.property("Expensive.gstamount"));
			projectionList.add(Projections.property("Expensive.gstnumber"));
			projectionList.add(Projections.property("Expensive.vendorname"));
			
			projectionList.add(Projections.property("Expensive.date"));
			projectionList.add(Projections.property("Expensive.createddate"));
			projectionList.add(Projections.property("Expensive.createdby"));
			projectionList.add(Projections.property("Expensive.modifydate"));
			projectionList.add(Projections.property("Expensive.modifyby"));
			projectionList.add(Projections.property("Expensive.entityid"));

			criteria.setProjection(projectionList);
			
			
			resultList = criteria.list();
			
			

			Iterator<?> it = resultList.iterator();

			while (it.hasNext()) {
				Object ob[] = (Object[]) it.next();
				ExpensiveDTO expensive = new ExpensiveDTO();
				if (ob[0] != null) {
					expensive.setId(Long.parseLong(ob[0].toString()));
				}
				if (ob[1] != null) {
					expensive.setItemname(ob[1].toString());
				}
				if (ob[2] != null) {
					expensive.setAmount(Double.parseDouble(ob[2].toString()));
				}
				if (ob[3] != null) {
					expensive.setGstamount(Double.parseDouble(ob[3].toString()));
				}
				if (ob[4] != null) {
					expensive.setGstno(ob[4].toString());
				}
				if (ob[5] != null) {
					expensive.setVendorname(ob[5].toString());
				}
				if (ob[6] != null) {
					expensive.setDate((Date)ob[6]);
				}
				if (ob[7] != null) {
					
				}
				if (ob[8] != null) {

				}
				if (ob[9] != null) {
					expensive.setModifydate((Date)ob[9] );
				}
				if (ob[10] != null) {
					
				}
				if (ob[11] != null) {
					
				}
				listexpensive.add(expensive);
				
			}
			baseDTO.setResponseContents(listexpensive);
			baseDTO.setTotalRecords(resultList.size());
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());

		} catch (Exception e) {
			log.info("Exception in service at searchData " + e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
		
	}

	public BaseDTO update(ExpensiveDTO expensivedto) {
		BaseDTO baseDTO = new BaseDTO();
		Expensive expensive=new Expensive();
		try {
			
			if(expensivedto!=null && expensivedto.getId()!=null) {
				
				expensive=expensiveRepository.findOne(expensivedto.getId());
				if(expensive!=null) {
					expensive.setItemname(expensivedto.getItemname());
					expensive.setAmount(expensivedto.getAmount());
					expensive.setGstamount(expensivedto.getGstamount());
					expensive.setGstnumber(expensivedto.getGstno());
					expensive.setModifyby(loginService.getCurrentUser().getId());
					expensive.setModifydate(new Date());
					expensive.setVendorname(expensivedto.getVendorname());
					expensive.setDate(expensivedto.getDate());
					
					expensiveRepository.save(expensive);
					baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
					
				}
				
			}
			
		}catch (Exception e) {
			log.info("Exception in service at searchData " + e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
	}

}
