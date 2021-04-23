package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.AppUtil;
import co.orffosoft.core.util.Constants;
import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.core.util.StockTransactionTypeIFC;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.CustomerBillingDTO;
import co.orffosoft.entity.Bill_D;
import co.orffosoft.entity.Bill_H;
import co.orffosoft.entity.CustomerCreditNoteDetail;
import co.orffosoft.entity.CustomerCreditNoteHeader;
import co.orffosoft.entity.CustomerMaster;
import co.orffosoft.entity.EntityMaster;
import co.orffosoft.entity.ItemPrice;
import co.orffosoft.entity.ProductVarietyMaster;
import co.orffosoft.entity.StockTransaction;
import co.orffosoft.entity.StockTransactionType;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.repository.Bill_D_Repository;
import co.orffosoft.repository.Bill_H_Repository;
import co.orffosoft.repository.CustomerCreditNoteDetailRepository;
import co.orffosoft.repository.CustomerCreditNoteHeaderRepository;
import co.orffosoft.repository.CustomerMasterRepository;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.GoodsReceiptNote_D_Repository;
import co.orffosoft.repository.ItemPriceRepository;
import co.orffosoft.repository.PaymentModeRepository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import co.orffosoft.repository.StockTransactionRepository;
import co.orffosoft.repository.StockTransactionTypeRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class CustomerBillingService {

	@Autowired
	LoginService loginService;

	@PersistenceContext
	EntityManager manager;

	@Autowired
	CustomerMasterRepository customerMasterRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	CustomerCreditNoteHeaderRepository customerCreditNoteHeaderRepository;
	
	@Autowired
	CustomerCreditNoteDetailRepository customerCreditNoteDetailRepository;

	@Autowired
	StockTransactionTypeRepository stockTransactionTypeRepository;

	@Autowired
	GoodsReceiptNote_D_Repository goodsReceiptNote_D_Repository;

	@Autowired
	ItemPriceRepository itemPriceRepository;
	
	@Autowired
	PaymentModeRepository paymentModeRepository;

	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;

	@Autowired
	Bill_H_Repository bill_H_Repository;

	@Autowired
	Bill_D_Repository bill_D_Repository;

	@Autowired
	EntityMasterRepository entityMasterRepository;

	HashMap<String, Long> priceAndItemPricePkMap2 = new HashMap<>();

	@Autowired
	SequenceConfigService sequenceConfigService;

	/**
	 * @param mobno
	 * @return
	 */
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
			log.error("Exception Occured In CustomerBillingService Service autoCompleteSupplier", e);
		}
		return response;
	}

	/**
	 * @param mobno
	 * @return
	 */
	@Transactional
	public BaseDTO billCreation(CustomerBillingDTO dtoo) {
		
		dtoo.getBillingDtoList().removeIf(x->x.getItemId()== null);
		List<CustomerBillingDTO> billingDtoList = dtoo.getBillingDtoList();
		BaseDTO response = new BaseDTO();
		CustomerBillingDTO customerBillingDTO = new CustomerBillingDTO();
		log.info("Inside CustomerBillingService Class billCreation method");
		Bill_H bill_H = new Bill_H();
		Bill_D bill_D = new Bill_D();
		CustomerMaster customerMaster = new CustomerMaster();
		CustomerMaster excustomerMaster = new CustomerMaster();
		CustomerCreditNoteDetail customerCreditNoteDetail = new CustomerCreditNoteDetail();
		StockTransaction stockTransaction = new StockTransaction();
		Integer paymentmodee=paymentModeRepository.paymentmode(dtoo.getPaymentmode());
		try {
			//log.info("Inside Try Block");
			Long userId = loginService.getCurrentUser().getId();
			bill_H = new Bill_H();
			bill_H.setBill_h_date(new Date());
			if (billingDtoList != null && billingDtoList.size() > 0
					&& billingDtoList.get(0).getCustomerBillingType().equalsIgnoreCase("WITH_CUSTOMER")) {
				if (billingDtoList.get(0).getCustomerMaster() != null
						&& billingDtoList.get(0).getCustomerMaster().getId() != null) {
					bill_H.setBill_h_customer_fk(billingDtoList.get(0).getCustomerMaster().getId());
				} else {
					excustomerMaster = customerMasterRepository.findCustomerByMobilenumber(
							billingDtoList.get(0).getCustomerMaster().getPrimaryContactNumber().toString(),
							loginService.getCurrentUser().getEntityId());
					if (excustomerMaster == null) {
						customerMaster.setName(billingDtoList.get(0).getCustomerMaster().getName().toUpperCase());
						customerMaster.setPrimaryContactNumber(
								billingDtoList.get(0).getCustomerMaster().getPrimaryContactNumber());
						customerMaster.setCreatedBy(loginService.getCurrentUser());
						customerMaster.setEntityId(loginService.getCurrentUser().getEntityId());
						customerMaster.setCreatedDate(new Date());
						customerMaster=customerMasterRepository.save(customerMaster);
						bill_H.setBill_h_customer_fk(customerMaster.getId());
					}

				}

			}
			List<String> billNumberAndStoreName = sequenceConfigService
					.generateBillNumberAndSave(SequenceName.BILL_PAYEMENT);
			if (billNumberAndStoreName != null && billNumberAndStoreName.size() > 0) {
				bill_H.setBill_h_bill_no(billNumberAndStoreName.get(0));
				// bill_H.setStoreName(billNumberAndStoreName.get(1));
			}

			// commented on 6/11/2019
			// bill_H.setBill_h_net_amount(billingDtoList.stream().mapToDouble(i ->
			// i.getBill_D_NetAmount()).sum());

			bill_H.setBill_h_net_amount(billingDtoList.stream()
					.mapToDouble(i -> i.getTotalNetPrice() == null ? 0 : i.getTotalNetPrice()).sum());
			bill_H.setCreated_by(userId);
			bill_H.setCreated_date(new Date());
			bill_H.setStoreEntity(entityMasterRepository.findOne(loginService.getCurrentUser().getEntityId()));
			bill_H.setCase_mode(paymentmodee);
			bill_H.setCollect_amount(dtoo.getReciveAmount());
			bill_H.setBill_status("billed");
			bill_H = bill_H_Repository.save(bill_H);

			StockTransactionType st = stockTransactionTypeRepository.getGRNID(StockTransactionTypeIFC.SALE);

			if (bill_H != null) {
				for (CustomerBillingDTO dto : billingDtoList) {
					if (dto.getItemId() != null) {
						bill_D = new Bill_D();
						bill_D.setBill_d_bill_h_fk(bill_H.getBill_h_pk());
						bill_D.setBill_d_sku_fk(dto.getItemId());
						bill_D.setBill_d_item_qnty(dto.getBilledQnty());
						bill_D.setBill_d_unitrate(dto.getUnitRate());
						bill_D.setBill_d_totalunitrate(dto.getTotalRate());
						bill_D.setBill_d_cgst_percentage(Double.parseDouble(dto.getCgstPercent().toString()));
						bill_D.setBill_d_cgst_value(dto.getCgstAmount());
						if (dto.getSgstPercent() != null) {
							bill_D.setBill_d_sgst_percentage(Double.parseDouble(dto.getSgstPercent().toString()));
							bill_D.setBill_d_sgst_value(dto.getSgstAmount());
						}
						bill_D.setBill_d_purchase_price(dto.getBill_D_PurchasePrice());
						bill_D.setBill_d_discount_percentage(dto.getDiscountPercent());
						bill_D.setBill_d_discount_value(dto.getBill_D_DiscountValue());
						bill_D.setBill_d_net_amount(dto.getTotalNetPrice());
						bill_D.setCreated_by(userId);
						bill_D.setCreated_date(new Date());
						bill_D.setItem_barcode_number(dto.getBarcode());
						bill_D_Repository.save(bill_D);

						// Item Price Saving
						ItemPrice itemPrice = itemPriceRepository.findOne(dto.getItemPricePk());
						if (itemPrice == null) {
							throw new RestException("Item Price Pk Not Found" + itemPrice);
						}
						Double finalClosingStock = 0.0D;
						if (dto.getUomName() != null && !(dto.getUomName().isEmpty())) {
							Double closingStock = stockTransactionRepository
									.findClosingQnty(itemPrice.getItemprice_pk());
							if (dto.getUomName().equalsIgnoreCase(Constants.FOOT_INCH)) {
								finalClosingStock = AppUtil.getCalculateClosingStockForFeetAndInch(dto.getFeet(),
										dto.getInches(), closingStock);
								itemPrice.setItem_closing_stock(finalClosingStock);
							} else {
								itemPrice.setItem_closing_stock(AppUtil
										.getClosingStock(itemPrice.getItem_closing_stock(), dto.getBilledQnty()));
							}
							itemPriceRepository.save(itemPrice);
						}

						// For Stock Update In Stock Transaction Table

						if (st != null) {
							stockTransaction = new StockTransaction();

							stockTransaction
									.setProductVarietyMaster(productVarietyMasterRepository.findOne(dto.getItemId()));
							stockTransaction.setStock_tran_date(new Date());
							stockTransaction.setStock_tran_type_fk(st.getStock_tran_type_pk());
							stockTransaction.setStock_tran_received_qnty(0D);
							stockTransaction.setStock_tran_issued_qnty(dto.getBilledQnty());
							if (dto.getUnitRate() != null) {
								stockTransaction.setStock_tran_sellingprice(dto.getUnitRate());
							}
							if (dto.getBill_D_PurchasePrice() != null) {
								stockTransaction.setStock_tran_purchaseprice(dto.getBill_D_PurchasePrice());
							}
							stockTransaction.setCreated_by(loginService.getCurrentUser().getId());
							stockTransaction.setCreated_date(new Date());
							if (bill_D != null) {
								stockTransaction.setBill_d_fk(bill_D.getBill_d_pk());
							}
							if (bill_H != null) {
								stockTransaction.setBill_d_bill_h_fk(bill_H.getBill_h_pk());
							}
							stockTransaction.setStoreEntity(
									entityMasterRepository.findOne(loginService.getCurrentUser().getEntityId()));

							stockTransaction.setStock_tran_item_price_fk(dto.getItemPricePk());

							stockTransaction.setExpiryDate(dto.getExpiryDate());
							stockTransaction.setCgstvalue(dto.getCgstAmount());
							stockTransaction.setSgstvalue(dto.getSgstAmount());
							stockTransaction.setDiscountvalue(dto.getBill_D_DiscountValue());

							stockTransactionRepository.save(stockTransaction);

						}

					}

				}

			}
			if (dtoo.getReciveAmount() < dtoo.getTotalNetPrice() && dtoo != null) {
				
				EntityMaster entitymaster=entityMasterRepository.findOne(loginService.getCurrentUser().getEntityId());
				
				if (billingDtoList.get(0).getCustomerMaster() != null
						&& billingDtoList.get(0).getCustomerMaster().getPrimaryContactNumber() != null && billingDtoList.get(0).getCustomerMaster().getName()!=null) {
					CustomerCreditNoteHeader excustomerCreditNoteHeader = new CustomerCreditNoteHeader();
					if(billingDtoList.get(0).getCustomerMaster() != null
							&& billingDtoList.get(0).getCustomerMaster().getId() != null) {
						 excustomerCreditNoteHeader = customerCreditNoteHeaderRepository.checkCustomer(billingDtoList.get(0).getCustomerMaster().getId()) ;
					}else {
					 excustomerCreditNoteHeader = customerCreditNoteHeaderRepository.checkCustomer(customerMaster.getId()) ;
					}
					
					if(excustomerCreditNoteHeader==null) {
						excustomerCreditNoteHeader = new CustomerCreditNoteHeader();
						excustomerCreditNoteHeader.setTransactionDate(new Date());
						excustomerCreditNoteHeader.setCreated_by(loginService.getCurrentUser().getId());
						excustomerCreditNoteHeader.setCreated_date(new Date());
						excustomerCreditNoteHeader.setBillhfk(bill_H);
						excustomerCreditNoteHeader.setTotalCreditAmount(dtoo.getTotalNetPrice()-dtoo.getReciveAmount());
						excustomerCreditNoteHeader.setEntitymaster(entitymaster);
						List<String> billNumberAndStoreName1 = sequenceConfigService
								.generateBillNumberAndSave(SequenceName.CreditNote);
						if (billNumberAndStoreName1 != null && billNumberAndStoreName1.size() > 0) {
							excustomerCreditNoteHeader.setCusCreditBillNo(billNumberAndStoreName1.get(0));
						}
						
						if(billingDtoList.get(0).getCustomerMaster().getId()!=null) {
							excustomerCreditNoteHeader.setCustomerMaster(customerMasterRepository.findOne(billingDtoList.get(0).getCustomerMaster().getId()));
						}else {
							if(customerMaster!=null&& customerMaster.getId()!=null) {
								excustomerCreditNoteHeader.setCustomerMaster( customerMasterRepository.findOne(customerMaster.getId()));
							}
						}
						//excustomerCreditNoteHeader.setCus_Address(customerCreditNoteDTO.getCustomerAddress());
						customerCreditNoteHeaderRepository.saveAndFlush(excustomerCreditNoteHeader);
					}else {
						excustomerCreditNoteHeader.setTotalCreditAmount((dtoo.getTotalNetPrice()-dtoo.getReciveAmount())+excustomerCreditNoteHeader.getTotalCreditAmount());
						customerCreditNoteHeaderRepository.save(excustomerCreditNoteHeader);
					}
						
				
					if( excustomerCreditNoteHeader!=null && excustomerCreditNoteHeader.getCusCreditNotePk()!=null) {
						
						
						
						customerCreditNoteDetail.setCustomerCreditNoteHeader(excustomerCreditNoteHeader);
						customerCreditNoteDetail.setPaidAmount(0d);
						customerCreditNoteDetail.setCreatedBy(loginService.getCurrentUser().getId());
						customerCreditNoteDetail.setCreatedDate(new Date());
						customerCreditNoteDetail.setCreditAmount(dtoo.getTotalNetPrice()-dtoo.getReciveAmount());
						customerCreditNoteDetail.setBillhfk(bill_H);
						customerCreditNoteDetail.setTransaction(new Date());
						customerCreditNoteDetail.setTransactiontype("BILL");
						customerCreditNoteDetail.setBillh_billno(bill_H.getBill_h_bill_no());
						customerCreditNoteDetail.setEntitymaster(entitymaster);
						
						customerCreditNoteDetailRepository.save(customerCreditNoteDetail);
					}

				} 

			}

			if (billingDtoList != null) {
				// customerBillingDTO = new CustomerBillingDTO();
				customerBillingDTO.setCustomerBillingDTOList(billingDtoList);
			}

			customerBillingDTO.setStoreName(loginService.getCurrentUser().getEntityName());
			customerBillingDTO.setBill_H_BillNo(bill_H.getBill_h_bill_no());
			customerBillingDTO.setReciveAmount(dtoo.getReciveAmount());
			//log.info("<<<  === Success Response === >>>");
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContent(customerBillingDTO);
			response.setGeneralContent(bill_H.getBill_h_bill_no());
		} catch (RestException e) {
			response.setMessage(e.getMessage());
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getCode());
			log.error("Exception Occured In CustomerBillingService billCreation", e);
		} catch (Exception e) {
			response.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getCode());
			log.error("Exception Occured In CustomerBillingService billCreation", e);
		}
		log.info("<<<  === End Of CustomerBillingService billCreation === >>>");
		return response;

	}

	public BaseDTO autoCompleteItemNameWithPrice(String itemName, String barcodeType, boolean barcode) {
		BaseDTO response = new BaseDTO();
		List<ProductVarietyMaster> productVarietyMasterList = new ArrayList<>();
		ProductVarietyMaster productVarietyMaster = new ProductVarietyMaster();
		try {
			itemName = "%" + itemName.trim() + "%";
			Query sql = null;
			String parameter = "";

			String query = "select case when itm.itemprice_new_price >= 0 then itm.itemprice_new_price else  itm.itemprice_old_price end as price ,itm.item_barcode_number,  "
					+ " itm.itemprice_purchase_price, itemprice_pk,  "
					+ " itm.item_closing_stock as rateWiseStock, pvm.name  "
					+ " ,pvm.id as itemId, pvm.cgst_percentage as cgstper, pvm.sgst_percentage as sgstper,  "
					+ " pvm.hsn_code as hsnCode,uom.name as uomname  " + " from itemPrice itm   "
					+ " left join stock_transaction st on itm.itemprice_pk = st.stock_tran_item_price_fk  "
					+ " left join product_variety_master pvm on pvm.id = itm.itemprice_sku_fk  "
					+ " inner join uom_master uom on uom.id=pvm.uom_id " + " where item_price_store_entity_fk = "
					+ loginService.getCurrentUser().getEntityId() + " input  "
					+ " group by itm.itemprice_purchase_price, itemprice_pk, pvm.name,pvm.id,uom.name ";

			if (barcodeType.equals("withbarcode") && barcode == true) {

				parameter = parameter + " and itm.item_barcode_number ilike '" + itemName + ",' ";

			} else {
				parameter = parameter + " and (pvm.name ilike '" + itemName + "' or pvm.code ilike '" + itemName
						+ "') ";
			}

			query = query.replaceAll("input", parameter);
			sql = manager.createNativeQuery(query);
			List<Object> itemWithPriceDetails = sql.getResultList();

			if (itemWithPriceDetails != null) {
				for (Object itemWithPriceDetail : itemWithPriceDetails) {
					Object[] itemWithPrice = (Object[]) itemWithPriceDetail;
					productVarietyMaster = new ProductVarietyMaster();

					productVarietyMaster.setCustomerBillingDTO(new CustomerBillingDTO());
					productVarietyMaster.getCustomerBillingDTO()
							.setSellingPrice(Double.parseDouble(itemWithPrice[0].toString()));
					if (itemWithPrice[1] != null) {
						productVarietyMaster.setBarcodenumber(itemWithPrice[1].toString());
					}
					productVarietyMaster.getCustomerBillingDTO()
							.setPurchasePrice(Double.parseDouble(itemWithPrice[2].toString()));
					productVarietyMaster.getCustomerBillingDTO()
							.setItemPricePk(Long.parseLong(itemWithPrice[3].toString()));
					productVarietyMaster.setItemPricePk(Long.parseLong(itemWithPrice[3].toString()));
					if (Double.valueOf(itemWithPrice[4].toString()) == 0D) {
						productVarietyMaster.setDiseabledItem(true);
					} else {
						productVarietyMaster.getCustomerBillingDTO()
								.setRateQTY(Double.valueOf(itemWithPrice[4].toString()));
					}

					productVarietyMaster.setDisplayName(itemWithPrice[5].toString() + " ( pp: "
							+ "xxx" + ", sp: "
							+ productVarietyMaster.getCustomerBillingDTO().getSellingPrice() + ")");
					productVarietyMaster.setName(itemWithPrice[5].toString());

					productVarietyMaster.setId(Long.parseLong(itemWithPrice[6].toString()));
					if (itemWithPrice[7] == null) {
						productVarietyMaster.setCgst_percentage(0.0);
					} else {
						productVarietyMaster.setCgst_percentage(Double.parseDouble(itemWithPrice[7].toString()));
					}
					if (itemWithPrice[8] == null) {
						productVarietyMaster.setSgst_percentage(0.0);
					} else {
						productVarietyMaster.setSgst_percentage(Double.parseDouble(itemWithPrice[8].toString()));
					}
					if (itemWithPrice[10] != null) {
						productVarietyMaster.setUomname(itemWithPrice[10].toString());
						;
					}
					productVarietyMaster.setHsnCode(itemWithPrice[9].toString());
					productVarietyMaster.getCustomerBillingDTO().setList_expiryDate(stockTransactionRepository
							.getexpirydate(productVarietyMaster.getId(), loginService.getCurrentUser().getEntityId()));

					productVarietyMasterList.add(productVarietyMaster);

				}
			}

			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(productVarietyMasterList);
		} catch (Exception e) {
			log.error("Exception Occured In StockItemInwardPNS Service autoCompleteSupplier", e);
		}
		return response;
	}

}
