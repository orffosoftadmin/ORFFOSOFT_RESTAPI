package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import co.orffosoft.dto.StockAdjustmentDTO;
import co.orffosoft.entity.ItemPrice;
import co.orffosoft.entity.ProductVarietyMaster;
import co.orffosoft.entity.StockAdjustment;
import co.orffosoft.entity.StockTransaction;
import co.orffosoft.entity.StockTransactionType;
import co.orffosoft.repository.ItemPriceRepository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.StockAdjustmentRepository;
import co.orffosoft.repository.StockTransactionRepository;
import co.orffosoft.repository.StockTransactionTypeRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class StockAdjustmentService {

	@Autowired
	LoginService loginService;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	StockTransactionTypeRepository StockTransactionTypeRepository;

	@Autowired
	StockAdjustmentRepository stockAdjustmentRepository;

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	StockTransactionTypeRepository stockTransactionTypeRepository;

	@Autowired
	ItemPriceRepository itemPriceRepository;

	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@PersistenceContext
	EntityManager manager;

	@Transactional
	public BaseDTO generateReport(ProductVarietyMaster items) {
		log.info(" >> StockAdjustmentService >>  generateReport method >> ");
		log.info(items.getName());
		BaseDTO response = new BaseDTO();
		List<StockAdjustmentDTO> stockAdjustmentDTOList = new ArrayList<>();
		StockAdjustmentDTO stockAdjustmentDTO = new StockAdjustmentDTO();
		String query = null;
		Query sql = null;
		List<Object[]> obj = null;
		Iterator<Object[]> billReport = null;

		try {
			
			// 1st Query Used to find the sum of closing stock , group by ItemName (It should always contain 1 Record)
			query = " select pvm.id, pvm.name , sum(item.item_closing_stock) as ItemQnty ,pvm.cgst_percentage,pvm.sgst_percentage "
					+ " from product_variety_master pvm  inner join itemPrice item on  pvm.id= item.itemprice_sku_fk  where pvm.name = '"
					+ items.getName() + "' and pvm.entityid =" + loginService.getCurrentUser().getEntityId()
					+ " group by pvm.id ";
			log.info(" StockAdjustmentService >> find the sum of closing Stock = "+query);
			sql = manager.createNativeQuery(query);
			obj = sql.getResultList();
			billReport = obj.iterator();
			while (billReport.hasNext()) {
				Object[] ob = billReport.next();
				if (ob[0] != null) {
					stockAdjustmentDTO.setItemId(Long.parseLong(ob[0].toString()));
				}
				if (ob[1] != null) {
					stockAdjustmentDTO.setName(ob[1].toString());
				}
				if (ob[2] != null) {
					stockAdjustmentDTO.setItemQty(Double.valueOf(ob[2].toString()));
				}
				if (ob[3] != null) {
					stockAdjustmentDTO.setCgstamount(Double.valueOf(ob[3].toString()));
				}
				if (ob[4] != null) {
					stockAdjustmentDTO.setSgstamount(Double.valueOf(ob[4].toString()));
				}
				
				// 2nd query Used to find the multiple sellingPrice with rateWise Quantity
				query = " select item.itemprice_pk , item.item_closing_stock as ItemQnty, "
						+ " item.itemprice_new_price , st.stock_tran_purchaseprice " + " from product_variety_master pvm "
						+ " inner join itemPrice item on  pvm.id= item.itemprice_sku_fk "
						+ " inner join stock_transaction st on st.stock_tran_item_price_fk=item.itemprice_pk " + " where pvm.name = '"
						+ items.getName() + "' and pvm.entityid = " + loginService.getCurrentUser().getEntityId()
						+ " group by item.item_closing_stock, item.itemprice_pk ,st.stock_tran_purchaseprice";
				log.info(" StockAdjustmentService >> find the sum of closing Stock = "+query);
				sql = manager.createNativeQuery(query);
				obj = sql.getResultList();
				Iterator<Object[]> priceWithQnty = obj.iterator();
				stockAdjustmentDTO.setSellingPrices(new HashMap<>());
				stockAdjustmentDTO.setRateWiseQnty1(new HashMap<>());
				while (priceWithQnty.hasNext()) {
					Object[] priceWithQntyObj = priceWithQnty.next();

					if (priceWithQntyObj[0] != null && priceWithQntyObj[2] != null) {
						// put (sellingPrice, ItemPricePk)
						stockAdjustmentDTO.getSellingPrices().put(Double.parseDouble(priceWithQntyObj[2].toString()),
								Long.parseLong(priceWithQntyObj[0].toString()));
					}
					if (priceWithQntyObj[1] != null && priceWithQntyObj[2] != null) {
						// put (sellingPrice, RateWiseQnty )
						 stockAdjustmentDTO.getRateWiseQnty1().put(Double.parseDouble(priceWithQntyObj[2].toString()),
								 Double.parseDouble(priceWithQntyObj[1].toString()));
					}
					
				}
				stockAdjustmentDTOList.add(stockAdjustmentDTO);
			}
			
			log.info(" === StockAdjustmentService Method Execuation Completed === ");
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(stockAdjustmentDTOList);
			log.info("<<<  === Success Response === >>>"+ response.getStatusCode());
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DailyTransactionReportService >> generateReport method >> ", e);
		}
		return response;
	}

	@Transactional
	public BaseDTO generatStockAdjustment(StockAdjustmentDTO items) {
		log.info(" >> StockAdjustmentService >>  generatStockAdjustment method >> ");
		BaseDTO respons = new BaseDTO();
		StockAdjustment stockadjustment = new StockAdjustment();
		StockTransaction stockTransaction = new StockTransaction();
		StockTransactionType st = new StockTransactionType();
		try {
			Long itemPricePk = items.getSellingPrices().get(items.getUnitRate());
			
			// Item Price Saving
			ItemPrice itemPrice = itemPriceRepository.findOne(itemPricePk);
			
			if (itemPrice == null) {
				throw new RestException("Item Price Pk Not Found"+itemPrice);
			}
			
		if(items.isChngeprice()  && itemPrice!=null) {
			ItemPrice exitemprice = itemPriceRepository.sellingprice_purchedprice(items.getItemId(), items.getSellingprice(), items.getPurchesprice(),loginService.getCurrentUser().getEntityId());
			if(exitemprice!=null) {
				itemPrice.setItemprice_purchase_price(items.getPurchesprice());
				itemPrice.setItemprice_selling_price(items.getSellingprice());
				
				itemPriceRepository.save(itemPrice);
			}else {
				
				throw new RestException("Same item price and selling price is available");
			}
			
		}

			if (items != null) {
				stockadjustment.setName(items.getName());
				stockadjustment.setItemId(items.getItemId());
				stockadjustment.setAction(items.getAction());
				stockadjustment.setActionQty(items.getActionQty());
				stockadjustment.setStoreCode(items.getStoreCode());
				stockadjustment.setStoreName(items.getStoreName());
				stockadjustment.setUserId(items.getUserId());
				stockadjustment.setCreatedate(new Date());
				stockadjustment.setStockdate(new Date());
				stockadjustment.setItemrate(items.getUnitRate());
				stockadjustment.setPurchasePrice(itemPrice.getItemprice_purchase_price());
			}
			stockAdjustmentRepository.save(stockadjustment);

			if (items != null) {
				st = StockTransactionTypeRepository.getGRNID(items.getAction());
				
				Double finalClosingStock=0.0D;
				if (items.getUomName() != null && !(items.getUomName().isEmpty())) {
					Double closingStock = stockTransactionRepository.findClosingQnty(itemPrice.getItemprice_pk());
					if (items.getUomName().equalsIgnoreCase(Constants.FOOT_INCH)) {
					    finalClosingStock = AppUtil.getCalculateClosingStockForFeetAndInch(items.getFeet(), items.getInches(), closingStock);
						itemPrice.setItem_closing_stock(finalClosingStock);
					} else if (items.getAction().equalsIgnoreCase(StockTransactionTypeIFC.STOCK_ADJUSTMENT_ADD)){
						itemPrice.setItem_closing_stock(itemPrice.getItem_closing_stock() + items.getActionQty());
					} else {
						itemPrice.setItem_closing_stock(AppUtil.getClosingStock(itemPrice.getItem_closing_stock(), items.getActionQty()));
					}
					itemPriceRepository.save(itemPrice);
				}

				stockTransaction.setStock_tran_item_price_fk(itemPrice.getItemprice_pk());
				stockTransaction.setStoreEntity(itemPrice.getStoreEntity());

				stockTransaction.setCreated_by(items.getUserId());
				stockTransaction.setProductVarietyMaster(productVarietyMasterRepository.findOne(items.getItemId()));

				if (items.getAction().equals("STOCK_ADJUSTMENT_ADD")) {
					stockTransaction.setStock_tran_issued_qnty(0.0);
					stockTransaction.setStock_tran_received_qnty(Double.parseDouble(items.getActionQty().toString()));
				} else {
					stockTransaction.setStock_tran_issued_qnty(items.getActionQty());
					stockTransaction.setStock_tran_received_qnty(0d);
				}
				stockTransaction.setStock_tran_sellingprice(itemPrice.getItemprice_new_price());
				stockTransaction.setStock_tran_purchaseprice(itemPrice.getItemprice_purchase_price());
				stockTransaction.setStock_tran_type_fk(st.getStock_tran_type_pk());
				stockTransaction.setStock_tran_date(new Date());
				stockTransaction.setCreated_date(new Date());
				stockTransaction.setSgstvalue(items.getSgstamount());
				stockTransaction.setCgstvalue(items.getCgstamount());
			}
			stockTransactionRepository.save(stockTransaction);
			respons.setStatusCode(0);

		} catch (Exception e) {
			respons.setStatusCode(1);
			log.error(" >>  Exception Occured In StockAdjustment >> generatStockAdjustment method >> ", e);
		}

		return respons;
	}

}
