package co.orffosoft.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.orffosoft.dto.BaseDTO;
import co.orffosoft.dto.DirectBillingReportDTO;
import co.orffosoft.dto.GoodsChangesHistoryReportDTO;
import co.orffosoft.entity.DirectBilling_D;
import co.orffosoft.entity.DirectBilling_H;
import co.orffosoft.entity.UserMaster;
import co.orffosoft.repository.DirectBilling_D_Repository;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class DirectBillingReportSrevice {
	@Autowired
	LoginService loginService;
	
	@PersistenceContext
	EntityManager manager;
	
	@Autowired
	DirectBilling_D_Repository DirectBilling_D_Repository;

	public BaseDTO GeneretReport(DirectBillingReportDTO dto) {
		BaseDTO baseDto = new BaseDTO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String query = null;
		String parameter="";
		Query sql = null;
		List<Object[]> obj = null;
		Iterator<Object[]> billReport = null;
		//DirectBillingReportDTO directbillingdto = new DirectBillingReportDTO();
		//List<DirectBillingReportDTO> listdirectbillingdto=new ArrayList();
		DirectBilling_H directbillingh;
		List<DirectBilling_H> listdirectbillingh=new ArrayList();
		Long entityid=loginService.getCurrentUser().getEntityId();
		try {
			if(dto!=null) {
				query = "select dbh.id,dbh.bill_number,dbh.bill_date,dbh.bill_value,dbh.created_by,um.username from direct_billing_h dbh " + 
						"inner join user_master um on um.id=dbh.created_by where :input";

				if (dto.getBillNo() != null && !dto.getBillNo().isEmpty()) {
					parameter= parameter + "dbh.bill_number ilike '%"+ dto.getBillNo() +"%' and ";
				}
				if (dto.getCustomername() != null && !dto.getCustomername().isEmpty()) {
					parameter= parameter + "dbh.customername ilike '%"+ dto.getCustomername() +"%' and ";
				}
				if (dto.getMobilenumber() != null) {
					parameter= parameter + "dbh.mobile_number = '"+ dto.getMobilenumber() +"' and ";
				}
				if (dto.getFromdate() != null) {
					
					Calendar calToDate = Calendar.getInstance();
					calToDate.setTime(dto.getFromdate());

					StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
					while (toDate.hasMoreTokens()) {
						dto.setDate(toDate.nextToken() + " 23:59:59 ");
						break;
					}
					parameter= parameter + "dbh.bill_date between '"+ dto.getDate() +"' and ";
				}
				if (dto.getTodate() != null) {
					Calendar calToDate = Calendar.getInstance();
					calToDate.setTime(dto.getTodate());

					StringTokenizer toDate = new StringTokenizer(sdf.format(calToDate.getTime()), " ");
					while (toDate.hasMoreTokens()) {
						dto.setDate(toDate.nextToken() + " 23:59:59 ");
						break;
					}
					parameter= parameter + " '"+ dto.getDate() +"' and ";

				}
				
				if(!dto.getBillNo().isEmpty() || !dto.getCustomername().isEmpty() || dto.getMobilenumber()!=null || dto.getFromdate()!=null
						|| dto.getTodate()!=null) {
					parameter=parameter +" dbh.entityid = "+ entityid + "" ;
				}
				
				query = query.replaceAll(":input", parameter);
				log.info("Sales Return List Query "+ query);
				sql = manager.createNativeQuery(query);
				obj = sql.getResultList();
				billReport = obj.iterator();
				
				if (billReport != null) {
					
					while (billReport.hasNext()) {
						//directbillingdto = new DirectBillingReportDTO();
						directbillingh = new DirectBilling_H();
						Object[] ob = billReport.next();
						
						if(ob[0]!=null) {
							//directbillingdto.setBillNo();
							directbillingh.setId(Long.valueOf(ob[0].toString()));
						}
						
						if(ob[1]!=null) {
							//directbillingdto.setBillNo();
							directbillingh.setBillNumber(ob[1].toString());
						}
						if(ob[2]!=null) {
							//directbillingdto.setDate(ob[2].toString());
							directbillingh.setDate(ob[2].toString());
						}
						if(ob[3]!=null) {
							//directbillingdto.setTotalNetPrice(Double.parseDouble(ob[3].toString()));
							directbillingh.setNetTotal(Double.parseDouble(ob[3].toString()));
						}
						
						if(ob[5]!=null) {
							//directbillingdto.setTotalNetPrice(Double.parseDouble(ob[3].toString()));
							directbillingh.setUsername(ob[5].toString());
						}
						listdirectbillingh.add(directbillingh);
					}
				}
				baseDto.setResponseContents(listdirectbillingh);
			}
		} catch (Exception e) {
			log.error(" >>  Exception Occured In DirectBillingReport >> generateReport method >> ", e);
		}

		return baseDto;

	}
	public BaseDTO showViewForm(DirectBilling_H directbill) {
		BaseDTO baseDTO=new BaseDTO();
		try {
			List<DirectBilling_D> listdirectbill=new ArrayList();
			if(directbill!=null && directbill.getId()!=null) {
				listdirectbill=DirectBilling_D_Repository.getdata(directbill.getId());
			}
			baseDTO.setResponseContents(listdirectbill);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return baseDTO;
		
		
	}
}
