package co.orffosoft.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import co.orffosoft.dto.DamageExpiredReportDto;
import co.orffosoft.entity.ProductVarietyMaster;
import co.orffosoft.repository.ProductVarietyMasterRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DamageExpiredReportService {
	@Autowired
	LoginService loginService;
	@Autowired
	ProductVarietyMasterRepository productVarietyMasterRepository;

	@PersistenceContext
	EntityManager manager;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public BaseDTO generetReport(DamageExpiredReportDto dto) {
		BaseDTO basedto = new BaseDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String query = null;
		Query sql = null;
		String parameter = "";
		// Iterator<?> billReport = null;
		DamageExpiredReportDto damageexpiredto=new DamageExpiredReportDto();
		String l = loginService.getCurrentUser().getEntityCode();
		List<DamageExpiredReportDto> damageexpiredtolist = new ArrayList();
		Iterator<Map<String, Object>> billReport = null;
		try {
			if (dto.getFromdate() != null && dto.getTodate() != null) {
//				query = " select adj.action,adj.itemid,adj.name, sum(adj.actionqty),  sum(adj.purchase_price) as price  from stock_adjustment adj "
//						+ " where adj.storecode = '" + loginService.getCurrentUser().getEntityCode()
//						+ "' and adj.action in ('EXPIRED','DAMAGED') input"
//						+ " group by adj.action,adj.name,adj.itemid  order by adj.itemid, adj.action";
				
				query = " select adj.action,adj.itemid,adj.name, sum(adj.actionqty),  adj.purchase_price as price  from stock_adjustment adj "
						+ " where adj.storecode = '" + loginService.getCurrentUser().getEntityCode()
						+ "' and adj.action in ('EXPIRED','DAMAGED') input"
						+ " group by adj.action,adj.name,adj.itemid,adj.purchase_price  order by adj.itemid, adj.action";

				if (dto.getProductvaritymaster() != null && dto.getProductvaritymaster().getName() != null) {

					parameter = parameter + " and adj.name = '" + dto.getProductvaritymaster().getName() + "'";
				}

				if (dto.getFromdate() != null) {
					Calendar calFromDate = Calendar.getInstance();
					calFromDate.setTime(dto.getFromdate());

					StringTokenizer fromDate = new StringTokenizer(sdf.format(calFromDate.getTime()), " ");
					while (fromDate.hasMoreTokens()) {
						dto.setFromdatee(fromDate.nextToken());
						break;
					}

					parameter = parameter + " and adj.stock_tran_adj_date between'" + dto.getFromdatee() + "' and ";
				}
				if (dto.getTodate() != null) {
					Calendar calToDate = Calendar.getInstance();
					calToDate.setTime(dto.getTodate());

					StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
					while (toDate.hasMoreTokens()) {
						dto.setTodatee(toDate.nextToken());
						break;
					}

					parameter = parameter + "' " + dto.getTodatee() + "' ";
				}
				query = query.replaceAll("input", parameter);
				log.info(" Query == " + query);
				sql = manager.createNativeQuery(query);
				Long itemId = 0L;
				int index = -1;
				List<Map<String, Object>> obj = jdbcTemplate.queryForList(query);
				Map<Long,Object> map=new HashMap<>();
				billReport = obj.iterator();
				if (billReport != null) {
					while (billReport.hasNext()) {
						damageexpiredto = new DamageExpiredReportDto();
						damageexpiredto.setDamageqnty(0.0);
						damageexpiredto.setExpiredqnty(0.0);
						damageexpiredto.setLossamount(0.0);
						damageexpiredto.setExpireamount(0.0);
						Map<String, Object> ob = billReport.next();
						if (itemId == Long.parseLong(ob.get("itemId").toString())) {
							
							damageexpiredto = damageexpiredtolist.get(index);
							adDetails(damageexpiredto, ob);
							
						} else {
							damageexpiredto.setItemname(ob.get("name").toString());
							//damageexpiredto.set
							adDetails(damageexpiredto, ob);
							damageexpiredtolist.add(damageexpiredto);
							index++;
						}
						
						itemId = Long.parseLong(ob.get("itemId").toString());
					}
					
				}
			}
			basedto.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			basedto.setResponseContents(damageexpiredtolist);

		} catch (Exception e) {
			log.error("Exception Occured In DamageExpiredReportService Service generetReport", e);
		}
		return basedto;

	}

	public void adDetails(DamageExpiredReportDto damageexpiredto, Map<String, Object> ob) {
		
		if (ob != null && ob.get("action") != null && ob.get("action").equals("EXPIRED")) {
			damageexpiredto.setExpiredqnty(Double.valueOf(ob.get("sum").toString()));
			damageexpiredto.setExpireamount(Double.parseDouble(ob.get("price").toString()) * Double.valueOf(ob.get("sum").toString()));
		}
		if (ob != null && ob.get("action") != null && ob.get("action").equals("DAMAGED")) {
			damageexpiredto.setDamageqnty(Double.valueOf(ob.get("sum").toString()));
			damageexpiredto.setDamageamount(Double.parseDouble(ob.get("price").toString()) * Double.valueOf(ob.get("sum").toString()));
		}
	}

	public BaseDTO itemAutoSearch(String itemName) {
		BaseDTO basedto = new BaseDTO();
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
			basedto.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
			basedto.setResponseContents(productVarietyMasterList);

		} catch (Exception e) {
			log.error("Exception Occured In DamageExpiredReportService Service autoCompleteSupplier", e);
		}

		return basedto;

	}

}
