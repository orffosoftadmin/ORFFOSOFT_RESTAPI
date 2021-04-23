package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.CustomerMasterDTO;
import co.orffosoft.entity.CustomerMaster;
import co.orffosoft.entity.SequenceConfig;
import co.orffosoft.entity.SupplierMaster;
import co.orffosoft.repository.CustomerMasterRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import co.orffosoft.repository.UserMasterRepository;
import co.orffosoft.rest.util.ResponseWrapper;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class CustomerMasterService {

	@Autowired
	CustomerMasterRepository customerMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;

	@Autowired
	LoginService loginService;

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ResponseWrapper responseWrapper;
	
	public BaseDTO checkmobileno(String mobno) {
		log.error("<<=====checkmobileno======>>");
		BaseDTO response=new BaseDTO();
		try {
			
			if(!mobno.isEmpty()) {
				CustomerMaster cm= customerMasterRepository.findCustomerByMobilenumber(mobno,loginService.getCurrentUser().getEntityId());
				if(cm==null) {
					response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
					
				}else {
					response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
				}
				
			}
			
		}catch (Exception e) {
			log.error("<<=======    ERROR DURING getall ==###" + e);
		}
		return response;
		
	}
	
	public BaseDTO addOrUpdate(CustomerMaster customerMasterReq) {
		BaseDTO basedto=new BaseDTO();
		CustomerMaster customerMaster = new CustomerMaster();
		try {
			
			
			
			if(customerMasterReq.getId()==null) {
//				customerMaster = customerMasterRepository.findCustomerByMobilenumber(customerMasterReq.getPrimaryContactNumber(), loginService.getCurrentUser().getEntityId());
//				if(customerMaster == null && customerMasterReq.getId() != null) {
//					basedto.setMessage("Alredy exist Customer ");
//					basedto.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
//					throw new RestException("Alredy exist Customer ");
//				}
				customerMaster.setName(customerMasterReq.getName().toUpperCase());
				customerMaster.setRegistrationDate(new Date());
				customerMaster.setPrimaryContactNumber(customerMasterReq.getPrimaryContactNumber());
				customerMaster.setActiveStatus(true);
				customerMaster.setCreatedBy(loginService.getCurrentUser());
				customerMaster.setCreatedDate(new Date());
				customerMaster.setVersion(0l);
				customerMaster.setEntityId(loginService.getCurrentUser().getEntityId());
				customerMaster.setCustomerAddress(customerMasterReq.getCustomerAddress());
				
				customerMasterRepository.save(customerMaster);
				basedto.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
				
			}else {
				customerMaster = new CustomerMaster();
				customerMaster=customerMasterRepository.findOne(customerMasterReq.getId());
				if(customerMaster!=null){
					customerMaster.setName(customerMasterReq.getName().toUpperCase());
					customerMaster.setRegistrationDate(customerMaster.getRegistrationDate());
					customerMaster.setPrimaryContactNumber(customerMasterReq.getPrimaryContactNumber());
					customerMaster.setActiveStatus(true);
					customerMaster.setCreatedBy(customerMaster.getCreatedBy());
					customerMaster.setCreatedDate(customerMaster.getCreatedDate());
					customerMaster.setVersion(0l);
					customerMaster.setEntityId(customerMaster.getEntityId());
					customerMaster.setCustomerAddress(customerMasterReq.getCustomerAddress());
					customerMaster.setModifiedBy(loginService.getCurrentUser());
					customerMaster.setModifiedDate(new Date());
					
					
					customerMasterRepository.save(customerMaster);
					basedto.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
					
					
				}
				
			}
			
		} catch (Exception e) {
			log.error("<<=======    ERROR DURING getall ==###" + e);
			basedto.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return basedto;
		
	}
	
//	public BaseDTO update(CustomerMaster customerMasterReq) {
//		BaseDTO basedto=new BaseDTO();
//		CustomerMaster customerMaster = new CustomerMaster();
//		
//		try {
//			if(customerMaster!=null && customerMaster.getId()!=null) {
//				
//				customerMaster=customerMasterRepository.findCustomerByMobilenumber_and_name(customerMaster.getPrimaryContactNumber(),
//						customerMaster.getName(), loginService.getCurrentUser().getEntityId());
//				if(customerMaster!=null) {
//					
//					if(customerMaster.getId().equals(customerMaster.getId())) {
//						customerMaster.setName(customerMaster.getName());
//						customerMaster.setPrimaryContactNumber(customerMaster.getPrimaryContactNumber());
//						customerMaster.setCustomerAddress(customerMaster.getCustomerAddress());
//						customerMaster.setModifiedBy(loginService.getCurrentUser());
//						customerMaster.setModifiedDate(new Date());
//						
//						customerMasterRepository.saveAndFlush(customerMaster);
//					}else {
//						basedto.setMessage("Alredy exist Customer ");
//						throw new RestException("Alredy exist Customer ");
//					}
//					
//				}else {
//					customerMaster=new CustomerMaster();
//					customerMaster.setName(customerMaster.getName());
//					customerMaster.setPrimaryContactNumber(customerMaster.getPrimaryContactNumber());
//					customerMaster.setCustomerAddress(customerMaster.getCustomerAddress());
//					customerMaster.setModifiedBy(loginService.getCurrentUser());
//					customerMaster.setModifiedDate(new Date());
//					
//					customerMasterRepository.saveAndFlush(customerMaster);
//					
//				}
//				basedto.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
//			}
//			
//		}catch (Exception e) {
//			log.error("<<=======    ERROR DURING getall ==###" + e);
//			basedto.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
//		}
//		return basedto;
//		
//		
//	}
	
	public BaseDTO getall(CustomerMaster customerMaster) {
		BaseDTO baseDTO=new BaseDTO();
		Session session = entityManager.unwrap(Session.class);
		Criteria criteria = session.createCriteria(CustomerMaster.class, "customermaster");
		try {
			if (customerMaster.getFilters() != null) {
				
				if (customerMaster.getFilters().get("name") != null) {
					String Name = (String) customerMaster.getFilters().get("name");

					log.info(" codeOrName " + Name);

					criteria.add(Restrictions.or(
							Restrictions.like("customermaster.name", "%" + Name + "%").ignoreCase()));
				}
				
				if (customerMaster.getFilters().get("primaryContactNumber") != null) {
					String Number = (String) customerMaster.getFilters().get("primaryContactNumber");

					log.info(" codeOrName " + Number);

					criteria.add(Restrictions.or(
							Restrictions.like("customermaster.primaryContactNumber", "%" + Number + "%").ignoreCase()));
				}
				
			}
			criteria.add(Restrictions.eq("customermaster.entityId",loginService.getCurrentUser().getEntityId()));
			
			criteria.setProjection(Projections.rowCount());
			Integer totalResult = ((Long) criteria.uniqueResult()).intValue();
			criteria.setProjection(null);
			
			// For Pagination
			if (customerMaster.getPaginationDTO() != null) {
				Integer pageNo = customerMaster.getPaginationDTO().getPageNo();
				Integer pageSize = customerMaster.getPaginationDTO().getPageSize();

				if (pageNo != null && pageSize != null) {
					criteria.setFirstResult(pageNo * pageSize);
					criteria.setMaxResults(pageSize);
					log.info("PageNo : [" + pageNo + "] pageSize[" + pageSize + "]");
				}

				String sortField = customerMaster.getPaginationDTO().getSortField();
				String sortOrder = customerMaster.getPaginationDTO().getSortOrder();
				if (sortField != null && sortOrder != null) {
					log.info("sortField : [" + sortField + "] sortOrder[" + sortOrder + "]");

					if (sortField.equals("name")) {

						sortField = "customermaster.name";

					} 

					if (sortOrder.equals("DESCENDING")) {
						criteria.addOrder(Order.desc(sortField));
					} else {
						criteria.addOrder(Order.asc(sortField));
					}
				}
			}
			
			log.info("Criteria Query  : " + criteria);

			ProjectionList projectionList = Projections.projectionList();
			
			projectionList.add(Projections.property("customermaster.name"));
			projectionList.add(Projections.property("customermaster.primaryContactNumber"));
			projectionList.add(Projections.property("customermaster.id"));
			projectionList.add(Projections.property("customermaster.customerAddress"));
			
			criteria.setProjection(projectionList);
			List<CustomerMaster> customerMasterList = new ArrayList<>();
			List<?> resultList = criteria.list();
			
			if (resultList != null) {
				Iterator<?> it = resultList.iterator();
				while (it.hasNext()) {
					Object ob[] = (Object[]) it.next();
					CustomerMaster responseEntity = new CustomerMaster();
					if(ob[0]!=null) {
					responseEntity.setName(ob[0].toString());
					}
					if(ob[1]!=null) {
					responseEntity.setPrimaryContactNumber(ob[1].toString());
					}
					if(ob[2]!=null) {
						responseEntity.setId(Long.parseLong(ob[2].toString()) );
					}
					
					if(ob[3]!=null) {
						responseEntity.setCustomerAddress(ob[3].toString());
					}
					

					customerMasterList.add(responseEntity);
				}
				baseDTO.setTotalRecords(totalResult);
				baseDTO.setResponseContents(customerMasterList);
				baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			}
			
		} catch (Exception e) {
			log.error("<<=======    ERROR DURING getall ==###" + e);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		
		return baseDTO;
		
	}
	



	private void validateFieldsNotNull(CustomerMasterDTO customerMaster) {
		Validate.notNull(customerMaster);
		Validate.notEmpty(customerMaster.getName());
		Validate.notNull(customerMaster.getActiveStatus());
		Validate.notNull(customerMaster.getPrimaryContactNumber());
	}

}
