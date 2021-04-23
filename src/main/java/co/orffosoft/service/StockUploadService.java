package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.orffosoft.core.util.AppUtil;
import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.core.util.StockTransactionTypeIFC;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.StockUploadDTO;
import co.orffosoft.entity.EntityMaster;
import co.orffosoft.entity.GoodsReceiptNote_D;
import co.orffosoft.entity.GoodsReceiptNote_H;
import co.orffosoft.entity.Goods_Change_History;
import co.orffosoft.entity.ItemPrice;
import co.orffosoft.entity.ProductVarietyMaster;
import co.orffosoft.entity.StockTransaction;
import co.orffosoft.entity.StockTransactionType;
import co.orffosoft.entity.SupplierMaster;
import co.orffosoft.enums.SequenceName;
import co.orffosoft.enums.StockTransferStatus;
import co.orffosoft.enums.StockTransferType;
import co.orffosoft.repository.EntityMasterRepository;
import co.orffosoft.repository.GoodsReceiptNote_D_Repository;
import co.orffosoft.repository.GoodsReceiptNote_H_Repository;
import co.orffosoft.repository.Goods_Change_History_Repository;
import co.orffosoft.repository.ItemPriceRepository;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import co.orffosoft.repository.SequenceConfigRepository;
import co.orffosoft.repository.StockTransactionRepository;
import co.orffosoft.repository.StockTransactionTypeRepository;
import co.orffosoft.repository.SupplierMasterRepository;
import co.orffosoft.repository.UserMasterRepository;
import co.orffosoft.rest.util.ResponseWrapper;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class StockUploadService {
	
	@Autowired
	Goods_Change_History_Repository goods_Change_History_Repository;
	
	@Autowired
	SupplierMasterRepository supplierMasterRepository;

	@Autowired
	SequenceConfigRepository sequenceConfigRepository;

	@Autowired
	LoginService loginService;

	@Autowired
	EntityManager entityManager;

	@Autowired
	SequenceConfigService sequenceConfigService;

	@Autowired
	ResponseWrapper responseWrapper;

	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@Autowired
	EntityMasterRepository entityMasterRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	GoodsReceiptNote_D_Repository goodsReceiptNote_D_Repository;

	@Autowired
	GoodsReceiptNote_H_Repository goodsReceiptNote_H_Repository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	StockTransactionRepository stockTransactionRepository;

	@Autowired
	StockTransactionTypeRepository stockTransactionTypeRepository;

	@Autowired
	ItemPriceRepository itemPriceRepository;

	@PersistenceContext
	EntityManager manager;
	
	public BaseDTO getAllProductVariety() {

		log.info("<-Inside SERVICE-Starts AllProductVariety-->");
		BaseDTO baseDTO = new BaseDTO();
		try {
			List<ProductVarietyMaster> productVarietyList = productVarietyMasterRepository
					.getProductVarietyItemInward();
			baseDTO.setResponseContent(productVarietyList);
			baseDTO.setTotalRecords(productVarietyList.size());
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			log.info("<-AllProductVariety GetAll Data Success-->");
		} catch (Exception exception) {
			log.error("exception Occured AllProductVariety : ", exception);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
	}

	@Transactional
	public BaseDTO saveStockItem(StockUploadDTO stockUpload) {
		log.info("<-Inside SERVICE-Starts saveStockItem-->");
		BaseDTO baseDTO = new BaseDTO();
		GoodsReceiptNote_H goodsReceiptNote_H = new GoodsReceiptNote_H();
		GoodsReceiptNote_H ExgoodsReceiptNote_H = new GoodsReceiptNote_H();
		try {

			if (stockUpload.getGoodsReceiptNote_H().getGrn_h_id() == null) {
				// Save to Header Table
				if (stockUpload.getGoodsReceiptNote_H().getGrn_h_id() == null) {
					goodsReceiptNote_H = saveGoodsReceiptNote_H(stockUpload);
				}

				if (goodsReceiptNote_H.getGrn_h_id() != null) {
					// Save to Header Table
					saveGoodsReceiptNote_D(stockUpload, goodsReceiptNote_H);
				}

				baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			} else {
				ExgoodsReceiptNote_H = goodsReceiptNote_H_Repository
						.getdetail(stockUpload.getGoodsReceiptNote_H().getGrn_h_id());
				if (ExgoodsReceiptNote_H != null) {
					ExgoodsReceiptNote_H.setSupplierMaster(ExgoodsReceiptNote_H.getSupplierMaster());
					
					ExgoodsReceiptNote_H.setStoreEntity(ExgoodsReceiptNote_H.getStoreEntity());

					ExgoodsReceiptNote_H.setGrn_H_Number(ExgoodsReceiptNote_H.getGrn_H_Number());
					ExgoodsReceiptNote_H.setModified_date(new Date());
					ExgoodsReceiptNote_H
							.setGrn_H_Total_Number(stockUpload.getGoodsReceiptNote_H().getGrn_H_Total_Number());
					ExgoodsReceiptNote_H
							.setGrn_H_Gross_Amount(stockUpload.getGoodsReceiptNote_H().getGrn_H_Gross_Amount());
					ExgoodsReceiptNote_H.setGrn_H_Discount_Percentage(
							stockUpload.getGoodsReceiptNote_H().getGrn_H_Discount_Percentage());
					ExgoodsReceiptNote_H
							.setGrn_H_Discount_Amount(stockUpload.getGoodsReceiptNote_H().getGrn_H_Discount_Amount());
					ExgoodsReceiptNote_H.setGrn_H_Status(ExgoodsReceiptNote_H.getGrn_H_Status());
					ExgoodsReceiptNote_H.setUserMaster(ExgoodsReceiptNote_H.getUserMaster());
					ExgoodsReceiptNote_H.setCreated_date(ExgoodsReceiptNote_H.getCreated_date());
					ExgoodsReceiptNote_H = goodsReceiptNote_H_Repository.save(ExgoodsReceiptNote_H);

					if (ExgoodsReceiptNote_H.getGrn_h_id() != null) {
						// Save to Header Table
						saveGoodsReceiptNote_D(stockUpload, ExgoodsReceiptNote_H);
					}

				}
			}
			
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
		} catch (Exception exception) {
			log.error("exception Occured saveStockItem : ", exception);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
	}

	@Transactional
	private void saveGoodsReceiptNote_D(StockUploadDTO stockUpload, GoodsReceiptNote_H goodsReceiptNote_h) {
		StockTransaction stockTransaction = new StockTransaction();
		ItemPrice itemPrice = new ItemPrice();
		List<GoodsReceiptNote_D> exGoodsReceiptNote_D = new ArrayList();

		exGoodsReceiptNote_D = goodsReceiptNote_D_Repository.getdata(goodsReceiptNote_h.getGrn_h_id());

		if (exGoodsReceiptNote_D == null || exGoodsReceiptNote_D.size()==0) {

			if (stockUpload.getGoodsReceiptNote_DList() != null) {
				

				StockTransactionType st = stockTransactionTypeRepository.getGRNID(StockTransactionTypeIFC.GRN);

				for (GoodsReceiptNote_D grnd : stockUpload.getGoodsReceiptNote_DList()) {
					stockTransaction = new StockTransaction();
					grnd.setGoodsReceiptNote_H(goodsReceiptNote_h);
					grnd.setProductVarietyMaster(
							productVarietyMasterRepository.findOne(grnd.getProductVarietyMaster().getId()));
					grnd.setGrn_D_Date(new Date());
					grnd.setGrn_D_Reject_Qnty(grnd.getGrn_D_Item_Qnty() - grnd.getGrn_D_Accepted_Qnty());
					grnd.setCreatedBy(loginService.getCurrentUser());
					grnd.setCreatedDate(new Date());
					grnd.setExpiryDate(grnd.getExpiryDate());
					

					// For Item Price update simultaneously using itemPrice Table
					itemPrice = new ItemPrice();
					itemPrice.setItemprice_grn_h_fk(goodsReceiptNote_h.getGrn_h_id());
					itemPrice.setItemprice_sku_fk(grnd.getProductVarietyMaster().getId());
					itemPrice.setItemprice_purchase_price(grnd.getGrn_D_Purchase_Amount());
					itemPrice.setItemprice_selling_price(grnd.getGrn_D_Selling_Amount());
					itemPrice.setItemprice_old_price(grnd.getGrn_D_Selling_Amount());
					itemPrice.setItemprice_new_price(grnd.getGrn_D_Selling_Amount());
					itemPrice.setItemprice_type("GRN Price");
					itemPrice.setCreated_by(loginService.getCurrentUser().getId());
					itemPrice.setCreated_date(new Date());

					if (stockUpload.getGrn_store_entity_fk() != 0) {

						itemPrice.setStoreEntity(entityMasterRepository.findOne(stockUpload.getGrn_store_entity_fk()));
					}
					if (grnd.getItem_barcode_number() == null) 
						grnd.setItem_barcode_number("");
					 else 
						 grnd.setItem_barcode_number(grnd.getItem_barcode_number()+",");
					
					ItemPrice data = itemPriceRepository.getItemPriceBasedOnFilter(grnd.getProductVarietyMaster().getId(),grnd.getGrn_D_Purchase_Amount(),
							grnd.getGrn_D_Selling_Amount(), stockUpload.getGrn_store_entity_fk());
					if (data == null) {
						itemPrice.setItem_barcode_number(grnd.getItem_barcode_number().trim());
						itemPrice.setItem_closing_stock(grnd.getGrn_D_Accepted_Qnty());
						itemPriceRepository.save(itemPrice);
						stockTransaction.setStock_tran_item_price_fk(itemPrice.getItemprice_pk());
					} else  {
						String barCode = data.getItem_barcode_number();
						if (barCode == null) {
							barCode="";
							barCode = barCode + grnd.getItem_barcode_number();
						} else {
							barCode = barCode + grnd.getItem_barcode_number()+",";
						}
						data.setItem_barcode_number(barCode.trim());
						data.setItem_closing_stock(data.getItem_closing_stock() + grnd.getGrn_D_Accepted_Qnty());
						itemPriceRepository.save(data);
						stockTransaction.setStock_tran_item_price_fk(data.getItemprice_pk());
					} 

					// For Stock Update In Stock Transaction Table

					if (stockUpload.getGrn_store_entity_fk() != 0) {

						stockTransaction
								.setStoreEntity(entityMasterRepository.findOne(stockUpload.getGrn_store_entity_fk()));
					}
					stockTransaction.setCgstvalue(grnd.getGrn_D_Cgst_Amount());
					stockTransaction.setSgstvalue(grnd.getGrn_D_Sgst_Amount());
					stockTransaction.setStock_tran_grn_h_id_fk(goodsReceiptNote_h.getGrn_h_id());
					stockTransaction.setStock_tran_supplier_id_fk(
							stockUpload.getGoodsReceiptNote_H().getSupplierMaster().getId());
					stockTransaction.setProductVarietyMaster(grnd.getProductVarietyMaster());
					stockTransaction.setStock_tran_date(new Date());
					stockTransaction.setStock_tran_type_fk(st.getStock_tran_type_pk());
					StockTransaction stockTran = stockTransactionRepository
							.getSkuId(grnd.getProductVarietyMaster().getId());

					if (stockTran == null) {
						stockTransaction.setStock_tran_opening_bal(grnd.getGrn_D_Accepted_Qnty());
						stockTransaction.setStock_tran_received_qnty(grnd.getGrn_D_Accepted_Qnty());
						stockTransaction.setStock_tran_closing_bal(grnd.getGrn_D_Accepted_Qnty());
					} else {
						stockTransaction.setStock_tran_received_qnty(grnd.getGrn_D_Accepted_Qnty());
						if (stockTran.getStock_tran_closing_bal() != null) {
							stockTransaction.setStock_tran_closing_bal(
									stockTran.getStock_tran_closing_bal() + grnd.getGrn_D_Accepted_Qnty());
						}

					}
					stockTransaction.setStock_tran_issued_qnty(0.0);
					if (grnd.getGrn_D_Selling_Amount() != null) {
						stockTransaction.setStock_tran_sellingprice(grnd.getGrn_D_Selling_Amount());
					}
					if (grnd.getGrn_D_Purchase_Amount() != null) {
						stockTransaction.setStock_tran_purchaseprice(grnd.getGrn_D_Purchase_Amount());
					}
					stockTransaction.setCreated_by(loginService.getCurrentUser().getId());
					stockTransaction.setCreated_date(new Date());
					stockTransaction.setExpiryDate(grnd.getExpiryDate());
					stockTransaction.setItem_barcode_number(grnd.getItem_barcode_number());

					stockTransactionRepository.save(stockTransaction);
					
					grnd.setStoreEntity(entityMasterRepository.findOne(stockUpload.getGrn_store_entity_fk()));

				}
				goodsReceiptNote_D_Repository.save(stockUpload.getGoodsReceiptNote_DList());
			}

		} else {
			
			for (GoodsReceiptNote_D grnd : stockUpload.getGoodsReceiptNote_DList()) {
				Double purchaseAmt = 0.0;
				Double sellingAmt = 0.0;
				if (grnd.getGrn_d_id() != null) {
					grnd.setModifiedBy(loginService.getCurrentUser());
					grnd.setModifiedDate(new Date());
						purchaseAmt = grnd.getOldPurchaseAmount();
						sellingAmt = grnd.getOldSellingAmount();	
				} else {
					grnd.setGoodsReceiptNote_H(goodsReceiptNote_h);
					grnd.setStoreEntity(goodsReceiptNote_h.getStoreEntity());
					grnd.setProductVarietyMaster(productVarietyMasterRepository.findOne(grnd.getProductVarietyMaster().getId()));
					grnd.setGrn_D_Reject_Qnty(grnd.getGrn_D_Accepted_Qnty());
					grnd.setVersion(1L);
					grnd.setCreatedBy(loginService.getCurrentUser());
					grnd.setCreatedDate(new Date());
					
				}
				goodsReceiptNote_D_Repository.save(grnd);

				// For Item Price update simultaneously using itemPrice Table
				ItemPrice exItemPrice = itemPriceRepository.getItemPrice(goodsReceiptNote_h.getGrn_h_id(),
						grnd.getProductVarietyMaster().getId(), sellingAmt,
						purchaseAmt);
				if (exItemPrice == null) {
					exItemPrice = new ItemPrice();
				}

				exItemPrice.setItemprice_grn_h_fk(goodsReceiptNote_h.getGrn_h_id());
				exItemPrice.setItemprice_sku_fk(grnd.getProductVarietyMaster().getId());
				exItemPrice.setItemprice_purchase_price(grnd.getGrn_D_Purchase_Amount());
				exItemPrice.setItemprice_selling_price(grnd.getGrn_D_Selling_Amount());
				exItemPrice.setItemprice_old_price(grnd.getGrn_D_Selling_Amount());
				exItemPrice.setItemprice_new_price(grnd.getGrn_D_Selling_Amount());
				exItemPrice.setItemprice_type("GRN Price");
				if (exItemPrice.getCreated_by() == null) {
					exItemPrice.setCreated_by(loginService.getCurrentUser().getId());
				} else {
					exItemPrice.setModified_by(loginService.getCurrentUser().getId());
				}
				if (exItemPrice.getCreated_date() == null) {
					exItemPrice.setCreated_date(new Date());
				} else {
					exItemPrice.setModified_date(new Date());
				}
				exItemPrice.setStoreEntity(goodsReceiptNote_h.getStoreEntity());
				exItemPrice.setItem_closing_stock(grnd.getGrn_D_Accepted_Qnty());
				itemPriceRepository.save(exItemPrice);

				// StockTransaction Update

				StockTransaction exStock = stockTransactionRepository
						.getStockByItemPrice(exItemPrice.getItemprice_pk());
				

				if (exStock == null) {
					exStock = new StockTransaction();
					exStock.setStock_tran_date(new Date());
				}

				if (exStock.getStock_tran_item_price_fk() == null) {
					exStock.setStock_tran_item_price_fk(exItemPrice.getItemprice_pk());
				}
				exStock.setStoreEntity(goodsReceiptNote_h.getStoreEntity());
				exStock.setStock_tran_grn_h_id_fk(goodsReceiptNote_h.getGrn_h_id());
				if (goodsReceiptNote_h.getSupplierMaster() != null) {
					exStock.setStock_tran_supplier_id_fk(goodsReceiptNote_h.getSupplierMaster().getId());
				}

				exStock.setProductVarietyMaster(grnd.getProductVarietyMaster());
				if (exStock.getStock_tran_type_fk() == null) {
					StockTransactionType stockTransactionType = stockTransactionTypeRepository.getGRNID(StockTransactionTypeIFC.GRN);
					exStock.setStock_tran_type_fk(stockTransactionType.getStock_tran_type_pk());
				}

				exStock.setStock_tran_opening_bal(grnd.getGrn_D_Accepted_Qnty());
				exStock.setStock_tran_received_qnty(grnd.getGrn_D_Accepted_Qnty());
				exStock.setStock_tran_closing_bal(grnd.getGrn_D_Accepted_Qnty());

				exStock.setStock_tran_issued_qnty(0.0);
				if (grnd.getGrn_D_Selling_Amount() != null) {
					exStock.setStock_tran_sellingprice(grnd.getGrn_D_Selling_Amount());
				}
				if (grnd.getGrn_D_Purchase_Amount() != null) {
					exStock.setStock_tran_purchaseprice(grnd.getGrn_D_Purchase_Amount());
				}

				if (exStock.getCreated_by() == null) {
					exStock.setCreated_by(loginService.getCurrentUser().getId());
				} else {
					exStock.setModified_by(loginService.getCurrentUser().getId());
				}
				if (exStock.getCreated_date() == null) {
					exStock.setCreated_date(new Date());
				} else {
					exStock.setModified_date(new Date());
				}
				stockTransactionRepository.save(exStock);

				// goods changes history

				Goods_Change_History gch = new Goods_Change_History();

				if (!grnd.getGrn_D_Accepted_Qnty().equals(grnd.getOldQnty())
						|| !grnd.getOldPurchaseAmount().equals(grnd.getGrn_D_Purchase_Amount())
						|| !grnd.getOldSellingAmount().equals(grnd.getGrn_D_Selling_Amount())) {

					gch.setGrnId(goodsReceiptNote_h.getGrn_h_id());
					gch.setNewQty(grnd.getGrn_D_Accepted_Qnty());
					gch.setOldQty(grnd.getOldQnty());
					gch.setOldPurchaesPrice(grnd.getOldPurchaseAmount());
					gch.setNewPurchaesPrice(grnd.getGrn_D_Purchase_Amount());
					gch.setOldSellPrise(grnd.getOldSellingAmount());
					gch.setNewSellPrise(grnd.getGrn_D_Selling_Amount());
					gch.setCreatedDate(new Date());
					gch.setCreatedBy(loginService.getCurrentUser().getCreatedBy());
					gch.setRemarks("TEst");
				    gch.setProduct_variety_id(grnd.getProductVarietyMaster());

					goods_Change_History_Repository.save(gch);

				}

			}

		}

	}

	private GoodsReceiptNote_H saveGoodsReceiptNote_H(StockUploadDTO stockUpload) {
		GoodsReceiptNote_H goodsReceiptNote_H = new GoodsReceiptNote_H();

		goodsReceiptNote_H.setSupplierMaster(
				supplierMasterRepository.findOne(stockUpload.getGoodsReceiptNote_H().getSupplierMaster().getId()));
		
		List<String> billNumberAndStoreName  = sequenceConfigService.generateBillNumberAndSave(SequenceName.GRN);
		if (billNumberAndStoreName != null && billNumberAndStoreName.size() > 0) {
			goodsReceiptNote_H.setGrn_H_Number(billNumberAndStoreName.get(0));
			
		}
		if (stockUpload.getGrn_store_entity_fk() != 0) {

			goodsReceiptNote_H.setStoreEntity(entityMasterRepository.findOne(stockUpload.getGrn_store_entity_fk()));
		}
		
		goodsReceiptNote_H.setGrn_H_Date(new Date());
		goodsReceiptNote_H.setGrn_H_Total_Number(stockUpload.getGoodsReceiptNote_H().getGrn_H_Total_Number());
		goodsReceiptNote_H.setGrn_H_Gross_Amount(stockUpload.getGoodsReceiptNote_H().getGrn_H_Gross_Amount());
		goodsReceiptNote_H
				.setGrn_H_Discount_Percentage(stockUpload.getGoodsReceiptNote_H().getGrn_H_Discount_Percentage());
		goodsReceiptNote_H.setGrn_H_Discount_Amount(stockUpload.getGoodsReceiptNote_H().getGrn_H_Discount_Amount());
		goodsReceiptNote_H.setGrn_H_Status("Active");
		goodsReceiptNote_H.setUserMaster(userMasterRepository.findOne(loginService.getCurrentUser().getId()));
		goodsReceiptNote_H.setCreated_date(new Date());
		goodsReceiptNote_H = goodsReceiptNote_H_Repository.save(goodsReceiptNote_H);

		return goodsReceiptNote_H;
	}

	@Transactional
	public BaseDTO getStockUploadDetails(StockUploadDTO stockItems) {

		log.info("<-Inside SERVICE-Starts updateStockItem-->");
		BaseDTO baseDTO = new BaseDTO();
		try {

			GoodsReceiptNote_H goodsReceiptNote_H = goodsReceiptNote_H_Repository.findOne(stockItems.getGrnHID());

			if (goodsReceiptNote_H != null) {
				if (goodsReceiptNote_H.getSupplierMaster() != null) {
					stockItems.setSupplierCodeName(goodsReceiptNote_H.getSupplierMaster().getCode() + " / "
							+ goodsReceiptNote_H.getSupplierMaster().getName());
				}
				stockItems.setGrnNumber(goodsReceiptNote_H.getGrn_H_Number());
				stockItems.setGrnHID(goodsReceiptNote_H.getGrn_h_id());
				stockItems.setGrnDate(goodsReceiptNote_H.getGrn_H_Date());
				stockItems.setGoodsReceiptNote_DList(
						goodsReceiptNote_D_Repository.getdata(goodsReceiptNote_H.getGrn_h_id()));
				for (GoodsReceiptNote_D hib : stockItems.getGoodsReceiptNote_DList()) {
					hib.setOldQnty(hib.getGrn_D_Accepted_Qnty());
					hib.setOldPurchaseAmount(hib.getGrn_D_Purchase_Amount());
					hib.setOldSellingAmount(hib.getGrn_D_Selling_Amount());
					if (hib.getItem_barcode_number() == null) {
						hib.setItem_barcode_number("");
					}
					hib.setItem_barcode_number(hib.getItem_barcode_number().replaceAll(",", "").trim());
					Long itemMovementStatus =  stockTransactionRepository.checkItemMovementStatus(hib.getProductVarietyMaster().getId(), hib.getGrn_D_Selling_Amount(),
							goodsReceiptNote_H.getStoreEntity().getId());
					
					hib.setDiseabled(itemMovementStatus == 0 ? false : true);
				}

			}

			baseDTO.setResponseContent(stockItems);
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
		} catch (Exception exception) {
			log.error("Exception Occured updateStockItemInwardPNS : ", exception);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;
	}

	// This is method is used for LazySearch
	@SuppressWarnings("deprecation")
	public BaseDTO lazySearchStockTransfer(StockUploadDTO stockUploadDTO) {
		log.info("<-Inside SERVICE Starts Lazy Search StockTranfer-->");
		BaseDTO baseDTO = new BaseDTO();
		List<?> resultList = null;
		int totRecord = 0;
		try {
			
			if(stockUploadDTO.getPaginationDTO().getFilters().get("itemcodeorname") == null) {
			
			Session session = entityManager.unwrap(Session.class);
			Criteria criteria = session.createCriteria(GoodsReceiptNote_H.class, "goodsReceiptNoteH");
			criteria.createAlias("goodsReceiptNoteH.supplierMaster", "supplier", CriteriaSpecification.LEFT_JOIN);
			criteria.createCriteria("goodsReceiptNoteH.userMaster", "userMaster");
			criteria.createCriteria("goodsReceiptNoteH.storeEntity", "storeEntity");

			if (stockUploadDTO.getPaginationDTO().getFilters() != null) {

				if (stockUploadDTO.getPaginationDTO().getFilters().get("grn_h_id") != null) {
					Long id = (Long) stockUploadDTO.getPaginationDTO().getFilters().get("grn_h_id");
					log.info(" ID Filter : " + id);
					criteria.add(Restrictions.like("goodsReceiptNoteH.grn_h_id", "%" + id + '%').ignoreCase());
				}
				
				if (stockUploadDTO.getPaginationDTO().getFilters().get("storeName") != null) {
					String shopname =  stockUploadDTO.getPaginationDTO().getFilters().get("storeName").toString();
					log.info(" Store name : " + shopname);
					criteria.add(Restrictions.like("storeEntity.name", "%" + shopname + '%').ignoreCase());
				}
				
				if (stockUploadDTO.getPaginationDTO().getFilters().get("storeCode") != null) {
					String shopcode =  stockUploadDTO.getPaginationDTO().getFilters().get("storeCode").toString();
					log.info(" Store name : " + shopcode);
					criteria.add(Restrictions.like("storeEntity.code", "%" + shopcode + '%').ignoreCase());
				}
				
//				if (stockUploadDTO.getPaginationDTO().getFilters().get("totalPurchaseAmount") != null) {
//					String puramt = stockUploadDTO.getPaginationDTO().getFilters().get("totalPurchaseAmount").toString() ;
//					log.info(" pur amt : " + puramt);
//					criteria.add(Restrictions.like("goodsReceiptNoteH.grn_H_Gross_Amount", "%" + puramt + '%').ignoreCase());
//				}

				String transferType = (String) stockUploadDTO.getPaginationDTO().getFilters().get("grn_H_Number");
				if (transferType != null) {
					log.info(" Stock Tranfer Type INWARD Type filter value is -----> ");
					StockTransferType status = StockTransferType.valueOf(transferType);
					log.info(" transferType Filter : " + transferType);
					criteria.add(Restrictions.eq("goodsReceiptNoteH.grn_H_Number", status));
				}

				String codeName = (String) stockUploadDTO.getPaginationDTO().getFilters().get("supplierCodeName");
				if (StringUtils.isNotEmpty(codeName)) {
					log.info("Supplier Code Name :" + codeName);
					if (AppUtil.isInteger(codeName)) {
						criteria.add(Restrictions.like("supplier.code", "%" + codeName + '%').ignoreCase());
					} else {
						criteria.add(Restrictions.like("supplier.name", "%" + codeName + '%').ignoreCase());
					}
				}

				String grnNumber = (String) stockUploadDTO.getPaginationDTO().getFilters().get("grnNumber");
				if (StringUtils.isNotEmpty(grnNumber)) {
					log.info("grnNumber :" + grnNumber);
					criteria.add(
							Restrictions.like("goodsReceiptNoteH.grn_H_Number", "%" + grnNumber + '%').ignoreCase());
				}

//				String status = (String) stockUploadDTO.getPaginationDTO().getFilters().get("status");
//				if (status != null) {
//					StockTransferStatus stockTransferStatus = StockTransferStatus.valueOf(status);
//					log.info(" status Filter : " + status);
//					criteria.add(Restrictions.eq("goodsReceiptNoteH.grn_H_Status", stockTransferStatus));
//				}

				if (stockUploadDTO.getPaginationDTO().getFilters().get("grnDate") != null) {
					Date grnDate = new Date((long) stockUploadDTO.getPaginationDTO().getFilters().get("grnDate"));
					log.info("grnDate Filter : " + grnDate);
					criteria.add(Restrictions.eq("goodsReceiptNoteH.grn_H_Date", grnDate));
				}
				if (stockUploadDTO.getPaginationDTO().getFilters().get("modifiedDate") != null) {
					Date modifiedDate = new Date(
							(long) stockUploadDTO.getPaginationDTO().getFilters().get("modifiedDate"));

					java.sql.Date startDateTime = new java.sql.Date(modifiedDate.getTime());
					java.sql.Date endDateTime = new java.sql.Date(AppUtil.getEndDateTime(modifiedDate).getTime());
					log.info("modifiedDate Filter : " + modifiedDate);
					criteria.add(Restrictions.between("goodsReceiptNoteH.modified_date", startDateTime, endDateTime));

				}

			}

			criteria.setProjection(null);
			if (stockUploadDTO.getPaginationDTO().getSortField() != null) {

				String sortField = stockUploadDTO.getPaginationDTO().getSortField();
				String sortOrder = stockUploadDTO.getPaginationDTO().getSortOrder();
				if (sortField != null && sortOrder != null) {
					log.info("sortField : [" + sortField + "] sortOrder[" + sortOrder + "]");

					if (sortField.equals("id")) {

						sortField = "goodsReceiptNoteH.grn_h_id";

					} else if (sortField.equals("status")) {

						sortField = "goodsReceiptNoteH.grn_H_Status";

					}

					else if (sortField.equals("grn_H_Date")) {

						sortField = "goodsReceiptNoteH.grn_H_Date";

					}

					if (sortOrder.equals("DESCENDING")) {
						criteria.addOrder(Order.desc(sortField));
					} else {
						criteria.addOrder(Order.asc(sortField));
					}
				}

			} else {
				criteria.addOrder(Order.desc("goodsReceiptNoteH.grn_h_id"));
			}

			criteria.add(Restrictions.isNotNull("goodsReceiptNoteH.userMaster.id"));
			List<Long> idList = new ArrayList<>();
			idList.add(loginService.getCurrentUser().getEntityId());
//			EntityMaster entityMastr = loginService.getStoreDetails(loginService.getCurrentUser().getEntityId());
//			if (entityMastr.getEntityMasterParent() == null) {
//				List<EntityMaster> entityMasterList = entityMasterRepository
//						.findParentEntityMaster(entityMastr.getId());
//				if (entityMasterList != null) {
//					idList.addAll(entityMasterList.stream().map(e -> e.getId()).collect(Collectors.toList()));
//				}
//			}
			criteria.add(Restrictions.in("storeEntity.id", idList));
			if (loginService.getCurrentUser().getId() != 809) {
				// criteria.add(Restrictions.eq("goodsReceiptNoteH.userMaster.id",
				// loginService.getCurrentUser().getId()));
			}

			log.info("Criteria Query  : ");

			ProjectionList projectionList = Projections.projectionList();

			projectionList.add(Projections.property("goodsReceiptNoteH.grn_h_id"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Number"));
			projectionList.add(Projections.property("supplier.code"));
			projectionList.add(Projections.property("supplier.name"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Status"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Date"));
			
			projectionList.add(Projections.property("userMaster.username"));
			projectionList.add(Projections.property("goodsReceiptNoteH.modified_date"));
			projectionList.add(Projections.property("userMaster.modifiedDate"));
			projectionList.add(Projections.property("storeEntity.code"));
			projectionList.add(Projections.property("storeEntity.name"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Gross_Amount"));

			criteria.setProjection(projectionList);

			 totRecord = criteria.list().size();
			if (stockUploadDTO.getPaginationDTO() != null) {
				Integer pageNo = stockUploadDTO.getPaginationDTO().getFirst();
				Integer pageSize = stockUploadDTO.getPaginationDTO().getPageSize();

				if (pageNo != null && pageSize != null) {
					criteria.setFirstResult(pageNo * pageSize);
					criteria.setMaxResults(pageSize);
					log.info("PageNo : [" + pageNo + "] pageSize[" + pageSize + "]");
				}
			}
			 resultList = criteria.list();
			}else {
				if(stockUploadDTO.getPaginationDTO().getFilters().get("itemcodeorname")!=null) {
				resultList=goodsReceiptNote_H_Repository.getgrn_itemname(loginService.getCurrentUser().getEntityId(), stockUploadDTO.getPaginationDTO().getFilters().get("itemcodeorname").toString());
				totRecord=resultList.size();
				}
			}

		

			List<StockUploadDTO> responseList = new ArrayList<StockUploadDTO>();

			Iterator<?> it = resultList.iterator();
			while (it.hasNext()) {
				Object ob[] = (Object[]) it.next();
				StockUploadDTO response = new StockUploadDTO();
				response.setGrnHID(Long.parseLong(ob[0].toString()) );
				response.setGrnNumber(ob[1].toString());
				if (ob[2] != null && ob[3] != null) {
					response.setSupplierCodeName(((String) ob[2]) + " / " + (String) ob[3]);
				}
				if (ob[4] != null && ob[4] != null) {
					response.setStatus(ob[4].toString());
				}
				if (ob[5] != null && ob[5] != null) {
					response.setGrnDate((Date) ob[5]);
				}
				if (ob[6] != null && ob[6] != null) {
					response.setGrnCreatedBy(ob[6].toString());
				}
				if (ob[7] != null && ob[7] != null) {
					response.setModifiedDate((Date) ob[7]);
				} else {
					response.setModifiedDate((Date) ob[5]);
				}

				if (ob[9] != null) {
					response.setStoreCode(ob[9].toString());
				}
				if (ob[10] != null) {
					response.setStoreName(ob[10].toString());
				}
				if (ob[11] != null) {
					response.setTotalPurchaseAmount(Double.parseDouble(ob[11].toString()));
				}

				responseList.add(response);
			}

			baseDTO.setResponseContents(responseList);
			
		   baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			baseDTO.setTotalRecords(totRecord);

		} catch (RestException restException) {
			baseDTO.setStatusCode(restException.getStatusCode());
			log.error("RestException in PaymentService search() ", restException);
		} catch (Exception exception) {
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
			log.error("Exception in PaymentService search() ", exception);
		}
		return responseWrapper.send(baseDTO);
	}

	public Integer getProjectionCount(Criteria criteria) {

		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("stockTransfer.id"));
		projectionList.add(Projections.property("stockTransfer.transferType"));
		projectionList.add(Projections.property("supplierTypeMaster.code"));
		projectionList.add(Projections.property("supplierMaster.code"));
		projectionList.add(Projections.property("supplierMaster.name"));

		projectionList.add(Projections.property("stockTransfer.status"));

		projectionList.add(Projections.property("stockTransfer.dateReceived"));

		criteria.setProjection(projectionList);

		List<?> resultList = criteria.list();

		return resultList.size();

	}

	// View selected StockItem List
	// product group master
	public BaseDTO getAllProductVarietyMasterList(Long id) {

		log.info("<-Inside SERVICE- Starts getAllProductVarietyMasterList-->");
		BaseDTO baseDTO = new BaseDTO();
		try {
			List<ProductVarietyMaster> intendList = productVarietyMasterRepository.getAllProductVarietyMaster(id);
			log.info("<-Inside SERVICE- Starts getAllProductVarietyMasterList--> size is :" + intendList.size());
			baseDTO.setResponseContent(intendList);
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			log.info("<-getAllProductCategoryList Data Success-->");
		} catch (Exception exception) {
			log.error("exception Occured getAllProductCategoryList : ", exception);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;

	}

	// Selected Variety Master
	public BaseDTO getSelectedProductVarietyMaster(Long id) {

		log.info("<-Inside SERVICE- Starts getAllProductVarietyMasterList-->");
		BaseDTO baseDTO = new BaseDTO();
		try {
			ProductVarietyMaster intendList = productVarietyMasterRepository.getSelectedProductVarietyMaster(id);
			log.info("<-Inside SERVICE- Starts getAllProductVarietyMasterList--> ");
			baseDTO.setResponseContent(intendList);
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());
			log.info("<-getAllProductCategoryList Data Success-->");
		} catch (Exception exception) {
			log.error("exception Occured getAllProductCategoryList : ", exception);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		return baseDTO;

	}

	public BaseDTO autoCompleteSupplier(String suppliercode) {
		BaseDTO response = new BaseDTO();
		List<SupplierMaster> supplierMasterList = new ArrayList<>();
		SupplierMaster supplierMaster = new SupplierMaster();
		try {
			suppliercode = "%" + suppliercode.trim() + "%";
			Long userId = loginService.getCurrentUser().getId();
			List<SupplierMaster> supplierMasterlistt = supplierMasterRepository.autoCompleteSupplier(suppliercode,
					userId);
			if (supplierMasterlistt != null) {
				for (SupplierMaster sup : supplierMasterlistt) {
					supplierMaster = new SupplierMaster();
					if (sup.getName() != null) {
						supplierMaster.setId(sup.getId());
						supplierMaster.setCode(sup.getCode());
						supplierMaster.setName(sup.getName());
						supplierMaster.setGstNumber(sup.getGstNumber());
						supplierMaster.setAadharNumber(sup.getAadharNumber());
						supplierMasterList.add(supplierMaster);
					}

				}
			}
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(supplierMasterList);
		} catch (Exception e) {
			log.error("Exception Occured In StockItemInwardPNS Service autoCompleteSupplier", e);
		}
		return response;
	}

	public BaseDTO autoCompleteItem(String itemName) {
		BaseDTO response = new BaseDTO();
		List<ProductVarietyMaster> productVarietyMasterList = new ArrayList<>();
		ProductVarietyMaster productVarietyMaster = new ProductVarietyMaster();
		try {
			itemName = "%" + itemName.trim() + "%";
			Long userId = loginService.getCurrentUser().getEntityId();
			
			List<ProductVarietyMaster> productVarietyMasterListt = productVarietyMasterRepository
					.autoCompleteItem(itemName, userId);
			if (productVarietyMasterListt != null) {
				for (ProductVarietyMaster pvm : productVarietyMasterListt) {
					productVarietyMaster = new ProductVarietyMaster();
					if (pvm.getName() != null) {
						productVarietyMaster.setId(pvm.getId());
						productVarietyMaster.setCode(pvm.getCode());
						productVarietyMaster.setName(pvm.getName());
						productVarietyMaster.setCgst_percentage(pvm.getCgst_percentage());
						productVarietyMaster.setSgst_percentage(pvm.getSgst_percentage());
						productVarietyMaster.setHsnCode(pvm.getHsnCode());
						if (pvm.getUomMaster() != null) {
							productVarietyMaster.setUomMaster(pvm.getUomMaster());
						}
						productVarietyMaster.setActiveStatus(pvm.getActiveStatus());
						productVarietyMasterList.add(productVarietyMaster);
					}

				}
			}
			response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			response.setResponseContents(productVarietyMasterList);
		} catch (Exception e) {
			log.error("Exception Occured In StockItemInwardPNS Service autoCompleteSupplier", e);
		}
		return response;
	}

	public BaseDTO getShopNames(Long id) {
		BaseDTO respons = new BaseDTO();
		List<EntityMaster> entityList = entityMasterRepository.findParentEntityMaster(id);
		respons.setResponseContents(entityList);
		respons.setStatusCode(0);

		return respons;

	}
	
	public BaseDTO getdataforexcel() {
		log.info("<-Inside SERVICE getdataforexcel()-->");
		Long autoIncrement = 0L;
		Map<Long, StockUploadDTO> StockUploadDTOMap = new HashMap();
		BaseDTO baseDTO = new BaseDTO();
		
		try {
			Session session = entityManager.unwrap(Session.class);
			Criteria criteria = session.createCriteria(GoodsReceiptNote_H.class, "goodsReceiptNoteH");
			criteria.createAlias("goodsReceiptNoteH.supplierMaster", "supplier", CriteriaSpecification.LEFT_JOIN);
			criteria.createCriteria("goodsReceiptNoteH.userMaster", "userMaster");
			criteria.createCriteria("goodsReceiptNoteH.storeEntity", "storeEntity");
			criteria.setProjection(null);
			
			criteria.add(Restrictions.isNotNull("goodsReceiptNoteH.userMaster.id"));
			List<Long> idList = new ArrayList<>();
			idList.add(loginService.getCurrentUser().getEntityId());
			EntityMaster entityMastr = loginService.getStoreDetails(loginService.getCurrentUser().getEntityId());
			if (entityMastr.getEntityMasterParent() == null) {
				List<EntityMaster> entityMasterList = entityMasterRepository
						.findParentEntityMaster(entityMastr.getId());
				if (entityMasterList != null) {
					idList.addAll(entityMasterList.stream().map(e -> e.getId()).collect(Collectors.toList()));
				}
			}
			criteria.add(Restrictions.in("storeEntity.id", idList));
			

			log.info("Criteria Query  : ");

			ProjectionList projectionList = Projections.projectionList();

			projectionList.add(Projections.property("goodsReceiptNoteH.grn_h_id"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Number"));
			projectionList.add(Projections.property("supplier.code"));
			projectionList.add(Projections.property("supplier.name"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Status"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Date"));
			projectionList.add(Projections.property("userMaster.username"));
			projectionList.add(Projections.property("userMaster.modifiedDate"));
			projectionList.add(Projections.property("userMaster.modifiedDate"));
			projectionList.add(Projections.property("storeEntity.code"));
			projectionList.add(Projections.property("storeEntity.name"));
			projectionList.add(Projections.property("goodsReceiptNoteH.grn_H_Gross_Amount"));

			criteria.setProjection(projectionList);
			
			List<?> resultList1 = criteria.list();
			Iterator<?> it = resultList1.iterator();
			while (it.hasNext()) {
			 autoIncrement++;
				Object ob[] = (Object[]) it.next();
				StockUploadDTO response = new StockUploadDTO();
				response.setGrnHID((Long) ob[0]);
				response.setGrnNumber(ob[1].toString());
				if (ob[2] != null && ob[3] != null) {
					response.setSupplierCodeName(((String) ob[2]) + " / " + (String) ob[3]);
				}
				if (ob[4] != null && ob[4] != null) {
					response.setStatus(ob[4].toString());
				}
				if (ob[5] != null && ob[5] != null) {
					response.setGrnDate((Date) ob[5]);
				}
				if (ob[6] != null && ob[6] != null) {
					response.setGrnCreatedBy(ob[6].toString());
				}
				if (ob[7] != null && ob[7] != null) {
					response.setModifiedDate((Date) ob[7]);
				} else {
					response.setModifiedDate((Date) ob[5]);
				}

				if (ob[9] != null) {
					response.setStoreCode(ob[9].toString());
				}
				if (ob[10] != null) {
					response.setStoreName(ob[10].toString());
				}
				if (ob[11] != null) {
					response.setTotalPurchaseAmount(Double.parseDouble(ob[11].toString()));
				}
				
				if (!(StockUploadDTOMap.containsKey(autoIncrement))) {

					StockUploadDTOMap.put(autoIncrement, response);
				}
			}
		
			
			List<Map<String, Object>> listMapForExcel = new ArrayList<>();
			for (Long dto : StockUploadDTOMap.keySet()) {
				Map<String, Object> map = new HashMap<>();
				StockUploadDTO dailyDto = StockUploadDTOMap.get(dto);
				map.put("StoreCode", dailyDto.getStoreCode());
				map.put("StoreName", dailyDto.getStoreName());
				map.put("GrnNumber", dailyDto.getGrnNumber());
				map.put("SuppilerCode", dailyDto.getSupplierCodeName());
				map.put("TotalPurcheashAmt", dailyDto.getTotalPurchaseAmount());
				map.put("GrnCreatedDate", dailyDto.getGrnCreatedDate());
				map.put("GrnModifyDate", dailyDto.getModifiedDate());
				
				listMapForExcel.add(map);
			}
			baseDTO.setListOfData(listMapForExcel);
			
		}catch (Exception e) {
			// TODO: handle exception
			log.error("Exception in getdataforexcel() ", e);
			
		}
		
		return baseDTO;
	}

}
