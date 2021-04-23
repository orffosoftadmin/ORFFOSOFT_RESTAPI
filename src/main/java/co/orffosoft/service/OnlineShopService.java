package co.orffosoft.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.JDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.core.util.RestException;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.OnlineShopResponse;
import co.orffosoft.dto.PaginationDTO;
import co.orffosoft.dto.ProductVariryMastersearchDTO;
import co.orffosoft.rest.util.ResponseWrapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class OnlineShopService {

	@Autowired
	ResponseWrapper responseWrapper;

	@PersistenceContext
	EntityManager entityManager;
	
	@PersistenceContext
	EntityManager manager;
	
	@Autowired
	LoginService loginService;
	
	public BaseDTO searchProductVarities(ProductVariryMastersearchDTO productVariryMastersearchDTO) {

		if (productVariryMastersearchDTO == null) {
			throw new RestException(ErrorDescription.REQUEST_OBJECT_SHOULD_NOT_EMPTY);

		}
		log.info("productVariryMastersearchDTO === >>>", productVariryMastersearchDTO);
		if (productVariryMastersearchDTO != null) {
			PaginationDTO paginationDTO = productVariryMastersearchDTO.getPaginationDTO();
		}
		BaseDTO baseDTO = new BaseDTO();
		String query = null;
		String parameter = "";
		Query sql = null;
		List<Object[]> obj = null;
		List<Object[]> obj1 = null;
		Iterator<Object[]> billReport = null;
		int size=0;
		List<OnlineShopResponse> onlineShopResponseDTOList = new ArrayList<>();

		try {
			query= " select sum(st.stock_tran_received_qnty - st.stock_tran_issued_qnty),ip.itemprice_selling_price,pvm.name from stock_transaction st " + 
					" inner join itemprice ip on st.stock_tran_item_price_fk=ip.itemprice_pk " + 
					" inner join product_variety_master pvm on st.stock_tran_sku_id_fk=pvm.id " + 
					" where st.stock_tran_store_entity_fk = " + loginService.getCurrentUser().getEntityId() + 
					" group by ip.itemprice_selling_price,ip.itemprice_selling_price,pvm.name order by pvm.name asc "  ;
			
		
			long id=loginService.getCurrentUser().getEntityId();
			log.info(" OnlineShop Query ="+query);
			query=query.replaceAll("input:", "");
			sql = manager.createNativeQuery(query);
			obj1 = sql.getResultList();
			size=obj1.size();
			baseDTO.setTotalRecords(size);
			
			parameter=" offset "+productVariryMastersearchDTO.getPaginationDTO().getPageNo()*
					productVariryMastersearchDTO.getPaginationDTO().getPageSize() ;
			query=query + parameter;
			sql = manager.createNativeQuery(query);
			obj = sql.getResultList();
			billReport = obj.iterator();

			
			
			
			while (billReport.hasNext()) {
				Object ob[] = billReport.next();

				OnlineShopResponse onlineShopResponse = new OnlineShopResponse();
				//onlineShopResponse.setVarityId(Long.parseLong(ob[0].toString()));
				//onlineShopResponse.setStoreName((String) ob[1]);
				//onlineShopResponse.setVarityCodeorName((String) ob[2]);
				onlineShopResponse.setVarityName((String) ob[2]);
				onlineShopResponse.setClosingBlance(Double.valueOf(ob[0].toString()));
				onlineShopResponse.setItemPrice(Double.valueOf(ob[1].toString()));
				log.info(onlineShopResponse);
				onlineShopResponseDTOList.add(onlineShopResponse);
			}
			baseDTO.setResponseContents(onlineShopResponseDTOList);
			baseDTO.setTotalRecords(onlineShopResponseDTOList.size());
			baseDTO.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getErrorCode());

		} catch (RestException re) {
			baseDTO.setStatusCode(re.getStatusCode());
		} catch (JDBCException jdbcExp) {
			log.error("<< JDBC Exception ==>>", jdbcExp);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		} catch (Exception exp) {
			log.error("<<== Exception In Search Service == >>", exp);
			baseDTO.setStatusCode(ErrorDescription.FAILURE_RESPONSE.getErrorCode());
		}
		log.info("<< == End Of Product Variety Master Search Method ==>>");

		return responseWrapper.send(baseDTO);
	}
	
}
