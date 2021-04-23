package co.orffosoft.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.core.util.Validate;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.CustomerCreditNoteDTO;
import co.orffosoft.entity.Bill_H;
import co.orffosoft.entity.CustomerCreditNoteDetail;
import co.orffosoft.entity.CustomerCreditNoteHeader;
import co.orffosoft.entity.CustomerMaster;
import co.orffosoft.entity.EntityMaster;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.repository.Bill_H_Repository;
import co.orffosoft.repository.CustomerCreditNoteDetailRepository;
import co.orffosoft.repository.CustomerCreditNoteHeaderRepository;
import co.orffosoft.repository.CustomerMasterRepository;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import co.orffosoft.repository.UserMasterRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CustomerCreditNoteService {

	@Autowired
	CustomerCreditNoteHeaderRepository customerCreditNoteHeaderRepository;

	@PersistenceContext
	EntityManager manager;

	@Autowired
	CustomerCreditNoteDetailRepository customerCreditNoteDetailRepository;

	@Autowired
	CustomerMasterRepository customerMasterRepository;

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;

	@Autowired
	Bill_H_Repository bill_H_Repository;

	@Autowired
	EntityMasterRepository entityMasterRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	EntityManager em;

	@Autowired
	JdbcTemplate jdbcTemplet;

	@Autowired
	SequenceConfigService sequenceConfigService;

	public BaseDTO PaymentRecive(List<CustomerCreditNoteDTO> listcustomerCreditNoteDTO) {
		log.error("<------ Start PaymentRecive------>");
		BaseDTO response = new BaseDTO();
		try {
			CustomerCreditNoteDetail creditnonedetail;
			CustomerCreditNoteHeader customerCreditNoteHeader;
			EntityMaster entity = entityMasterRepository.findId(loginService.getCurrentUser().getEntityId());
			Bill_H billh;
			if (!listcustomerCreditNoteDTO.isEmpty()) {
				for (CustomerCreditNoteDTO dto : listcustomerCreditNoteDTO) {

					if (dto.getReceiveAmount() != null && dto.getReceiveAmount() > 0.0) {
						billh = new Bill_H();
						creditnonedetail = new CustomerCreditNoteDetail();

						if (dto.getBill_h_fk() != null) {

							billh = bill_H_Repository.findOne(dto.getBill_h_fk());
						}
						customerCreditNoteHeader = customerCreditNoteHeaderRepository
								.findOne(dto.getCusHeaderCreditNotePk());

						creditnonedetail.setCustomerCreditNoteHeader(customerCreditNoteHeader);
						creditnonedetail.setPaidAmount(dto.getReceiveAmount());
						creditnonedetail.setCreatedBy(loginService.getCurrentUser().getId());
						creditnonedetail.setCreatedDate(new Date());
						creditnonedetail.setCreditAmount(0.0);
						if (billh.getBill_h_pk() != null) {
							creditnonedetail.setBillhfk(billh);
						}
						creditnonedetail.setTransaction(new Date());
						creditnonedetail.setTransactiontype(dto.getTransactiontype());
						creditnonedetail.setBillh_billno(dto.getBillhbillno());
						creditnonedetail.setEntitymaster(entity);

						customerCreditNoteDetailRepository.save(creditnonedetail);
					}

				}
				response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			}

		} catch (Exception e) {
			log.error("Error in PaymentRecive in service  ", e);
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return response;
	}

	public BaseDTO getdetail(CustomerCreditNoteDTO customerCreditNoteDTO) {
		BaseDTO response = new BaseDTO();

		CustomerCreditNoteDTO customerDTO = null;
		List<CustomerCreditNoteDTO> listcustomerDTO = new ArrayList();

		try {
			if (customerCreditNoteDTO != null || customerCreditNoteDTO.getCusHeaderCreditNotePk() != null) {
				List<Object> ob = customerCreditNoteHeaderRepository
						.getdetail(customerCreditNoteDTO.getCusHeaderCreditNotePk());

				Iterator<?> it = ob.iterator();

				while (it.hasNext()) {
					Object ob1[] = (Object[]) it.next();
					customerDTO = new CustomerCreditNoteDTO();

					if (ob1[0] != null) {
						customerDTO.setTransactiontype(ob1[0].toString());
					}
					if (ob1[1] != null) {
						customerDTO.setBillhbillno(ob1[1].toString());
					}
					if (ob1[2] != null) {
						customerDTO.setTotalCreditAmount(Double.parseDouble(ob1[2].toString()));
					}
					if (ob1[3] != null) {
						customerDTO.setPaidAmount(Double.parseDouble(ob1[3].toString()));
					}
					if (ob1[4] != null) {
						customerDTO.setBill_h_fk(Long.parseLong(ob1[4].toString()));
					}
					if (ob1[5] != null) {
						customerDTO.setCusHeaderCreditNotePk(Long.parseLong(ob1[5].toString()));
					}

					if (customerDTO.getTotalCreditAmount() != null && customerDTO.getPaidAmount() != null) {
						customerDTO.setTotalPendingAmount(
								customerDTO.getTotalCreditAmount() - customerDTO.getPaidAmount());
					}

					listcustomerDTO.add(customerDTO);

				}

			}

			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			response.setResponseContents(listcustomerDTO);

		} catch (Exception e) {
			log.error("Error in getdetail method ", e);
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}

		return response;
	}

	public BaseDTO save(CustomerCreditNoteDTO customerCreditNoteDTO) {
		log.info("<< CustomerCreditNoteService create start >>" + customerCreditNoteDTO);
		BaseDTO response = new BaseDTO();
		CustomerCreditNoteHeader existingRecord = null;
		CustomerMaster customerMaster = new CustomerMaster();
		CustomerCreditNoteDetail creditnotedetail = new CustomerCreditNoteDetail();
		EntityMaster entity = entityMasterRepository.findId(loginService.getCurrentUser().getEntityId());
		try {
			Validate.notNull(customerCreditNoteDTO, ErrorDescription.SUPPLIER_MASTER_SHOULD_NOT_BE_EMPTY);
			Validate.notNullOrEmpty(customerCreditNoteDTO.getCustomerMaster().getName(),
					ErrorDescription.SUPPLIER_MASTER_CODE_SHOULD_NOT_BE_EMPTY);
			Validate.notNullOrEmpty(customerCreditNoteDTO.getCustomerMaster().getPrimaryContactNumber(),
					ErrorDescription.SUPPLIER_MASTER_NAME_SHOULD_NOT_BE_EMPTY);

			if (customerCreditNoteDTO.getCustomerMaster().getPrimaryContactNumber() != null) {

				customerMaster = customerMasterRepository.findCustomerByMobilenumber_and_name(
						customerCreditNoteDTO.getCustomerMaster().getPrimaryContactNumber(),
						customerCreditNoteDTO.getCustomerMaster().getName(),
						loginService.getCurrentUser().getEntityId());
				if (customerMaster == null) {
					customerMaster = new CustomerMaster();

					customerMaster.setName(customerCreditNoteDTO.getCustomerMaster().getName().toUpperCase());
					customerMaster.setRegistrationDate(new Date());
					customerMaster.setPrimaryContactNumber(
							customerCreditNoteDTO.getCustomerMaster().getPrimaryContactNumber());
					customerMaster.setActiveStatus(true);
					customerMaster.setCreatedBy(loginService.getCurrentUser());
					customerMaster.setCreatedDate(new Date());
					customerMaster.setVersion(0l);
					customerMaster.setEntityId(loginService.getCurrentUser().getEntityId());
					customerMaster.setCustomerAddress(customerCreditNoteDTO.getCustomerAddress());

					customerMaster = customerMasterRepository.save(customerMaster);
				}

				existingRecord = customerCreditNoteHeaderRepository.checkCustomer(customerMaster.getId());
				if (existingRecord == null) {

					existingRecord = new CustomerCreditNoteHeader();
					existingRecord.setTransactionDate(new Date());
					existingRecord.setCreated_by(loginService.getCurrentUser().getId());
					existingRecord.setCreated_date(new Date());
					existingRecord.setTotalCreditAmount(customerCreditNoteDTO.getTotalCreditAmount());
					existingRecord.setBillhfk(null);
					existingRecord.setEntitymaster(entity);
					List<String> billNumberAndStoreName = sequenceConfigService
							.generateBillNumberAndSave(SequenceName.CreditNote);
					if (billNumberAndStoreName != null && billNumberAndStoreName.size() > 0) {
						existingRecord.setCusCreditBillNo(billNumberAndStoreName.get(0));
					}

					if (customerMaster != null && customerMaster.getId() != null) {
						existingRecord.setCustomerMaster(customerMaster);
					}
					customerCreditNoteDTO.setCreditBillNo(existingRecord.getCusCreditBillNo());

					response.setStatusCode(0);

					existingRecord = customerCreditNoteHeaderRepository.save(existingRecord);
				} else {
					existingRecord.setTotalCreditAmount(
							existingRecord.getTotalCreditAmount() + customerCreditNoteDTO.getTotalCreditAmount());
					customerCreditNoteHeaderRepository.save(existingRecord);
				}

				creditnotedetail.setCustomerCreditNoteHeader(existingRecord);
				creditnotedetail.setPaidAmount(0d);
				creditnotedetail.setCreatedBy(loginService.getCurrentUser().getId());
				creditnotedetail.setCreatedDate(new Date());
				creditnotedetail.setCreditAmount(customerCreditNoteDTO.getTotalCreditAmount());
				creditnotedetail.setTransaction(new Date());
				creditnotedetail.setTransactiontype("CREDIT");
				creditnotedetail.setEntitymaster(entity);

				List<String> billNumberAndStoreName = sequenceConfigService
						.generateBillNumberAndSave(SequenceName.CreditNote);
				if (billNumberAndStoreName != null && billNumberAndStoreName.size() > 0) {
					creditnotedetail.setBillh_billno(billNumberAndStoreName.get(0));

				}

				customerCreditNoteDetailRepository.save(creditnotedetail);
			}

			// }

			// saving to Detail Table
			log.info("== Saving to CustomerNoteCedit Detail Yet to Start == ");
			// saveCustomerCreditNoteDetail(existingRecord, customerCreditNoteDTO);
			log.info("== Saving to CustomerNoteCedit Detail Finished == ");
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			log.info("Successfully saved" + existingRecord);

		} catch (RestException exception) {

			response.setStatusCode(response.getStatusCode());

			log.error("supplierMaster RestException ", exception);

		} catch (DataIntegrityViolationException exception) {

			log.error("Data Integrity Violation Exception while Creating supplierMaster ", exception.getMessage());
			String exceptionCause = ExceptionUtils.getRootCauseMessage(exception);
			log.error("Exception Cause 1 : " + exceptionCause);
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
			if (exceptionCause.contains("uq_supplier_master_code")) {
				response.setStatusCode(ErrorDescription.SUPPLIER_MASTER_CODE_SHOULD_NOT_BE_DUPLICATE.getErrorCode());
			}

		} catch (Exception exception) {

			log.error("Error while Creating supplierMaster ", exception);

			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}

		return response;
	}

	public BaseDTO search(CustomerCreditNoteDTO customerCreditNoteDTO) {
		BaseDTO response = new BaseDTO();
		CustomerCreditNoteDTO dto = new CustomerCreditNoteDTO();
		Session session = em.unwrap(Session.class);
		Criteria criteria = session.createCriteria(CustomerCreditNoteHeader.class, "customerCreditNoteHeader");

		criteria.createAlias("customerCreditNoteHeader.customerMaster", "customerMaster",
				CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias("customerCreditNoteHeader.entitymaster", "entitymaster", CriteriaSpecification.INNER_JOIN);

		try {

			if (customerCreditNoteDTO.getPaginationDTO().getFilters() != null) {

				if (customerCreditNoteDTO.getPaginationDTO().getFilters().get("customerName") != null) {
					String customerName = (String) customerCreditNoteDTO.getPaginationDTO().getFilters()
							.get("customerName");

					log.info(" customerName " + customerName);

					criteria.add(Restrictions
							.or(Restrictions.like("customerMaster.name", "%" + customerName + "%").ignoreCase()));
				}

				if (customerCreditNoteDTO.getPaginationDTO().getFilters().get("customerMobileNo") != null) {
					String contactNumber = (String) customerCreditNoteDTO.getPaginationDTO().getFilters()
							.get("customerMobileNo");

					log.info(" contactNumber " + contactNumber);

					criteria.add(Restrictions.like("customerMaster.primaryContactNumber", "%" + contactNumber + "%")
							.ignoreCase());
				}

			}

			if (loginService.getCurrentUser().getId() != 809) {
				// criteria.add(
				// Restrictions.eq("customerCreditNoteHeader.created_by",
				// loginService.getCurrentUser().getId()));
				criteria.add(Restrictions.eq("customerCreditNoteHeader.entitymaster.id",
						loginService.getCurrentUser().getEntityId()));
			}
			criteria.setProjection(Projections.rowCount());
			Integer totalResult = ((Long) criteria.uniqueResult()).intValue();
			criteria.setProjection(null);

			// For Pagination
			if (customerCreditNoteDTO.getPaginationDTO() != null) {
				Integer pageNo = customerCreditNoteDTO.getPaginationDTO().getPageNo();
				Integer pageSize = customerCreditNoteDTO.getPaginationDTO().getPageSize();

				if (pageNo != null && pageSize != null) {
					criteria.setFirstResult(pageNo * pageSize);
					criteria.setMaxResults(pageSize);
					log.info("PageNo : [" + pageNo + "] pageSize[" + pageSize + "]");
				}

				String sortField = customerCreditNoteDTO.getPaginationDTO().getSortField();
				String sortOrder = customerCreditNoteDTO.getPaginationDTO().getSortOrder();
				if (sortField != null && sortOrder != null) {
					log.info("sortField : [" + sortField + "] sortOrder[" + sortOrder + "]");

					if (sortField.equals("customerName")) {

						sortField = "customerMaster.name";

					} else if (sortField.equals("customerMobileNo")) {

						sortField = "customerMaster.primaryContactNumber";

					}

					if (sortOrder.equals("DESCENDING")) {
						criteria.addOrder(Order.desc(sortField));
					} else {
						criteria.addOrder(Order.asc(sortField));
					}
				} else {
					criteria.addOrder(Order.desc("customerCreditNoteHeader.created_date"));
				}
			}

			log.info("Criteria Query  : " + criteria);

			ProjectionList projectionList = Projections.projectionList();

			projectionList.add(Projections.sum("customerCreditNoteHeader.totalCreditAmount"));

			projectionList.add(Projections.groupProperty("cusCreditNotePk"));
			projectionList.add(Projections.groupProperty("customerMaster.name"));
			projectionList.add(Projections.groupProperty("customerMaster.primaryContactNumber"));

			criteria.setProjection(projectionList);
			List<CustomerCreditNoteDTO> customerCreditNoteDTOList = new ArrayList<>();
			List<?> resultList = criteria.list();
			if (resultList != null) {
				Iterator<?> it = resultList.iterator();
				while (it.hasNext()) {
					Object ob[] = (Object[]) it.next();
					CustomerCreditNoteDTO customerCreditNoteDto = null;

					customerCreditNoteDto = new CustomerCreditNoteDTO();

					if (ob[0] != null) {
						customerCreditNoteDto.setTotalCreditAmount(Double.parseDouble(ob[0].toString()));
					}
					if (ob[1] != null) {
						customerCreditNoteDto.setCusHeaderCreditNotePk(Long.parseLong(ob[1].toString()));
					}
					if (ob[2] != null) {
						customerCreditNoteDto.setCustomerName(ob[2].toString());
					}
					if (ob[3] != null) {
						customerCreditNoteDto.setCustomerMobileNo(Long.parseLong(ob[3].toString()));
					}
					customerCreditNoteDTOList.add(customerCreditNoteDto);
				}

			}
			dto=gettotalPaymentDetails();
			response.setTotalRecords(totalResult);
			response.setResponseContents(customerCreditNoteDTOList);
			response.setResponseContent(dto);
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());

		} catch (RestException restException) {
			response.setStatusCode(restException.getStatusCode());
			log.error("RestException in CustomerCreditNoteService search() ", restException);
		} catch (Exception exception) {
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
			log.error("Exception in CustomerCreditNoteService search() ", exception);
		}

		return response;

	}

	private CustomerCreditNoteDTO gettotalPaymentDetails() {
		CustomerCreditNoteDTO dto = new CustomerCreditNoteDTO();
		try {

			List<Object> obj = customerCreditNoteDetailRepository
					.getpaymentdetails(loginService.getCurrentUser().getEntityId());
			Iterator<?> it = obj.iterator();
			Object ob[] = (Object[]) it.next();
			
//			while (it.hasNext()) {
//				Object ob[] = (Object[]) it.next();
//				
//			}
			
			dto.setTotalCreditAmount(Double.parseDouble(ob[0].toString()));
			dto.setTotalPaidAmount(Double.parseDouble(ob[1].toString()));

		} catch (Exception e) {
			log.error("Exception in CustomerCreditNoteService gettotalPaymentDetails() ", e);
		}
		return dto;

	}

	public BaseDTO get(Long id) {
		log.info(" Start CustomerCreditNoteService.  get()  ");
		BaseDTO response = new BaseDTO();
		CustomerCreditNoteDTO customerCreditNoteDto;
		List<CustomerCreditNoteDTO> listcustomerCreditNoteDto = new ArrayList();
		try {
			List<Object> ob = customerCreditNoteDetailRepository.getdetailbycredit_h_pk(id);
			Iterator<?> it = ob.iterator();
			while (it.hasNext()) {
				Object ob1[] = (Object[]) it.next();
				customerCreditNoteDto = new CustomerCreditNoteDTO();

				if (ob1[0] != null) {
					customerCreditNoteDto.setBillhbillno(ob1[0].toString());
				}
				if (ob1[1] != null) {
					customerCreditNoteDto.setTransactionDate((Date) ob1[1]);
				}
				if (ob1[2] != null) {
					customerCreditNoteDto.setTotalCreditAmount(Double.parseDouble(ob1[2].toString()));
				}
				if (ob1[3] != null) {
					customerCreditNoteDto.setPaidAmount(Double.parseDouble(ob1[3].toString()));
				}
				if (ob1[4] != null) {
					customerCreditNoteDto.setTransactiontype(ob1[4].toString());
				}
				if (ob1[5] != null) {
					customerCreditNoteDto.setUsername(ob1[5].toString());
				}

				listcustomerCreditNoteDto.add(customerCreditNoteDto);

			}

			response.setResponseContents(listcustomerCreditNoteDto);
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			log.info(" End  CustomerCreditNoteService.  get()  ");
		} catch (RestException restException) {
			response.setStatusCode(restException.getStatusCode());
			log.error("RestException in CustomerCreditNoteService get() ", restException);
		} catch (Exception exception) {
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
			log.error("Exception in CustomerCreditNoteService get() ", exception);
		}
		return response;
	}

	public BaseDTO update(CustomerCreditNoteDTO customerCreditNoteDTO) {
		// TODO Auto-generated method stub
		BaseDTO response = new BaseDTO();
		try {

			CustomerCreditNoteDetail exCustomerCreditNoteDetail = new CustomerCreditNoteDetail();

			// check previous customerFK with current CustomerFK if matched

			// then update customer master only

			// else check new customerfk present then use t

			//

			if (customerCreditNoteDTO != null && !customerCreditNoteDTO.getCustomerName().trim().equals("")) {

				CustomerMaster customerMaster = customerMasterRepository.findCustomerByMobilenumber_and_name(
						customerCreditNoteDTO.getCustomerMobileNo().toString(), customerCreditNoteDTO.getCustomerName(),
						loginService.getCurrentUser().getEntityId());

				if (customerMaster == null) {

					customerMaster = new CustomerMaster();
					customerMaster = customerCreditNoteDTO.getCustomerMaster();

					customerMaster.setName(customerCreditNoteDTO.getCustomerMaster().getName());
					customerMaster.setPrimaryContactNumber(
							customerCreditNoteDTO.getCustomerMaster().getPrimaryContactNumber().toString());
					customerMaster.setModifiedBy(loginService.getCurrentUser());
					customerMaster.setModifiedDate(new Date());

					customerMasterRepository.save(customerMaster);

					CustomerCreditNoteHeader customerCreditNoteHeader = customerCreditNoteHeaderRepository
							.findOne(customerCreditNoteDTO.getCusHeaderCreditNoteFk());

					if (customerCreditNoteHeader != null) {

						// customerCreditNoteHeader.setCusMobileNo(Long.parseLong(
						// customerCreditNoteDTO.getCustomerMobileNo().toString()) );
						// customerCreditNoteHeader.setCus_Name(customerCreditNoteDTO.getCustomerName());
						// customerCreditNoteHeader.setCus_Address(customerCreditNoteDTO.getCustomerAddress());
						customerCreditNoteHeader.setTotalCreditAmount(customerCreditNoteDTO.getTotalCreditAmount());
						customerCreditNoteHeader.setModified_date(new Date());

						customerCreditNoteHeaderRepository.save(customerCreditNoteHeader);

						exCustomerCreditNoteDetail = customerCreditNoteDetailRepository
								.updateLastPaidAmt(customerCreditNoteDTO.getCusHeaderCreditNoteFk());
						if (exCustomerCreditNoteDetail != null) {
							exCustomerCreditNoteDetail.setPaidAmount(customerCreditNoteDTO.getLastPaidAmount());
							exCustomerCreditNoteDetail.setModifiedBy(loginService.getCurrentUser().getId());
							exCustomerCreditNoteDetail.setModifiedDate(new Date());

							customerCreditNoteDetailRepository.save(exCustomerCreditNoteDetail);
							response.setStatusCode(0);

						}

					}

					response.setStatusCode(0);

				} else {

					throw new RestException("Customer alredy exist");
				}

			}

		} catch (Exception e) {
			log.error("Exception in CustomerCreditNoteService update() ", e);

		}
		return response;
	}

	public BaseDTO getdetailbyBillno(CustomerCreditNoteDTO customerCreditNoteDTO) {
		BaseDTO response = new BaseDTO();
		CustomerCreditNoteDTO dto;
		List<CustomerCreditNoteDTO> listdto = new ArrayList();
		;
		try {
			if (customerCreditNoteDTO != null && customerCreditNoteDTO.getBillhbillno() != null) {
				List<Object> custlist = customerCreditNoteDetailRepository
						.getdetailbybillno(customerCreditNoteDTO.getBillhbillno());

				Iterator<?> it = custlist.iterator();

				while (it.hasNext()) {
					Object ob1[] = (Object[]) it.next();
					dto = new CustomerCreditNoteDTO();

					if (ob1[0] != null) {
						dto.setTransactionDate((Date) ob1[0]);
					}
					if (ob1[1] != null) {
						dto.setTransactiontype(ob1[1].toString());
					}
					if (ob1[2] != null) {
						dto.setBillhbillno(ob1[2].toString());
					}
					if (ob1[3] != null) {
						dto.setTotalCreditAmount(Double.parseDouble(ob1[3].toString()));
					}

					listdto.add(dto);

				}
				response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
				response.setResponseContents(listdto);
			}

		} catch (Exception e) {
			log.error("Exception in CustomerCreditNoteService getdetailbyBillno() ", e);
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}

		return response;

	}

	public BaseDTO autoCompleteItem(String itemName) {
		BaseDTO basedto = new BaseDTO();
		// CustomerCreditNoteDTO customerCreditNoteDTO = new CustomerCreditNoteDTO();
		// List<CustomerCreditNoteDTO> listto = new ArrayList();
		// Iterator<?> ob;
		try {
			itemName = "%" + itemName.trim() + "%";
			Long entityId = loginService.getCurrentUser().getEntityId();
			List<CustomerMaster> listcustmermaster = customerMasterRepository.autocomplet(itemName, entityId);
			if (!listcustmermaster.isEmpty() && listcustmermaster != null) {
				basedto.setResponseContents(listcustmermaster);
			}

			basedto.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
		} catch (Exception e) {
			log.error("Exception in CustomerCreditNoteService autoCompleteItem() ", e);
			basedto.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return basedto;
	}

	public BaseDTO delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
