package co.orffosoft.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.DailyStockTransactionReportDTO;
import co.orffosoft.repository.ItemPriceRepository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.StockTransactionRepository;
import co.orffosoft.repository.StockTransactionTypeRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DailyStockTransactionReportService {

	@Autowired
	LoginService loginService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@PersistenceContext
	EntityManager manager;

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	StockTransactionTypeRepository stockTransactionTypeRepository;

	@Autowired
	ItemPriceRepository itemPriceRepository;

	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@Transactional
	public BaseDTO generateReport(DailyStockTransactionReportDTO items) {

		log.info(" >> DailyStockTransactionReportService >>  generateReport method >> ");
		BaseDTO response = new BaseDTO();
		List<DailyStockTransactionReportDTO> dailyStokTransactionReportDTOList = new ArrayList<>();
		Map<Long, DailyStockTransactionReportDTO> dailyStokTransactionReportDTOMap = new HashMap();
		DailyStockTransactionReportDTO dailyStockTransactionReportDTO = new DailyStockTransactionReportDTO();
		try {

						String input = "";
			if (items.getFromDate() != null && items.getToDate() != null) {
				input = input + " and st.stock_tran_date between '" + items.getFromDate() + "' and  '"
						+ items.getToDate() + "' ";
			}
			if (items.getProductVarietyMaster().getName() != null && !(items.getProductVarietyMaster().getName().isEmpty())) {
				input = input + " and st.stock_tran_sku_id_fk= " +items.getProductVarietyMaster().getId();
			}

			String stockTransaction = " select pvm.id , pvm.name , st.stock_tran_date, sty.stock_tran_type_code ,  item.itemprice_new_price, item.itemprice_pk,   "
					+ "					case when sty.stock_tran_type_code = 'SALE'  then sum(st.stock_tran_issued_qnty)    "
					+ "					     when sty.stock_tran_type_code = 'STKUPD/GRN' then sum(st.stock_tran_received_qnty)  "
					+ "					     when sty.stock_tran_type_code = 'GRN' then sum(st.stock_tran_received_qnty) "
					+ "						 when sty.stock_tran_type_code = 'SALE/RETURN' then sum(st.stock_tran_received_qnty)    "
					+ "						 when sty.stock_tran_type_code = 'DAMAGED' then sum(st.stock_tran_issued_qnty)   "
					+ "						 when sty.stock_tran_type_code = 'STOCK-ADJUSTMENT_ADD' then  sum(st.stock_tran_received_qnty)    "
					+ "						 when sty.stock_tran_type_code = 'STOCK-ADJUSTMENT_MINUS' then sum(st.stock_tran_issued_qnty)    "
					+ "						 when sty.stock_tran_type_code = 'EXPIRED' then sum(st.stock_tran_issued_qnty)    "
					+ "						 end as stockQnty "
					+ "					from stock_transaction st inner join stock_transaction_type sty on st.stock_tran_type_fk = sty.stock_tran_type_pk   "
					+ "					inner join product_variety_master pvm on pvm.id = st.stock_tran_sku_id_fk  "
					+ "                 inner join itemprice item on item.itemprice_pk = st.stock_tran_item_price_fk   "
					+ "					where st.stock_tran_store_entity_fk =  "
					+ loginService.getCurrentUser().getEntityId() + " input: "
					+ "					group by pvm.id , pvm.name , st.stock_tran_date, sty.stock_tran_type_code,  item.itemprice_new_price, item.itemprice_pk  "
					+ "					order by pvm.id, st.stock_tran_date,item.itemprice_pk asc";

			log.info("stockTransaction before Replace Query === >>> " + stockTransaction);
			stockTransaction = stockTransaction.replaceAll("input:", input);
			log.info("stockTransaction After Rreplace Query === >>> " + stockTransaction);
			Query querry = manager.createNativeQuery(stockTransaction);

			List<Object[]> stockTransactionReport = querry.getResultList();
			Iterator<Object[]> stockReport = stockTransactionReport.iterator();
			String temp = "";
			Long autoIncrement = 0L;
			while (stockReport.hasNext()) {
				Object[] ob1 = stockReport.next();

				String currentCheck = Long.parseLong(ob1[0].toString()) + "_" + (Date) ob1[2] + "_"
						+ Long.parseLong(ob1[5].toString());
				if (!temp.isEmpty() && temp.equalsIgnoreCase(currentCheck)) {

				} else {
					++autoIncrement;
					dailyStockTransactionReportDTO = new DailyStockTransactionReportDTO();
					String openingBal = " select sum( st.stock_tran_received_qnty - st.stock_tran_issued_qnty ) as openingBal  "
							+ " from stock_transaction st where " + " st.stock_tran_date < '" + items.getFromDate()
							+ "' and  st.stock_tran_item_price_fk= " + ob1[5].toString();
					Query query = manager.createNativeQuery(openingBal);
					BigDecimal openingQntyBig = (BigDecimal) query.getSingleResult();
					Double openingQnty = 0d;
					if (openingQntyBig != null) {
						openingQnty = openingQntyBig.doubleValue();
					}
					dailyStockTransactionReportDTO.setOpeningStockQty(openingQnty);
					dailyStockTransactionReportDTO.setSaleQnty(0.0);
					dailyStockTransactionReportDTO.setGrnReceivedQnty(0d);
					dailyStockTransactionReportDTO.setItemReceivedQnty(0d);
					dailyStockTransactionReportDTO.setSaleReturnQnty(0d);
					dailyStockTransactionReportDTO.setDamageQnty(0d);
					dailyStockTransactionReportDTO.setExpiredQnty(0d);
					dailyStockTransactionReportDTO.setTotalClosingStockQnty(0D);
					dailyStockTransactionReportDTO.setItemIssuedQnty(0d);
					dailyStockTransactionReportDTO.setStockAdjuestMentAddQnty(0d);
					dailyStockTransactionReportDTO.setStockadjusmtneMinusQnty(0d);
				}

				dailyStockTransactionReportDTO.setItemId(Long.parseLong(ob1[0].toString()));
				dailyStockTransactionReportDTO.setItemName(ob1[1].toString());
				dailyStockTransactionReportDTO.setStockTransactionDate((Date) ob1[2]);
				dailyStockTransactionReportDTO.setTransactionType(ob1[3].toString());
				dailyStockTransactionReportDTO.setItemPrice(Double.parseDouble(ob1[4].toString()));
				dailyStockTransactionReportDTO.setItemPricePK(Long.parseLong(ob1[5].toString()));
				//dailyStockTransactionReportDTO.setItemPricePk();
				// dailyStockTransactionReportDTO.setOpeningStockQty(Long.parseLong(ob1[7].toString()));
				if (ob1[3].toString().equals("GRN".toString()) || ob1[3].toString().equals("STKUPD/GRN".toString())) {

					dailyStockTransactionReportDTO.setGrnReceivedQnty(Double.valueOf(ob1[6].toString()));
					dailyStockTransactionReportDTO.setItemReceivedQnty(Double.valueOf(ob1[6].toString()));

				} else if (ob1[3].toString().equals("SALE".toString())) {
					if (ob1[6] != null) {
						dailyStockTransactionReportDTO.setSaleQnty(Double.valueOf(ob1[6].toString()));
						dailyStockTransactionReportDTO.setItemIssuedQnty(Double.valueOf(ob1[6].toString()));
					}

				} else if (ob1[3].toString().equals("SALE/RETURN".toString())) {
					if (ob1[6] != null) {
						dailyStockTransactionReportDTO.setSaleReturnQnty(Double.valueOf(ob1[6].toString()));
						dailyStockTransactionReportDTO.setItemReceivedQnty(Double.valueOf(ob1[6].toString()));
					}

				} else if (ob1[3].toString().equals("DAMAGED".toString())) {
					if (ob1[6] != null) {
						dailyStockTransactionReportDTO.setDamageQnty(Double.valueOf(ob1[6].toString()));
						dailyStockTransactionReportDTO.setItemIssuedQnty(Double.valueOf(ob1[6].toString()));
					}

				} else if (ob1[3].toString().equals("STOCK-ADJUSTMENT_ADD".toString())) {
					if (ob1[6] != null) {
						dailyStockTransactionReportDTO.setStockAdjuestMentAddQnty(Double.valueOf(ob1[6].toString()));
						dailyStockTransactionReportDTO.setItemReceivedQnty(Double.valueOf(ob1[6].toString()));
					}

				} else if (ob1[3].toString().equals("STOCK-ADJUSTMENT_MINUS".toString())) {
					if (ob1[6] != null) {
						dailyStockTransactionReportDTO.setStockadjusmtneMinusQnty(Double.valueOf(ob1[6].toString()));
						dailyStockTransactionReportDTO.setItemIssuedQnty(Double.valueOf(ob1[6].toString()));
					}

				} else if (ob1[3].toString().equals("EXPIRED".toString())) {
					if (ob1[6] != null) {
						dailyStockTransactionReportDTO.setExpiredQnty(Double.valueOf(ob1[6].toString()));
						dailyStockTransactionReportDTO.setItemIssuedQnty(Double.valueOf(ob1[6].toString()));
					}

				}

				temp = Long.parseLong(ob1[0].toString()) + "_" + (Date) ob1[2] + "_"
						+ Long.parseLong(ob1[5].toString());
				Double closingBal = 0d;
				if (dailyStockTransactionReportDTO.getOpeningStockQty() == 0) {

					closingBal = (dailyStockTransactionReportDTO.getGrnReceivedQnty()
							- dailyStockTransactionReportDTO.getSaleQnty()
							+ dailyStockTransactionReportDTO.getSaleReturnQnty()
							- dailyStockTransactionReportDTO.getDamageQnty()
							- dailyStockTransactionReportDTO.getExpiredQnty()
							+ dailyStockTransactionReportDTO.getStockAdjuestMentAddQnty()
							- dailyStockTransactionReportDTO.getStockadjusmtneMinusQnty()
							- dailyStockTransactionReportDTO.getOpeningStockQty());
				} else {
					closingBal = (dailyStockTransactionReportDTO.getOpeningStockQty()
							- dailyStockTransactionReportDTO.getGrnReceivedQnty()
							- dailyStockTransactionReportDTO.getSaleQnty()
							+ dailyStockTransactionReportDTO.getSaleReturnQnty()
							- dailyStockTransactionReportDTO.getDamageQnty()
							- dailyStockTransactionReportDTO.getExpiredQnty()
							+ dailyStockTransactionReportDTO.getStockAdjuestMentAddQnty()
							- dailyStockTransactionReportDTO.getStockadjusmtneMinusQnty());

				}

				dailyStockTransactionReportDTO.setTotalClosingStockQnty(closingBal);

				// dailyStokTransactionReportDTOList.add(dailyStockTransactionReportDTO);
				if (!(dailyStokTransactionReportDTOMap.containsKey(autoIncrement))) {

					dailyStokTransactionReportDTOMap.put(autoIncrement, dailyStockTransactionReportDTO);
				} else {
					// Long closingBal = (dailyStockTransactionReportDTO.getGrnReceivedQnty() -
					// dailyStockTransactionReportDTO.getSaleQnty() +
					// dailyStockTransactionReportDTO.getSaleReturnQnty() -
					// dailyStockTransactionReportDTO.getDamageQnty() +
					// dailyStockTransactionReportDTO.getStockAdjuestMentAddQnty() -
					// dailyStockTransactionReportDTO.getStockadjusmtneMinusQnty());
					// dailyStockTransactionReportDTO.setTotalClosingStockQnty(closingBal);

				}
			}

			List<Map<String, Object>> listMapForExcel = new ArrayList<>();
			int indexCount=0;
			String previousItemPKTemp = "";
			for (Long dto : dailyStokTransactionReportDTOMap.keySet()) {
				Map<String, Object> map = new HashMap<>();
				DailyStockTransactionReportDTO dailyDto = dailyStokTransactionReportDTOMap.get(dto);
				map.put("ItemName", dailyDto.getItemName());
				map.put("StockTransactionDate", dailyDto.getStockTransactionDate());
				map.put("ItemPrice", dailyDto.getItemPrice());
				map.put("OpeningQnty", dailyDto.getOpeningStockQty());
				map.put("GrnQnty", dailyDto.getGrnReceivedQnty());
				map.put("SaleQnty", dailyDto.getSaleQnty());
				map.put("ReturnQnty", dailyDto.getSaleReturnQnty());
				map.put("DamageQty", dailyDto.getDamageQnty());
				map.put("Stockadjustment(ADD)", dailyDto.getStockAdjuestMentAddQnty());
				map.put("Stockadjustment(Minus)", dailyDto.getStockadjusmtneMinusQnty());
				map.put("CloseQnty", dailyDto.getTotalClosingStockQnty());
				if (!(previousItemPKTemp.isEmpty())) {
					String previousItemPK = dailyDto.getItemPricePK().toString();
					if (previousItemPK.equals(previousItemPKTemp)) {
						Double previousItemPricePkClosingBal = dailyStokTransactionReportDTOList.get(indexCount - 1).getTotalClosingStockQnty();
					 	Double closingStock = previousItemPricePkClosingBal + dailyDto.getTotalClosingStockQnty();
					 	dailyDto.setTotalClosingStockQnty(closingStock);
					}
				} 
				previousItemPKTemp = dailyDto.getItemPricePK().toString();
				listMapForExcel.add(map);
				dailyStokTransactionReportDTOList.add(dailyDto);
				indexCount++;
			}

			response.setResponseContents(dailyStokTransactionReportDTOList);
			response.setListOfData(listMapForExcel);
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			log.info("<<<  === Success Response === >>>");
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> generateReport method >> ", e);
		}
		return response;
	}

}
