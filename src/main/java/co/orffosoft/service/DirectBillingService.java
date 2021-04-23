package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.DirectBillingDTO;
import co.orffosoft.entity.CustomerMaster;
import co.orffosoft.entity.DirectBilling_D;
import co.orffosoft.entity.DirectBilling_H;
import co.orffosoft.entity.ProductVarietyMaster;
import co.orffosoft.entity.UserMaster;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.repository.CustomerMasterRepository;
import co.orffosoft.repository.DirectBilling_D_Repository;
import co.orffosoft.repository.DirectBilling_H_Repository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.UserMasterRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DirectBillingService {
	
	@Autowired
	LoginService loginService;

	@PersistenceContext
	EntityManager manager;

	@Autowired
	DirectBilling_H_Repository directBilling_H_Repository;
	
	@Autowired
	DirectBilling_D_Repository directBilling_D_Repository;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	SequenceConfigService sequenceConfigService;
	
	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;
	
	@Autowired
	CustomerMasterRepository customerMasterRepository;
	
	UserMasterRepository userMasterRepository;
	
	@Transactional
	public BaseDTO save(List<DirectBillingDTO> directBillingDTOList) {
		log.info("");
		BaseDTO baseDTO = new BaseDTO();
		DirectBilling_H directBilling_H =  new DirectBilling_H();
		DirectBilling_D directBilling_D;
		List<DirectBilling_D> directBilling_D_List = new ArrayList<>();
		DirectBillingDTO directBillingDTO = new DirectBillingDTO();
		CustomerMaster customerMaster= new CustomerMaster();
		CustomerMaster excustomerMaster= new CustomerMaster();
		UserMaster userMaster=new UserMaster();
		try {
			
			directBilling_H.setBillDate(new Date());
			
			List<String> billNumberAndStoreName  = sequenceConfigService.generateBillNumberAndSave(SequenceName.BILL_PAYEMENT);
			if (billNumberAndStoreName != null && billNumberAndStoreName.size() > 0) {
				directBilling_H.setBillNumber(billNumberAndStoreName.get(0));
			}
			
			directBilling_H.setBillValue(directBillingDTOList.stream().mapToDouble(x->x.getTotalNetPrice()).sum());
			directBilling_H.setDiscountValue(directBillingDTOList.stream().mapToDouble(x->x.getDiscountAmt()).sum());
			directBilling_H.setCreatedBy(loginService.getCurrentUser());
			directBilling_H.setEntityid(loginService.getCurrentUser().getEntityId());
			directBilling_H.setCustomername(directBillingDTOList.get(0).getCustomerName());
			directBilling_H.setCreatedDate(new Date());
			directBilling_H.setMobileNumber(directBillingDTOList.get(0).getMobileNumber());
			directBillingDTO.setBillNo(directBilling_H.getBillNumber());
			directBillingDTO.setStoreName(billNumberAndStoreName.get(1).toString());
			directBilling_H_Repository.save(directBilling_H);
			
			
			for (DirectBillingDTO dto : directBillingDTOList) {
				directBilling_D  = new DirectBilling_D();
				directBilling_D.setDirect_billing_h_fk(directBilling_H.getId());
				directBilling_D.setProduct_variety_id(productVarietyMasterRepository.findOne(dto.getProductVarietyId()));  
				directBilling_D.setUnit_rate(dto.getSellingPrice());
				directBilling_D.setItem_qty(dto.getBilledQnty());
				directBilling_D.setDiscount_value(dto.getDiscountAmt());
				directBilling_D.setTotal_netprice(dto.getTotalNetPrice());
				directBilling_D.setCreated_by(directBilling_H.getCreatedBy().getId());
				directBilling_D.setCreated_date(new Date());
				directBilling_D_List.add(directBilling_D);
			}
			directBilling_D_Repository.save(directBilling_D_List);
			
			if(!directBillingDTOList.isEmpty()&&directBillingDTOList.get(0).getMobileNumber().toString()!=null&&
					directBillingDTOList.get(0).getCustomerName()!=null) {
				Long id = loginService.getCurrentUser().getEntityId();
				excustomerMaster= customerMasterRepository.findCustomerByMobilenumber_and_name(directBillingDTOList.get(0).getMobileNumber().toString(),
						directBillingDTOList.get(0).getCustomerName(), loginService.getCurrentUser().getEntityId());
				
				
				
				if(excustomerMaster == null) {
					customerMaster.setActiveStatus(true);
					if(userMaster!=null) {
						customerMaster.setCreatedBy(loginService.getCurrentUser());
					}
					customerMaster.setEntityId(loginService.getCurrentUser().getEntityId());
					customerMaster.setPrimaryContactNumber(directBillingDTOList.get(0).getMobileNumber().toString());
					customerMaster.setCreatedDate(new Date());
					customerMaster.setName(directBillingDTOList.get(0).getCustomerName());
					customerMaster.setRegistrationDate(new Date());
					customerMaster.setVersion(0l);
					
					customerMasterRepository.save(customerMaster);
				}
				
			}
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			baseDTO.setResponseContent(directBillingDTO);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(" = Excetion Occured ON DirectillingService Save Method Call = "+e);
		}
		return baseDTO;
	}
	
	public BaseDTO autoCompleteMobileNo(String mobno) {
		log.info("Inside CustomerBillingService Class autoCompleteMobileNo method");
		BaseDTO response = new BaseDTO();
		List<CustomerMaster> customerMasterList = new ArrayList<>();
		CustomerMaster customerMaster = new CustomerMaster();
		try {
			mobno = "%" + mobno.trim() + "%";
			Long entityId = loginService.getCurrentUser().getEntityId();
			List<CustomerMaster> customerMasterlistt = customerMasterRepository.loadMobileNumberAutoSearch(mobno,
					entityId);
			log.info("Retrival Process from Repository Done");
			if (customerMasterlistt != null) {
				for (CustomerMaster cus : customerMasterlistt) {
					customerMaster = new CustomerMaster();
					if (cus.getName() != null && cus.getPrimaryContactNumber() != null) {
						customerMaster.setId(cus.getId());
						customerMaster.setName(cus.getName());
						customerMaster.setPrimaryContactNumber(cus.getPrimaryContactNumber());
						customerMasterList.add(customerMaster);
					}

				}
			}
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			log.info("<<<  === Success Response === >>>");
			response.setResponseContents(customerMasterList);
		} catch (Exception e) {
			log.error("Exception Occured In DirectBillingService Service autoCompleteSupplier", e);
		}
		return response;
	}

}
