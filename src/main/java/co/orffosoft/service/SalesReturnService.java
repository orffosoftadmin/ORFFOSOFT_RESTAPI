package co.orffosoft.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

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
import co.orffosoft.dto.SalesReturnDTO;
import co.orffosoft.entity.Bill_D;
import co.orffosoft.entity.Bill_H;
import co.orffosoft.entity.ItemPrice;
import co.orffosoft.entity.SalesReturn;
import co.orffosoft.entity.SalesReturnItems;
import co.orffosoft.entity.StockTransaction;
import co.orffosoft.entity.StockTransactionType;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.repository.Bill_D_Repository;
import co.orffosoft.repository.Bill_H_Repository;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.ItemPriceRepository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.SalesReturnItemsRepository;
import co.orffosoft.repository.SalesReturnRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import co.orffosoft.repository.StockTransactionRepository;
import co.orffosoft.repository.StockTransactionTypeRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SalesReturnService {

	@Autowired
	LoginService loginService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	Bill_H_Repository bill_H_Repository;

	@Autowired
	ItemPriceRepository itemPriceRepository;

	@Autowired
	EntityMasterRepository entityMasterRepository;

	@Autowired
	StockTransactionTypeRepository stockTransactionTypeRepository;

	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@PersistenceContext
	EntityManager manager;

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;

	@Autowired
	SalesReturnRepository salesReturnRepository;

	@Autowired
	SalesReturnItemsRepository salesReturnItemsRepository;

	@Autowired
	Bill_D_Repository bill_D_Repository;

	@Autowired
	SequenceConfigService sequenceConfigService;

	@Transactional
	public BaseDTO generateReport(SalesReturnDTO items) {
		log.info(" >> DailyTransactionReportService >>  generateReport method >> ");
		BaseDTO response = new BaseDTO();
		List<SalesReturnDTO> salesReturnDTOList = new ArrayList<>();
		SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String query = null;
		Query sql = null;
		List<Object[]> obj = null;
		Iterator<Object[]> billReport = null;
		String parameter = "";
		try {
			/*
			 * query =
			 * " select bill_h.bill_h_pk as bill_h_pk , bill_h.bill_h_bill_no as billNo , cm.name as customerName , cm.primary_contact_number as mobileNumber, "
			 * +
			 * " bill_h.bill_h_date as billDate,  abs(sum(st.stock_tran_received_qnty-st.stock_tran_issued_qnty) * st.stock_tran_sellingprice) as netAmount ,"
			 * +
			 * "  um.username as userName ,billd.bill_d_cgst_percentage,billd.bill_d_sgst_percentage "
			 * +
			 * " from bill_h bill_h left join customer_master cm on bill_h.bill_h_customer_fk = cm.id "
			 * + " inner join user_master um on um.id = bill_h.created_by" +
			 * " inner join stock_transaction st on st.bill_d_bill_h_fk = bill_h.bill_h_pk "
			 * + "left join bill_d billd on billd.bill_d_bill_h_fk=bill_h.bill_h_pk" +
			 * " where :input ";
			 */

			query = "select t1.billHPk ,  t1.bill_h_bill_no , t1.name,t1.primary_contact_number , t1.bill_h_date,  sum(netprice) as netprice,  t1.username "
					+ " from  "
					+ " (select billh.bill_h_pk as billHPk ,  billh.bill_h_bill_no , cm.name,cm.primary_contact_number , billh.bill_h_date,  "
					+ " abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty ) * st.stock_tran_sellingprice) as netprice, "
					+ " um.username " + " from bill_h billh   "
					+ " left join customer_master cm on cm.id = billh.bill_h_customer_fk    "
					+ " inner join user_master um on um.id = billh.created_by   "
					+ " inner join stock_transaction st on st.bill_d_bill_h_fk = billh.bill_h_pk   " + " where :input "
					+ " group by billh.bill_h_bill_no, cm.name, billh.bill_h_date, st.stock_tran_sellingprice,um.username,billh.bill_h_pk,  "
					+ " cm.primary_contact_number) as t1  "
					+ " group by t1.bill_h_bill_no, t1.name, t1.bill_h_date, t1.username, t1.billHPk, t1.primary_contact_number ";

			if (items.getFromDate() != null) {
				Calendar calFromDate = Calendar.getInstance();
				calFromDate.setTime(items.getFromDate());

				StringTokenizer fromDate = new StringTokenizer(sdf.format(calFromDate.getTime()), " ");
				while (fromDate.hasMoreTokens()) {
					items.setFromDateStr(fromDate.nextToken() + " 00:00:00 ");
					break;
				}

				parameter = parameter + " billh.bill_h_date between ' " + items.getFromDateStr() + "' and ";
			}
			if (items.getToDate() != null) {
				Calendar calToDate = Calendar.getInstance();
				calToDate.setTime(items.getToDate());

				StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
				while (toDate.hasMoreTokens()) {
					items.setToDateStr(toDate.nextToken() + " 23:59:59 ");
					break;
				}

				parameter = parameter + "' " + items.getToDateStr() + "' and ";
			}
			if (items.getBillNo() != null && !items.getBillNo().isEmpty() && items.getBillNo().length() > 0) {
				parameter = parameter + " billh.bill_h_bill_no ilike '%" + items.getBillNo() + "%' and ";
			}
			if (items.getCustomerName() != null && !items.getCustomerName().isEmpty()
					&& items.getCustomerName().length() > 0) {
				parameter = parameter + " cm.name ilike '%" + items.getCustomerName() + "%' and ";
			}
			if (items.getCustomerMobileNo() != null) {
				parameter = parameter + " cm.primary_contact_number ilike '%" + items.getCustomerMobileNo() + "%' and ";
			}
			parameter = parameter + " billh.bill_h_entity_fk =" + loginService.getCurrentUser().getEntityId();

			query = query.replaceAll(":input", parameter);
			log.info("Sales Return List Query " + query);
			sql = manager.createNativeQuery(query);

			obj = sql.getResultList();
			billReport = obj.iterator();
			if (billReport != null) {
				while (billReport.hasNext()) {
					salesReturnDTO = new SalesReturnDTO();
					Object[] ob = billReport.next();

					if (ob[0] != null) {
						salesReturnDTO.setBill_h_pk(Long.parseLong(ob[0].toString()));
					}

					if (ob[1] != null) {
						salesReturnDTO.setBillNo(ob[1].toString());
					}

					if (ob[2] != null) {
						salesReturnDTO.setCustomerName(ob[2].toString());
					}

					if (ob[3] != null) {
						salesReturnDTO.setCustomerMobileNo(Long.parseLong(ob[3].toString()));
					}

					if (ob[4] != null) {
						salesReturnDTO.setBillDate((Date) (ob[4]));
					}

					if (ob[5] != null) {

						salesReturnDTO.setNetPrice(Double.parseDouble(ob[5].toString()));
					}

					if (ob[6] != null) {
						salesReturnDTO.setUserName(ob[6].toString());
					}
					/*
					 * commented as on 07-03-2021 due to new column (cgstValue, SgstValue,
					 * discountValue) add in stockTransaction to add GST value in NetPrice if (ob[7]
					 * != null) { salesReturnDTO.setCgstPercent(Long.parseLong(ob[7].toString()) );
					 * } if (ob[8] != null) {
					 * salesReturnDTO.setSgstPercent(Long.parseLong(ob[8].toString())); }
					 * 
					 * if(salesReturnDTO.getCgstPercent()!=null&&salesReturnDTO.getSgstPercent()!=
					 * null) { Long
					 * gst=salesReturnDTO.getCgstPercent()+salesReturnDTO.getSgstPercent();
					 * salesReturnDTO.setNetPrice(salesReturnDTO.getNetPrice()+(salesReturnDTO.
					 * getNetPrice()*gst)/100);
					 * 
					 * }
					 */

					salesReturnDTOList.add(salesReturnDTO);
				}
			}

			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(salesReturnDTOList);
			log.info("<<<  === Success Response === >>>");
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> generateReport method >> ", e);
		}
		return response;

	}

	/**
	 * @param dto
	 * @return
	 */
	public BaseDTO getBillDetailsByID(SalesReturnDTO dto) {
		log.info(" >> DailyTransactionReportService >>  getBillDetailsByID method >> ");
		BaseDTO response = new BaseDTO();
		List<SalesReturnDTO> salesReturnDTOList = new ArrayList<>();
		SalesReturnDTO salesReturnDTO = new SalesReturnDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String query = null;
		Query sql = null;
		List<Object[]> obj = null;
		Iterator<Object[]> billReport = null;
		try {
			List<Bill_D> billList = bill_D_Repository.getBillDPk(dto.getBill_h_pk());

			List<Long> billDPk = billList.stream().map(e -> e.getBill_d_pk()).collect(Collectors.toList());
			String billDPKStr = "";
			for (Long l : billDPk) {
				billDPKStr = billDPKStr + l.toString() + " , ";
			}
			int index = billDPKStr.length() - 2;

			billDPKStr = billDPKStr.substring(0, index);

			query = " select bilH.bill_h_pk as bill_h_pk, bilD.bill_d_pk as bill_d_pk, pvm.id as itemId, cm.id as customerFk , "
					+ " cm.name as CustomerName , " + " pvm.name as ItemName ,  pvm.hsn_code as HsnCode ,  "
					+ " abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty)) as ItemQnty , "
					+ " bilD.bill_d_unitrate as UnitRate  , bilD.bill_d_cgst_percentage as CGSTPercentage , bilD.bill_d_cgst_value as CGSTValue , "
					+ " bilD.bill_d_sgst_percentage as SGSTPercentage,  bilD.bill_d_sgst_value as SGSTValue , "
					+ " bilD.bill_d_totalunitrate as TotalUnitRate ,  "
					+ " bilD.bill_d_discount_value as DiscountValue , coalesce(bilD.bill_d_discount_percentage,0) as discountPercentage , bild.item_barcode_number ,"
					+ " st.stock_tran_item_price_fk , bilD.bill_d_purchase_price as purchesprice, uom.name as uomName  "
					+ " from bill_h bilH inner join bill_d bilD on bilH.bill_h_pk = bilD.bill_d_bill_h_fk  "
					+ " inner join stock_transaction st  on st.bill_d_fk=bilD.bill_d_pk  "
					+ " inner join product_variety_master pvm on bilD.bill_d_sku_fk = pvm.id  "
					+ " inner join user_master um on um.id = bilH.created_by "
					+ " left join customer_master cm on cm.id = bilH.bill_h_customer_fk  "
					+ " inner join uom_master uom on uom.id = pvm.uom_id " + " where st.bill_d_fk in ("
					+ billDPKStr.trim() + ")  and st.stock_tran_store_entity_fk ="
					+ loginService.getCurrentUser().getEntityId()
					+ " group by bilH.bill_h_pk , bilD.bill_d_pk, pvm.id, cm.id, st.stock_tran_item_price_fk, uom.name "
					+ " order by bilH.bill_h_date asc";
			log.info(" Query = " + query);
			sql = manager.createNativeQuery(query);
			obj = sql.getResultList();
			billReport = obj.iterator();
			if (billReport != null) {
				while (billReport.hasNext()) {
					salesReturnDTO = new SalesReturnDTO();
					Object[] ob = billReport.next();

					if (ob[0] != null) {
						salesReturnDTO.setBill_h_pk(Long.parseLong(ob[0].toString()));
					}

					if (ob[1] != null) {
						salesReturnDTO.setBill_d_pk(Long.parseLong(ob[1].toString()));
					}

					if (ob[2] != null) {
						salesReturnDTO.setItemId(Long.parseLong(ob[2].toString()));
					}

					if (ob[3] != null) {
						salesReturnDTO.setCustomerFk(Long.parseLong(ob[3].toString()));
					}

					if (ob[4] != null) {
						salesReturnDTO.setCustomerName(ob[4].toString());
					}
					if (ob[5] != null) {
						salesReturnDTO.setItemName(ob[5].toString());
					}

					if (ob[6] != null) {
						salesReturnDTO.setHsnCode(ob[6].toString());
					}

					if (ob[7] != null) {
						salesReturnDTO.setQnty(Double.valueOf(ob[7].toString()));
					}

					if (ob[8] != null) {
						salesReturnDTO.setUnitPrice(Double.parseDouble(ob[8].toString()));
					}

					if (ob[9] != null) {
						salesReturnDTO.setCgstPercent(Double.parseDouble(ob[9].toString()));
					}

					if (ob[11] != null) {
						salesReturnDTO.setSgstPercent(Double.parseDouble(ob[11].toString()));
					}

					if (ob[13] != null) {
						if (salesReturnDTO.getQnty() > 0.0 && salesReturnDTO.getUnitPrice() > 0.0) {

							salesReturnDTO.setTotalUnitPrice(salesReturnDTO.getQnty() * salesReturnDTO.getUnitPrice());
						}
					}
					if (salesReturnDTO.getTotalUnitPrice() != null) {
						salesReturnDTO.setCgstAmount(
								salesReturnDTO.getTotalUnitPrice() * salesReturnDTO.getCgstPercent() / 100);
						salesReturnDTO.setSgstAmount(
								salesReturnDTO.getTotalUnitPrice() * salesReturnDTO.getSgstPercent() / 100);
					}

					if (ob[14] != null) {
						BigDecimal b = (BigDecimal) ob[14];
						salesReturnDTO.setDiscount(Double.valueOf(b.doubleValue()));
					} else {
						salesReturnDTO.setDiscount(0D);
					}
					
					if (ob[15] != null) {
						salesReturnDTO.setDiscountPercentage(Double.parseDouble(ob[15].toString()));
					}

					
					if (ob[16] != null) {
						salesReturnDTO.setBarcode(ob[16].toString());
					}
					if (salesReturnDTO.getTotalUnitPrice() != null && salesReturnDTO.getCgstAmount() != null
							&& salesReturnDTO.getSgstAmount() != null) {
						Double totalgstAmt = salesReturnDTO.getCgstAmount() + salesReturnDTO.getSgstAmount();
						salesReturnDTO.setNetPrice(salesReturnDTO.getTotalUnitPrice() + (totalgstAmt - salesReturnDTO.getDiscount()));
					}
					if (ob[17] != null) {
						salesReturnDTO.setItemPricePk(Long.parseLong(ob[17].toString()));
					}
					if (ob[18] != null) {
						salesReturnDTO.setPurchesprice(Double.valueOf(ob[18].toString()));
					}
					if (ob[19] != null) {
						salesReturnDTO.setUomName(ob[19].toString());
					}
					// need to fix purchse price in stocktransaction table

					salesReturnDTO.setReturnedQnty(0.0);
					salesReturnDTO.setReturnedAmount(0D);

					salesReturnDTOList.add(salesReturnDTO);
				}
			}

			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(salesReturnDTOList);
			// log.info("<<< === Success Response === >>>");
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> getBillDetailsByID method >> ", e);
		}
		return response;

	}

	/**
	 * @param dto
	 * @return
	 */
	@Transactional
	public BaseDTO submit(SalesReturnDTO dto) {
		log.info("<<<  === Inside SalesReturn Service ==>> submit === >>>");
		BaseDTO response = new BaseDTO();
		SalesReturn salesReturn = new SalesReturn();
		StockTransaction stockTransaction = new StockTransaction();
		SalesReturnItems salesReturnItems = new SalesReturnItems();
		Bill_H billh = new Bill_H();

		try {
			boolean valid = checkReturnQnty(dto.getSalesReturnDTOList());
			if (dto != null && valid) {
				salesReturn.setBill_h_fk(dto.getBill_h_pk());

				List<String> billNumberAndStoreName = sequenceConfigService
						.generateBillNumberAndSave(SequenceName.SALES_RETURN);
				if (billNumberAndStoreName != null && billNumberAndStoreName.size() > 0) {
					salesReturn.setBill_no(billNumberAndStoreName.get(0));
				}

				salesReturn.setReturn_date(new Date());
				salesReturn.setNet_total(
						dto.getSalesReturnDTOList().stream().mapToDouble(i -> i.getReturnedAmount()).sum());
				salesReturn.setCreated_by(loginService.getCurrentUser().getId());
				salesReturn.setCreated_date(new Date());
				salesReturn.setEntityfk(loginService.getCurrentUser().getEntityId());
				if (dto.getCustomerFk() != null) {
					salesReturn.setCustomer_fk(dto.getCustomerFk());
				}

				SalesReturn salesRt = salesReturnRepository.save(salesReturn);

				if (salesRt == null) {
					response.setMessage("Sales Return Not Saved");
					log.error(">>>>>> Sales Return Not Saved Properly see log Files >>>");
					new RestException("Sales Return Not Saved");
				}

				billh = bill_H_Repository.findOne(dto.getBill_h_pk());

				Double qnty = dto.getSalesReturnDTOList().stream().mapToDouble(x -> x.getQnty()).sum();
				Double reqnty = dto.getSalesReturnDTOList().stream().mapToDouble(x -> x.getReturnedQnty()).sum();

				if (qnty.equals(reqnty)) {
					billh.setBill_status("billcanceled");
					bill_H_Repository.save(billh);

				} else {
					billh.setBill_status("partialcanceled");
					bill_H_Repository.save(billh);

				}

				StockTransactionType st = stockTransactionTypeRepository.getGRNID(StockTransactionTypeIFC.SALE_RETURN);
				if (st == null) {
					response.setMessage("Stock Transaction Type Not Found For Sales Return");
					new RestException("Stock Transaction Type Not Found");
				}

				for (SalesReturnDTO dt : dto.getSalesReturnDTOList()) {
					if (dt.getReturnedQnty() != null && dt.getReturnedQnty() > 0) {
						salesReturnItems = new SalesReturnItems();
						salesReturnItems.setSales_return_id(salesRt.getId());
						salesReturnItems.setSku_id(dt.getItemId());
						salesReturnItems.setItem_qty(dt.getReturnedQnty());
						salesReturnItems.setPurchase_price(dto.getUnitPrice());
						salesReturnItems.setCreated_by(loginService.getCurrentUser().getId());
						salesReturnItems.setCreated_date(new Date());
						if (dt.getCgstPercent() != null) {
							salesReturnItems.setCgst_value(dt.getReturnedCgstAmount());
						}
						if (dt.getSgstPercent() != null) {
							salesReturnItems.setSgst_value(dt.getReturnedSgstAmount());
						}

						salesReturnItems.setNet_amount(dt.getReturnedAmount());
						salesReturnItems.setSelling_price(dto.getUnitPrice());
						salesReturnItems.setBill_d_fk(dt.getBill_d_pk());
						salesReturnItemsRepository.save(salesReturnItems);

						// Item Price Saving
						ItemPrice itemPrice = itemPriceRepository.findOne(dt.getItemPricePk());

						if (itemPrice == null) {
							throw new RestException("Item Price Pk Not Found" + itemPrice);
						}
						Double finalClosingStock = 0.0D;
						if (dt.getUomName() != null && !(dt.getUomName().isEmpty())) {
							Double closingStock = stockTransactionRepository
									.findClosingQnty(itemPrice.getItemprice_pk());
							if (dt.getUomName().equalsIgnoreCase(Constants.FOOT_INCH)) {
								finalClosingStock = AppUtil.getCalculateClosingStockForFeetAndInch(dt.getFeet(),
										dt.getInches(), closingStock);
								itemPrice.setItem_closing_stock(finalClosingStock);
							} else {
								itemPrice.setItem_closing_stock(
										itemPrice.getItem_closing_stock() + dt.getReturnedQnty());
							}
							itemPriceRepository.save(itemPrice);
						}

						stockTransaction = new StockTransaction();
						stockTransaction.setStock_tran_date(new Date());
						stockTransaction.setStock_tran_type_fk(st.getStock_tran_type_pk());
						stockTransaction
								.setStock_tran_received_qnty(Double.parseDouble(dt.getReturnedQnty().toString()));
						stockTransaction.setStock_tran_issued_qnty(0.0);
						if (dto.getUnitPrice() != null) {
							stockTransaction.setStock_tran_sellingprice(dto.getUnitPrice());
						}
						stockTransaction.setCreated_by(loginService.getCurrentUser().getId());
						stockTransaction.setCreated_date(new Date());
						stockTransaction.setStock_tran_sellingprice(dt.getUnitPrice());
						if (dt.getBill_d_pk() != null) {
							stockTransaction.setBill_d_fk(dt.getBill_d_pk());
						}
						if (dt.getBill_h_pk() != null) {
							stockTransaction.setBill_d_bill_h_fk(dt.getBill_h_pk());
						}
						if (dt.getItemId() != null) {
							stockTransaction
									.setProductVarietyMaster(productVarietyMasterRepository.findOne(dt.getItemId()));
						}
						if (dt.getItemPricePk() == null) {
							throw new RestException(" ItemPriceId not Found");
						}
						stockTransaction.setStock_tran_item_price_fk(itemPrice.getItemprice_pk());

						stockTransaction.setStoreEntity(
								entityMasterRepository.findOne(loginService.getCurrentUser().getEntityId()));

						stockTransaction.setStock_tran_purchaseprice(dt.getPurchesprice());
						stockTransaction.setCgstvalue(dt.getReturnedCgstAmount());
						stockTransaction.setSgstvalue(dt.getReturnedSgstAmount());
						if (dt.getDiscount() == null) {
							dt.setDiscount(0D);
						}
						stockTransaction.setDiscountvalue(dt.getDiscount());
						stockTransactionRepository.save(stockTransaction);
					}
				}
				response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			} else {
				response.setMessage("No Requrted Qn Avail");
			}

		} catch (Exception e) {
			log.error("Exception Occured IN SalesReturn Service", e);
		}
		return response;
	}

	private boolean checkReturnQnty(List<SalesReturnDTO> salesReturnDTOList) {

		boolean valid = false;

		Optional<SalesReturnDTO> list = salesReturnDTOList.stream().filter(e -> e.getReturnedQnty() > 0).findAny();
		if (list.isPresent()) {
			valid = true;
		}

		return valid;

	}
}
