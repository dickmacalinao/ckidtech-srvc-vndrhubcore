package com.ckidtech.quotation.service.app.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.ReferenceDataRepository;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.utils.Util;
import com.mongodb.client.DistinctIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@ComponentScan({ "com.ckidtech.quotation.service.core.controller" })
@EnableMongoRepositories("com.ckidtech.quotation.service.core.dao")
@Service
public class ReferenceDataService {

	private static final Logger LOG = Logger.getLogger(ReferenceDataService.class.getName());
	
	@Autowired
	private ReferenceDataRepository referenceDataRepository;
	
	@Autowired
	private MessageController msgController;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	

	/**
	 * View all REST Connetion configuration. open and not secured
	 * 
	 * @return
	 */	
	public Map<String, List<ReferenceData>> viewRESTConnectionConfig() {

		LOG.log(Level.INFO, "Calling AppConfig Service viewAllRefernceData()");
		Map<String, List<ReferenceData>> refData = new HashMap<String, List<ReferenceData>>();
		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "value");
		
		refData.put("ServiceURL", referenceDataRepository.searchByRefGroup("ServiceURL", pageable));
		
		List<ReferenceData> refList;
		for ( ReferenceData refGroup : referenceDataRepository.searchByRefGroup("ServiceURL", pageable) ) {		
			String refId = refGroup.getId().split(":")[2];
			refList = referenceDataRepository.searchByRefGroup(refId, pageable);
			if ( !refList.isEmpty() )			
				refData.put(refId, refList);	
		}
		
		return refData;
	}
	

	/**
	 * View all App Config records
	 * 
	 * @return
	 */	
	public Map<String, List<ReferenceData>> viewAllReferenceData() {

		LOG.log(Level.INFO, "Calling AppConfig Service viewAllRefernceData()");
		Map<String, List<ReferenceData>> refData = new HashMap<String, List<ReferenceData>>();			
		
		List<ReferenceData> refList;
		for ( String refGroupName : queryAllReferenceGroup() ) {			
			refList = viewReferenceDataByRefGroup(refGroupName);
			if ( !refList.isEmpty() )			
				refData.put(refGroupName, refList);	
		}
		
		return refData;
	}
	
	/**
	 * View all App Config records by Group
	 * 
	 * @return
	 */	
	public List<ReferenceData> viewReferenceDataByRefGroup(String refGroup) {

		LOG.log(Level.INFO, "Calling AppConfig Service viewReferenceDataByRefGroup()");
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
		return referenceDataRepository.searchByRefGroup(refGroup, pageable);
	}
	
	
	public List<ReferenceData> viewReferenceDataByRoleAndRefGroup(String grantTo, String refGroup) {

		LOG.log(Level.INFO, "Calling AppConfig Service viewReferenceDataByRoleAndRefGroup()");
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
		return referenceDataRepository.searchByRoleAndRefGroup(grantTo, refGroup, pageable);
	}
	
	/**
	 * Create App Config record
	 * 
	 * @return
	 */	
	public QuotationResponse createReferenceData(ReferenceData refData) {
		LOG.log(Level.INFO, "Calling AppConfig Service createReferenceData()");
		
		QuotationResponse quotation = new QuotationResponse();
		if (refData.getGrantTo() == null || "".equals(refData.getGrantTo()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Grant To"));
		if (refData.getRefGroup() == null || "".equals(refData.getRefGroup()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Reference Group"));
		if (refData.getId() == null || "".equals(refData.getId()))
			quotation.addMessage(msgController.createMsg("error.MFE", "ID"));
		if (refData.getValue() == null || "".equals(refData.getValue()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Value"));
		
		if (quotation.getMessages().isEmpty()) {

			ReferenceData refRep = referenceDataRepository.findById(refData.getId()).orElse(null);

			if (refRep != null) {
				quotation.addMessage(msgController.createMsg("error.RDAEE"));
			} else {	
				Util.initalizeUpdatedInfo(refData, msgController.getMsg("info.RDRC"));
				refData.setActiveIndicator(true);
				referenceDataRepository.save(refData);
				quotation.addMessage(msgController.createMsg("info.RDRC"));
			}
		}
		
		return quotation;
	}
	
	public QuotationResponse updateReferenceData(ReferenceData refData) {
		LOG.log(Level.INFO, "Calling AppConfig Service updateReferenceData()");
		
		QuotationResponse quotation = new QuotationResponse();
		if (refData.getGrantTo() == null || "".equals(refData.getGrantTo()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Grant To"));
		if (refData.getRefGroup() == null || "".equals(refData.getRefGroup()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Reference Group"));
		if (refData.getId() == null || "".equals(refData.getId()))
			quotation.addMessage(msgController.createMsg("error.MFE", "ID"));
		if (refData.getValue() == null || "".equals(refData.getValue()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Value"));
		
		if (quotation.getMessages().isEmpty()) {

			ReferenceData refRep = referenceDataRepository.findById(refData.getId()).orElse(null);

			if (refRep == null) {
				quotation.addMessage(msgController.createMsg("error.RDNFE"));
			} else {
				refRep.setGrantTo(refData.getGrantTo());
				refRep.setRefGroup(refData.getRefGroup());
				refRep.setValue(refData.getValue());
				refRep.setActiveIndicator(true);
				Util.initalizeUpdatedInfo(refRep, msgController.getMsg("info.RDRU"));
				referenceDataRepository.save(refRep);
				quotation.addMessage(msgController.createMsg("info.RDRU"));
			}
		}
		
		return quotation;
	}
	
	public List<String> queryAllReferenceGroup() {
	    List<String> categoryList = new ArrayList<>();
	    MongoCollection mongoCollection = mongoTemplate.getCollection("ReferenceData");
	    DistinctIterable distinctIterable = mongoCollection.distinct("refGroup",String.class);
	    MongoCursor cursor = distinctIterable.iterator();
	    while (cursor.hasNext()) {
	        String category = (String)cursor.next();
	        categoryList.add(category);
	    }
	    return categoryList;
	}
	
}
