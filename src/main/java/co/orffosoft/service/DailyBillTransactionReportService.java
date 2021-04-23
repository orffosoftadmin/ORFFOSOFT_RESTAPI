package co.orffosoft.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.DailyTransactionReportDTO;
import co.orffosoft.repository.Bill_D_Repository;
import co.orffosoft.repository.ItemPriceRepository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.SalesReturnRepository;
import co.orffosoft.repository.StockTransactionRepository;
import co.orffosoft.repository.StockTransactionTypeRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DailyBillTransactionReportService {

	@Autowired
	LoginService loginService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	StockTransactionTypeRepository stockTransactionTypeRepository;

	@Autowired
	SalesReturnRepository salesReturnRepository;

	@Autowired
	ItemPriceRepository itemPriceRepository;

	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@Autowired
	Bill_D_Repository billdReposetory;

	@PersistenceContext
	EntityManager manager;
	
	DecimalFormat df = new DecimalFormat("0.00");

	@Transactional
	@SuppressWarnings("unchecked")
	public BaseDTO generateReport(DailyTransactionReportDTO items) {
		log.info(" >> DailyTransactionReportService >>  generateReport method >> ");
		BaseDTO response = new BaseDTO();
		List<DailyTransactionReportDTO> dailyTransactionReportDTOList = new ArrayList<>();
		Map<Long, DailyTransactionReportDTO> dailyTransactionReportDTOMap = new HashMap();
		DailyTransactionReportDTO dailyTransactionReportDTO = new DailyTransactionReportDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String query = null;
		Query sql = null;
		List<Object[]> obj = null;
		Iterator<Object[]> billReport = null;
		String parameter = "";
		Long autoIncrement = 0L;
		try {
			// commented as on 11-04-2021
			
//			query = "  select t1.bill_h_bill_no, t1.name , t1.bill_h_date , sum(netprice-t1.discount_value + t1.cgst_value+ t1.sgst_value) as netPrice ,"
//					+ "  t1.username, t1.billHPk,t1.mob from  "
//					+ "	 (select billh.bill_h_bill_no , cm.name, billh.bill_h_date,   "
//					+ "	 abs(sum(st.stock_tran_issued_qnty - st.stock_tran_received_qnty) * st.stock_tran_sellingprice) as netprice,  "
//					+ "	 um.username, billh.bill_h_pk as billHPk,cm.primary_contact_number	as mob, st.discount_value, st.cgst_value, st.sgst_value "
//					+ "	 from bill_h billh  "
//					+ "	 left join customer_master cm on cm.id = billh.bill_h_customer_fk      "
//					+ "	 inner join user_master um on um.id = billh.created_by     "
//					+ "	 inner join stock_transaction st on st.bill_d_bill_h_fk = billh.bill_h_pk     "
//					+ "	 where :input   "
//					+ "	 group by billh.bill_h_bill_no, cm.name, billh.bill_h_date, st.stock_tran_sellingprice,um.username,billh.bill_h_pk,cm.primary_contact_number,"
//					+ " st.discount_value,st.cgst_value, st.sgst_value) as t1    "
//					+ "	 group by t1.bill_h_bill_no, t1.name, t1.bill_h_date, t1.username, t1.billHPk,t1.mob ";
					
			query = "  select billh.bill_h_pk ,billh.bill_h_bill_no as billno, cm.name, cm.primary_contact_number, billh.bill_h_date,"
					+ " case when  sum(billh.bill_h_net_amount - sales_return.net_total) is null then sum(billh.bill_h_net_amount)  "
					+ " else sum(billh.bill_h_net_amount - sales_return.net_total) end as netAmt, um.username "
					+ " from bill_h billh "
					+ " left join customer_master cm on cm.id = billh.bill_h_customer_fk "
					+ " inner join user_master um on um.id = billh.created_by "
					+ " left join sales_return sales_return on sales_return.bill_h_fk = billh.bill_h_pk "
					+ " where :input "
					+ " group by billh.bill_h_pk ,billh.bill_h_bill_no, cm.name, cm.primary_contact_number, billh.bill_h_date,um.username ";

			if (items.getFromDate() != null) {
				Calendar calFromDate = Calendar.getInstance();
				calFromDate.setTime(items.getFromDate());

				StringTokenizer fromDate = new StringTokenizer(sdf.format(calFromDate.getTime()), " ");
				while (fromDate.hasMoreTokens()) {
					items.setFromDateStr(fromDate.nextToken());
					break;
				}

				parameter = parameter + " billh.bill_h_date between ' " + items.getFromDateStr() + "' and ";
			}
			if (items.getToDate() != null) {
				Calendar calToDate = Calendar.getInstance();
				calToDate.setTime(items.getToDate());

				StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
				while (toDate.hasMoreTokens()) {
					items.setToDateStr(toDate.nextToken());
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
			log.info(" Query == " + query);
			sql = manager.createNativeQuery(query);

			obj = sql.getResultList();
			billReport = obj.iterator();
			if (billReport != null) {
				while (billReport.hasNext()) {
					dailyTransactionReportDTO = new DailyTransactionReportDTO();
					Object[] ob = billReport.next();
					autoIncrement++;
					
					if (ob[0] != null) {
						dailyTransactionReportDTO.setBillhpk(Long.parseLong(ob[0].toString()));
					}
					if (ob[1] != null) {
						dailyTransactionReportDTO.setBillNo(ob[1].toString());
					}

					if (ob[2] != null) {
						dailyTransactionReportDTO.setCustomerName(ob[2].toString());
					}
					
					if (ob[3] != null) {
						dailyTransactionReportDTO.setCustomerMobileNo(Long.parseLong(ob[3].toString()));
					}

					if (ob[4] != null) {
						dailyTransactionReportDTO.setBillDate((Date) ob[4]);
					}
					
					if (ob[5] != null) {
						dailyTransactionReportDTO.setNetPrice(Double.valueOf(ob[5].toString()));
					}
					
					if (ob[6] != null) {
						dailyTransactionReportDTO.setUserName(ob[6].toString());
					}
					dailyTransactionReportDTOList.add(dailyTransactionReportDTO);

				}
				// List<Map<String, Object>> listMapForExcel = new ArrayList<>();
				// for (Long dto : dailyTransactionReportDTOMap.keySet()) {
				// Map<String, Object> map = new HashMap<>();
				// DailyTransactionReportDTO dailyDto = dailyTransactionReportDTOMap.get(dto);
				// map.put("ItemName", dailyDto.getItemName());
				// map.put("BillNo",dailyDto.getBillNo());
				// map.put("BillDate",dailyDto.getBillDate());
				// map.put("QNTY",dailyDto.getQnty());
				// map.put("UnitPrice",dailyDto.getUnitPrice());
				// map.put("TotalUnitprice",dailyDto.getTotalUnitPrice());
				// map.put("Discount",dailyDto.getDiscount());
				// map.put("Cgst%",dailyDto.getCgstPercent());
				// map.put("CgstAmt",dailyDto.getCgstAmount());
				// map.put("Sgst%",dailyDto.getSgstPercent());
				// map.put("SgstAmt",dailyDto.getSgstAmount());
				// map.put("NetPrice",dailyDto.getNetPrice());
				// map.put("UserName",dailyDto.getUserName());
				//
				// listMapForExcel.add(map);
				// dailyTransactionReportDTOList.add(dailyTransactionReportDTOMap.get(dto));
				// }
				response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
				// response.setListOfData(listMapForExcel);
				response.setResponseContents(dailyTransactionReportDTOList);
			}

			log.info("<<<  === Success Response === >>>");
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> generateReport method >> ", e);
		}
		return response;
	}

	public BaseDTO viewBillDetails(DailyTransactionReportDTO items) {
		// log.info(" >> DailyTransactionReportService >> viewBillDetails method >> ");
		BaseDTO response = new BaseDTO();
		Query sql = null;
		List<Object[]> viewBillDetail = null;
		try {
			Iterator<Object[]> billReport = null;
			DailyTransactionReportDTO dailyTransactionReportDTO;
			List<DailyTransactionReportDTO> listdailyTransactionReportDTO = new ArrayList();
			String query = "";
			
			// commented as on 10-04-2021
			
//			query = " select t1.name, t1.ItemQnty, t1.unitPrice,t1.cgst_value, t1.sgst_value, t1.discountValue "
//					+ " from ( select  pvm.id, pvm.name , abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty)) as ItemQnty,  "
//					+ " st.stock_tran_sellingprice as unitPrice , st.cgst_value, st.sgst_value, "
//					+ " sum(coalesce(st.discount_value,0))  as discountValue " + " from stock_transaction st "
//					+ " inner join product_variety_master pvm on pvm.id = st.stock_tran_sku_id_fk  "
//					+ " where st.bill_d_bill_h_fk = " + items.getBillhpk()
//					+ " group by pvm.id, pvm.name, st.stock_tran_sellingprice, st.cgst_value, st.sgst_value) as t1";
				
			
			query = " select pvm.name , case when sum( billD.bill_d_item_qnty - salesr.item_qty) is null then sum(billD.bill_d_item_qnty) " + 
					"  else sum( billD.bill_d_item_qnty - salesr.item_qty) end as qnty,  billD.bill_d_unitrate as unitprice, " + 
					"  case when sum (billD.bill_d_cgst_value - salesr.cgst_value) is null then sum(billD.bill_d_cgst_value) " + 
					"  else sum (billD.bill_d_cgst_value - salesr.cgst_value) end as totcgst, " + 
					"  case when sum (billD.bill_d_sgst_value - salesr.sgst_value) is null then sum (billD.bill_d_sgst_value) " + 
					"  else sum (billD.bill_d_sgst_value - salesr.sgst_value) end  as totsgst,  " + 
					"  case when sum (billD.bill_d_discount_value / billD.bill_d_item_qnty * salesr.item_qty) is null then "
					+ " sum(billD.bill_d_discount_value) else sum (billD.bill_d_discount_value / billD.bill_d_item_qnty * salesr.item_qty) end as totdiscount "+ 
					" from bill_d billD  " + 
					" left join sales_return_items salesr on salesr.bill_d_fk = billD.bill_d_pk  " + 
					" inner join product_variety_master pvm on pvm.id = billD.bill_d_sku_fk  " + 
					"  where billD.bill_d_bill_h_fk =  "+ items.getBillhpk() +
					"  group by pvm.name , billD.bill_d_unitrate ";
						
			log.info(" Query = " + query);
			sql = manager.createNativeQuery(query);
			viewBillDetail = sql.getResultList();
			billReport = viewBillDetail.iterator();
			if (billReport != null) {
				while (billReport.hasNext()) {
					dailyTransactionReportDTO = new DailyTransactionReportDTO();
					Object[] obj = billReport.next();
					if (obj[0] != null) {
						dailyTransactionReportDTO.setItemName(obj[0].toString());
					}
					if (obj[1] != null) {
						dailyTransactionReportDTO.setQnty(Double.parseDouble(obj[1].toString()));
					}
					if (obj[2] != null) {
						dailyTransactionReportDTO.setUnitPrice(Double.parseDouble(obj[2].toString()));
					}
					if (obj[3] != null) {
						dailyTransactionReportDTO.setCgstAmount(Double.parseDouble(obj[3].toString()));
					} else {
						dailyTransactionReportDTO.setCgstAmount(0.0);
					}
					if (obj[4] != null) {
						dailyTransactionReportDTO.setSgstAmount(Double.parseDouble(obj[4].toString()));
					} else {
						dailyTransactionReportDTO.setSgstAmount(0.0);
					}
					if (obj[5] != null) {
						dailyTransactionReportDTO.setDiscount(Double.parseDouble(obj[5].toString()));
					} else {
						dailyTransactionReportDTO.setDiscount(0.0);
					}
					dailyTransactionReportDTO.setTotalUnitPrice(
							dailyTransactionReportDTO.getQnty() * dailyTransactionReportDTO.getUnitPrice());
					dailyTransactionReportDTO.setNetPrice(Double.valueOf(df.format(dailyTransactionReportDTO.getTotalUnitPrice()
							+ dailyTransactionReportDTO.getCgstAmount() + dailyTransactionReportDTO.getSgstAmount()
							- dailyTransactionReportDTO.getDiscount())));

					listdailyTransactionReportDTO.add(dailyTransactionReportDTO);
				}
			}

			response.setResponseContents(listdailyTransactionReportDTO);
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());

			log.info("<<<  === Success Response === >>>");
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> viewBillDetails method >> ", e);
		}
		return response;

	}

	public BaseDTO viewReturnDetails(DailyTransactionReportDTO items) {
		// log.info(" >> DailyTransactionReportService >> viewBillDetails method >> ");
		BaseDTO response = new BaseDTO();

		try {
			Iterator<Object[]> billReport = null;
			DailyTransactionReportDTO dailyTransactionReportDTO;
			List<DailyTransactionReportDTO> listviewreturniter = new ArrayList();
			List<Object[]> ob = salesReturnRepository.ReturnItems(items.getBillhpk());
			billReport = ob.iterator();

			if (billReport != null) {
				while (billReport.hasNext()) {
					dailyTransactionReportDTO = new DailyTransactionReportDTO();
					Object[] obj = billReport.next();

					if (obj[0] != null) {
						dailyTransactionReportDTO.setItemName(obj[0].toString());
					}
					if (obj[1] != null) {
						dailyTransactionReportDTO.setQnty(Double.parseDouble(obj[1].toString()));
					}
					if (obj[2] != null) {
						dailyTransactionReportDTO.setNetPrice(Double.parseDouble(obj[2].toString()));
					}
					if (obj[3] != null) {
						dailyTransactionReportDTO.setBillDate((Date) obj[3]);
					}
					if (obj[4] != null) {
						dailyTransactionReportDTO.setUserName(obj[4].toString());
					}

					listviewreturniter.add(dailyTransactionReportDTO);
				}
			}

			response.setResponseContents(listviewreturniter);
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());

			// slog.info("<<< === Success Response === >>>");
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> viewBillDetails method >> ", e);
		}
		return response;

	}

}
