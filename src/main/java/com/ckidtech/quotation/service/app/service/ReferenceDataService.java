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

import com.ckidtech.quotation.service.core.dao.ReferenceDataRepository;
import com.ckidtech.quotation.service.core.model.ReferenceData;
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
	MongoTemplate mongoTemplate;

	/**
	 * View all App Config records
	 * 
	 * @return
	 */	
	public Map<String, List<ReferenceData>> viewRESTConnectionConfig() {

		LOG.log(Level.INFO, "Calling AppConfig Service viewAllRefernceData()");
		Map<String, List<ReferenceData>> refData = new HashMap<String, List<ReferenceData>>();
		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "value");
		
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
	public Map<String, List<ReferenceData>> viewAllRefernceData(String role) {

		LOG.log(Level.INFO, "Calling AppConfig Service viewAllRefernceData()");
		Map<String, List<ReferenceData>> refData = new HashMap<String, List<ReferenceData>>();			
		
		List<ReferenceData> refList;
		for ( String refGroupName : queryAllReferenceGroup() ) {			
			refList = viewReferenceDataByGroup(role, refGroupName);
			if ( !refList.isEmpty() )			
				refData.put(refGroupName, viewReferenceDataByGroup(role, refGroupName));	
		}
		
		return refData;
	}
	
	/**
	 * View all App Config records by Group
	 * 
	 * @return
	 */	
	public List<ReferenceData> viewReferenceDataByGroup(String role, String refGroup) {

		LOG.log(Level.INFO, "Calling AppConfig Service viewReferenceDataByGroup()");
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
		return referenceDataRepository.searchByRoleAndRefGroup(role, refGroup, pageable);
	}
	
	/**
	 * Create App Config record
	 * 
	 * @return
	 */	
	public ReferenceData createReferenceData(ReferenceData refData) {
		LOG.log(Level.INFO, "Calling AppConfig Service createReferenceData()");
		return referenceDataRepository.insert(refData);
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
