package co.orffosoft.service;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.ProfitAndLossReportDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ProfitAndLossReportService {

	@Autowired
	LoginService loginService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@PersistenceContext
	EntityManager manager;
	
//	@SuppressWarnings("unchecked")
//	@Transactional
	
	public BaseDTO generate(ProfitAndLossReportDTO profitAndLossReportDTO) {
		BaseDTO response = new BaseDTO();
		List<ProfitAndLossReportDTO> profitAndLossReportDTOList = new ArrayList<>();
		Map<Long, ProfitAndLossReportDTO> profitAndLossReportDTOMap = new HashMap();
		List<Map<String, Object>> listMapForExcel = new ArrayList<>();
		ProfitAndLossReportDTO profitAndLossReportDto = new ProfitAndLossReportDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String query = null;
		Query sql = null;
		List<?> obj = null;
		Iterator<Map<String, Object>> billReport = null;
		String parameter = "";
		Long autoIncrement = 0L;
		
		Long Entityid;

		try {
			if (profitAndLossReportDTO != null) {
				
				if(profitAndLossReportDTO.getEntityMaster()!=null) {
					Entityid=profitAndLossReportDTO.getEntityMaster().getId();
				}else {
					Entityid=loginService.getCurrentUser().getEntityId();
				}

//				query = "  select pvm.name, abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty)) as soldQnty,   "
//						+ " st.stock_tran_purchaseprice,  " + " st.stock_tran_sellingprice,   "
//						+ " abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) *  "
//						+ " st.stock_tran_purchaseprice) as TotalPurchasePrice,  "
//						+ " abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) *  "
//						+ " st.stock_tran_sellingprice) as TotalSellingPrice,  " + " '0' as returnedQnty,  "
//						+ " '0' as expiredQnty,  '0' as damagedQnty, '0' as stockAddQnty,  "
//						+ " '0' as stockMinusQnty from stock_transaction st  "
//						+ " inner join product_variety_master pvm on st.stock_tran_sku_id_fk = pvm.id   "
//						+ " where pvm.entityid ="+ loginService.getCurrentUser().getEntityId() +" and st.stock_tran_type_fk != 1"
//						+ " and st.stock_tran_type_fk != 2 :input"
//						+ " group by pvm.name,st.stock_tran_purchaseprice, st.stock_tran_sellingprice ";
				
				query=" select pvm.name, abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty)) as soldQnty,  " + 
						" st.stock_tran_purchaseprice,   st.stock_tran_sellingprice,     " + 
						" abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) *   st.stock_tran_purchaseprice) as TotalPurchasePrice, " + 
						" abs(sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty) *   st.stock_tran_sellingprice) as TotalSellingPrice,   " + 
						" '0' as returnedQnty,   '0' as expiredQnty,  '0' as damagedQnty, '0' as stockAddQnty,   '0' as stockMinusQnty ,st.stock_tran_date as date, st.discount_value " + 
						" from stock_transaction st   inner join product_variety_master pvm on st.stock_tran_sku_id_fk = pvm.id     " + 
						" where pvm.entityid ="+Entityid+" and st.stock_tran_type_fk != 1 and st.stock_tran_type_fk != 2 and st.stock_tran_type_fk !=5 " + 
						" and st.stock_tran_type_fk !=8 and st.stock_tran_type_fk !=7 :input " + 
						" group by pvm.name,st.stock_tran_purchaseprice, st.stock_tran_sellingprice,st.stock_tran_date ,st.discount_value order by st.stock_tran_date asc";

				if (profitAndLossReportDTO.getFromdate() != null) {
					Calendar calFromDate = Calendar.getInstance();
					calFromDate.setTime(profitAndLossReportDTO.getFromdate());

					StringTokenizer fromDate = new StringTokenizer(sdf.format(calFromDate.getTime()), " ");
					while (fromDate.hasMoreTokens()) {
						profitAndLossReportDTO.setFromDateStr(fromDate.nextToken());
						break;
					}

					parameter = parameter + " and st.stock_tran_date between '" + profitAndLossReportDTO.getFromDateStr()
							+ "' and ";
				}
				if (profitAndLossReportDTO.getTodate() != null) {
					Calendar calToDate = Calendar.getInstance();
					calToDate.setTime(profitAndLossReportDTO.getTodate());

					StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
					while (toDate.hasMoreTokens()) {
						profitAndLossReportDTO.setToDateStr(toDate.nextToken());
						break;
					}

					parameter = parameter + "' " + profitAndLossReportDTO.getToDateStr() + "' ";
				}

				query = query.replaceAll(":input", parameter);
				log.info(" Query == " + query);
				List<Map<String, Object>>  maps = jdbcTemplate.queryForList(query);
			//	sql = manager.createNativeQuery(query);

			//	obj = jdbcTemplate.queryForList(query);
				
				billReport = (Iterator<Map<String, Object>>) maps.iterator();
				
				if (billReport != null) {
					while (billReport.hasNext()) {
						profitAndLossReportDto = new ProfitAndLossReportDTO();
						Map<String, Object> ob = billReport.next();
						
						autoIncrement++;
						if (ob != null && ob.get("name")!=null) {
							profitAndLossReportDto.setItemName(ob.get("name").toString());
						}

						if (ob != null && ob.get("soldqnty")!=null) {
							profitAndLossReportDto.setSoldQnty(Double.valueOf(ob.get("soldqnty").toString()));
						}

						if (ob != null && ob.get("stock_tran_purchaseprice")!=null) {
							profitAndLossReportDto.setPurchasePrice(Double.parseDouble(ob.get("stock_tran_purchaseprice").toString()));
						}

						if (ob != null && ob.get("stock_tran_sellingprice")!=null) {
							profitAndLossReportDto.setSellingPrice(Double.parseDouble(ob.get("stock_tran_sellingprice").toString()));
						}

						if (ob != null && ob.get("totalpurchaseprice")!=null) {
							profitAndLossReportDto.setTotalPurchasePrice(Double.parseDouble(ob.get("totalpurchaseprice").toString()));
						}

						if (ob != null && ob.get("totalsellingprice")!=null) {
							profitAndLossReportDto.setTotalSellingPrice(Double.parseDouble(ob.get("totalsellingprice").toString()));
						}

						if (ob != null && ob.get("returnedqnty")!=null) {
							profitAndLossReportDto.setReturnedQnty(Double.valueOf(ob.get("returnedqnty").toString()));
						}

						if (ob != null && ob.get("expiredqnty")!=null) {
							profitAndLossReportDto.setExpiredQnty(Double.valueOf(ob.get("expiredqnty").toString()));
						}

						if (ob != null && ob.get("damagedqnty")!=null) {
							profitAndLossReportDto.setDamagedQnty(Double.valueOf(ob.get("damagedqnty").toString()));
						}

						if (ob != null && ob.get("stockaddqnty")!=null) {
							profitAndLossReportDto.setStockAddQnty(Double.valueOf(ob.get("stockaddqnty").toString()));
						}

						if (ob != null && ob.get("stockminusqnty")!=null) {
							profitAndLossReportDto.setStockMinusQnty(Double.valueOf(ob.get("stockminusqnty").toString()));
						}

						if (ob != null && ob.get("discount_value")!=null) {
							profitAndLossReportDto.setDiscount(Double.valueOf(ob.get("discount_value").toString()));
						} else {
							profitAndLossReportDto.setDiscount(0D);
						}
						if (profitAndLossReportDto.getTotalPurchasePrice() == null) {
							profitAndLossReportDto.setTotalPurchasePrice(0D);
						}
						if (profitAndLossReportDto.getTotalSellingPrice() == null) {
							profitAndLossReportDto.setTotalSellingPrice(0D);
						}
						if (ob != null && ob.get("date")!=null) {
							profitAndLossReportDto.setStocktrandate((Date)ob.get("date"));
						}
						profitAndLossReportDto.setTotalNetProfit(profitAndLossReportDto.getTotalSellingPrice() - profitAndLossReportDto.getTotalPurchasePrice()
								                                 - profitAndLossReportDto.getDiscount());
						profitAndLossReportDTOList.add(profitAndLossReportDto);
					
						if (!(profitAndLossReportDTOMap.containsKey(autoIncrement))) {
							profitAndLossReportDTOMap.put(autoIncrement, profitAndLossReportDto);
					      }
					}
					for (Long dto : profitAndLossReportDTOMap.keySet()) {
						Map<String, Object> map = new HashMap<>();
						ProfitAndLossReportDTO dailyDto = profitAndLossReportDTOMap.get(dto);
						map.put("Item Name", dailyDto.getItemName());
						map.put("Sold Qnty",dailyDto.getSoldQnty());
						map.put("Item Purchase Price",dailyDto.getPurchasePrice());
						map.put("Item Selling Price",dailyDto.getSellingPrice());
						map.put("Total Purchase Price",dailyDto.getTotalPurchasePrice());
						map.put("Total Selling Price",dailyDto.getTotalSellingPrice());
						map.put("Net Profit",dailyDto.getTotalNetProfit());
						listMapForExcel.add(map);
					}
				}
			}
			log.info(" ProfitAndLossReportService Status >> "+ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(profitAndLossReportDTOList);
			response.setListOfData(listMapForExcel);
		} catch (Exception e) {
			log.error("Exception Occured On ProfitAndLossReportService" + e);
			log.error("Exception Occured On ProfitAndLossReportService" + e.getStackTrace());
			log.error("Exception Occured On ProfitAndLossReportService" + e.getMessage());
		}
		return response;

	}
}
