package co.orffosoft.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.core.util.ErrorDescription;
import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.SupplierGST_Detail_DTO;
import co.orffosoft.entity.SupplierMaster;
import co.orffosoft.repository.GoodsReceiptNote_D_Repository;
import co.orffosoft.repository.SupplierMasterRepository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SupplierGSTDetailSrevice {
	
	@Autowired
	LoginService loginService;
	
	@PersistenceContext
	EntityManager manager;
	
	@Autowired
	SupplierMasterRepository supplierMasterRepository;
	
	@Autowired
	GoodsReceiptNote_D_Repository goodsReceiptNote_D_Repository;
	
	
	public BaseDTO viewBillDetails(SupplierGST_Detail_DTO dto) {
		log.info("::: SupplierGSTDetailSrevice >> Start viewBillDetails()  :::");
		BaseDTO response= new BaseDTO();
		SupplierGST_Detail_DTO suplierdto;
		List<SupplierGST_Detail_DTO> listsuplierdto=new ArrayList();
		Iterator<Object[]> billReport = null;
		try {
			if(dto!=null&& dto.getGrnhpk()!=null) {
				
				List<Object[]> getOrder_qnty = goodsReceiptNote_D_Repository.getOrder_qnty(loginService.getCurrentUser().getEntityId(), dto.getGrnhpk());
				billReport=getOrder_qnty.iterator();
				log.info("generate control Receive in SupplierGSTdetailcontroller ");
				if(billReport!=null) {
					while (billReport.hasNext()) {
						suplierdto= new SupplierGST_Detail_DTO();
						Object[] ob = billReport.next();
						
						if(ob[0]!=null) {
							suplierdto.setItemname(ob[0].toString());
						}
						if(ob[1]!=null) {
							suplierdto.setOrderQnty(Double.parseDouble(ob[1].toString()));
						}
						if(ob[2]!=null) {
							suplierdto.setAccpetqnty(Double.parseDouble(ob[2].toString()));
						}
						if(ob[3]!=null) {
							suplierdto.setPurchesamount(Double.parseDouble(ob[3].toString()));
						}
						if(ob[4]!=null) {
							suplierdto.setSellingamount(Double.parseDouble(ob[4].toString()));
						}
						if(ob[5]!=null) {
							suplierdto.setDiscounramount(Double.parseDouble(ob[5].toString()));
						}
						if(ob[6]!=null) {
							suplierdto.setMrp(Double.parseDouble(ob[6].toString()));
						}
						listsuplierdto.add(suplierdto);
						
					}
					response.setResponseContents(listsuplierdto);
					response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
				}
			}
			
			
		}catch (Exception e) {
			log.error("Exception Occured in generate load in service", e);
		}
		
		return response;
		
	}
	
	public BaseDTO generate(SupplierGST_Detail_DTO dto) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		BaseDTO response= new BaseDTO();
		String query = null;
		Query sql = null;
		List<Object[]> obj = null;
		Iterator<Object[]> billReport = null;
		String parameter = "";
		SupplierGST_Detail_DTO suplierdto;
		List<SupplierGST_Detail_DTO> listsuplierdto=new ArrayList();
		
		try {
			if(dto!=null || dto.getFromdate()!=null) {
				
				query=" select sm.name as suppliername, grnd.grn_d_id,  grnh.grn_h_id , grnd.grn_d_cgst_amount as  " + 
						" cgstAmt, grnd.grn_d_cgst_percentage as cgstper, grnd.grn_d_sgst_amount as sgstamt , " + 
						" grnd.grn_d_sgst_percentage as sgstper , grnd.grn_d_net_amount as netamt,grnd.grn_d_total_purchase_amt as purchesamt " + 
						"  , sm.gst_number as gstnumber,grnh.grn_h_date as date from goods_receipt_note_h grnh  " + 
						" inner join goods_receipt_note_d grnd on grnd.grn_d_grn_h_id_fk=grnh.grn_h_id "
						+ " inner join supplier_master sm on sm.id=grnh.grn_h_supplier_id_fk " + 
						" where input";
				
				if (dto.getFromdate() != null) {
					Calendar calFromDate = Calendar.getInstance();
					calFromDate.setTime(dto.getFromdate());

					StringTokenizer fromDate = new StringTokenizer(sdf.format(calFromDate.getTime()), " ");
					while (fromDate.hasMoreTokens()) {
						dto.setFromDateStr(fromDate.nextToken());
						break;
					}

					parameter = parameter + " grnh.grn_h_date between '" + dto.getFromDateStr()
							+ "' and ";
				}
				
				if (dto.getTodate() != null) {
					Calendar calToDate = Calendar.getInstance();
					calToDate.setTime(dto.getTodate());

					StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
					while (toDate.hasMoreTokens()) {
						dto.setToDateStr(toDate.nextToken());
						break;
					}

					parameter = parameter + "' " + dto.getToDateStr() + "' ";
				}
				
				if(dto.getSupplierMaster()!=null) {
					
					parameter = parameter+"and grnh.grn_h_supplier_id_fk = " +dto.getSupplierMaster().getId()+" ";
				}
				parameter = parameter+"and grnh.grn_store_entity_fk = " +loginService.getCurrentUser().getEntityId()+" ";
				
				query = query.replaceAll("input", parameter);
				//log.info(" Query == " + query);
				sql = manager.createNativeQuery(query);
				obj = sql.getResultList();
				
				billReport = obj.iterator();
				if(billReport!=null) {
					while (billReport.hasNext()) {
						suplierdto= new SupplierGST_Detail_DTO();
						
						Object[] ob = billReport.next();
						
						if(ob[0]!=null) {
							suplierdto.setSuppliername(ob[0].toString());
						}
						if(ob[1]!=null) {
							suplierdto.setGrndpk(Long.parseLong(ob[1].toString()));
						}
						if(ob[2]!=null) {
							suplierdto.setGrnhpk(Long.parseLong(ob[2].toString()));
						}
					
						if(ob[3]!=null) {
							suplierdto.setCgstamt(Double.valueOf(ob[3].toString()));
						}
						
						if(ob[4]!=null) {
							suplierdto.setCgstpersentage(Double.valueOf(ob[4].toString()));
						}
						
						if(ob[5]!=null) {
							suplierdto.setSgstamt(Double.valueOf(ob[5].toString()));
						}
						if(ob[6]!=null) {
							suplierdto.setSgstpersentage(Double.valueOf(ob[6].toString()));
						}
						if(ob[7]!=null) {
							suplierdto.setNetamount(Double.valueOf(ob[7].toString()));
						}
						if(ob[8]!=null) {
							suplierdto.setPurchesamount(Double.valueOf(ob[8].toString()));
						}
						if(ob[9]!=null) {
							suplierdto.setGstno((ob[9].toString()));
						}
						if(ob[10]!=null) {
							suplierdto.setTransactiondate(ob[10].toString());
						}
						
						listsuplierdto.add(suplierdto);
					}
					response.setResponseContents(listsuplierdto);
					response.setStatusCode(ErrorDescription.SUCCESS_RESPONSE.getCode());
				}
				
			}
			
			
		}catch (Exception e) {
			log.error("Exception Occured in generate load in service", e);
		}
		return response;
		
		
	}
	
	public BaseDTO suplierAutoSearch(String suppliercode){
		BaseDTO response = new BaseDTO();
		List<SupplierMaster> supplierMasterList = new ArrayList<>();
		SupplierMaster supplierMaster = new SupplierMaster();
		try {
			suppliercode = "%" + suppliercode.trim() + "%";
			Long userId = loginService.getCurrentUser().getId();
			List<SupplierMaster> supplierMasterlistt = supplierMasterRepository.autoCompletename(suppliercode,
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
			
		}catch (Exception e) {
			log.error("Exception Occured in suplierAutoSearch load in service", e);		}
		
		return response;
		
	}

}
