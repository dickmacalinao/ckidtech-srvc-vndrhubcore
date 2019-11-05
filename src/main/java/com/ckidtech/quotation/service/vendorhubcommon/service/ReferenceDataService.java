package com.ckidtech.quotation.service.vendorhubcommon.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.ProductRepository;
import com.ckidtech.quotation.service.core.dao.ReferenceDataRepository;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({ "com.ckidtech.quotation.service.core.controller" })
@EnableMongoRepositories("com.ckidtech.quotation.service.core.dao")
@Service
public class ReferenceDataService {

	private static final Logger LOG = Logger.getLogger(ReferenceDataService.class.getName());
	
	@Autowired
	private ReferenceDataRepository referenceDataRepository;
	
	@Autowired
	private VendorRepository vendorRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private MessageController msgController;
	
	public List<ReferenceData> viewAllReferenceData() {
		LOG.log(Level.INFO, "Calling AppConfig Service viewAllReferenceData()");
		return (List<ReferenceData>) referenceDataRepository.findAll();
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
	public QuotationResponse createReferenceData(AppUser loginUser, ReferenceData refData) {
		LOG.log(Level.INFO, "Calling AppConfig Service createReferenceData()");
		
		QuotationResponse quotation = new QuotationResponse();
		quotation.setProcessSuccessful(false);
		
		if (refData.getGrantTo() == null || "".equals(refData.getGrantTo()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Grant To"));
		if (refData.getRefGroup() == null || "".equals(refData.getRefGroup()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Reference Group"));
		if (refData.getValue() == null || "".equals(refData.getValue()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Value"));
		
		if (quotation.getMessages().isEmpty()) {
			
			Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
			System.out.println( refData.getGrantTo() + ":" + refData.getRefGroup());
			List<ReferenceData> listRefData = referenceDataRepository.searchByRoleAndRefGroup(refData.getGrantTo(), refData.getRefGroup(), pageable);			
			for (ReferenceData data : listRefData) {
				if ( data.getValue().equalsIgnoreCase(refData.getValue()) ) {
					quotation.addMessage(msgController.createMsg("error.RDAEE"));
				}
			}

			if (quotation.getMessages().isEmpty()) {
				Util.initalizeCreatedInfo(refData, loginUser.getUsername(), msgController.getMsg("info.RDRC"));
				refData.setId(null);
				refData.setActiveIndicator(true);
				referenceDataRepository.save(refData);		
				
				quotation.setReferenceData(refData);
				quotation.addMessage(msgController.createMsg("info.RDRC"));
				quotation.setProcessSuccessful(true);
			}
		}
		
		return quotation;
	}
	

	
	public QuotationResponse updateReferenceData(AppUser loginUser, ReferenceData refData) {
		LOG.log(Level.INFO, "Calling AppConfig Service updateReferenceData()");
		
		QuotationResponse quotation = new QuotationResponse();
		quotation.setProcessSuccessful(false);
		
		if (refData.getId() == null || "".equals(refData.getId()))
			quotation.addMessage(msgController.createMsg("error.MFE", "ID"));
		if (refData.getGrantTo() == null || "".equals(refData.getGrantTo()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Grant To"));
		if (refData.getRefGroup() == null || "".equals(refData.getRefGroup()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Reference Group"));
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
				Util.initalizeUpdatedInfo(refRep, loginUser.getUsername(), msgController.getMsg("info.RDRU"));
				referenceDataRepository.save(refRep);
				
				quotation.setReferenceData(refData);
				quotation.addMessage(msgController.createMsg("info.RDRU"));
				quotation.setProcessSuccessful(true);
			}
		}
		
		return quotation;
	}
	
	/**
	 * Delete App Config record
	 * 
	 * @return
	 */	
	public QuotationResponse deleteReferenceData(AppUser loginUser, String refId) {
		LOG.log(Level.INFO, "Calling AppConfig Service deleteReferenceData()");
		
		QuotationResponse quotation = new QuotationResponse();
		quotation.setProcessSuccessful(false);
		
		if (refId == null || "".equals(refId))
			quotation.addMessage(msgController.createMsg("error.MFE", "ID"));
		
		if (quotation.getMessages().isEmpty()) {

			ReferenceData refRep = referenceDataRepository.findById(refId).orElse(null);

			// Reference data not found.
			if (refRep == null) {
				quotation.addMessage(msgController.createMsg("error.RDNFE"));
				return quotation;
			} 
			
			// Reference data not assigned to vendor cannot be deleted.
			//if ( !"ALL".equals(grantTo) && !grantTo.equalsIgnoreCase(refRep.getGrantTo()) ) {
			//	quotation.addMessage(msgController.createMsg("error.RDCBDE"));
			//	return quotation;
			//}
			
			checkForObjectReference(quotation, loginUser.getObjectRef(), refRep.getValue());
			
			if ( quotation.getMessages().isEmpty() ) {	
				referenceDataRepository.delete(refRep);	
				quotation.addMessage(msgController.createMsg("info.RDRC"));
				quotation.setProcessSuccessful(true);
			}
			
		}
		
		return quotation;
	}	
	
	private void checkForObjectReference(QuotationResponse quotation, String grantTo, String refValue) {
		
		if ( "ALL".equalsIgnoreCase(grantTo) ) {
			
			List<Vendor> vendors = (List<Vendor>) vendorRepository.findAll();
			for (Vendor vendor : vendors) {								
				// Check if data is being reference in Product
				checkForProductReference(quotation, vendor.getId(), refValue);				
			}
			
		} else {
			
			// Check if data is being reference in Product
			checkForProductReference(quotation, grantTo, refValue);			
			
		}
		
	}
	 	
	private void checkForProductReference(QuotationResponse quotation, String vendorId, String refValue) {		

		// Retrieve references from product
		List<Product> allProducts = productRepository.listProductsByGroup(vendorId, refValue);
		
		// Reference data is referenced by products. Kindly delete those referencing object first before proceeding.
		if ( allProducts.size()>0 ) {
			quotation.addMessage(msgController.createMsg("error.RDRBPE"));
		}
		
	}
	
	/**
	 * Should not be called in the service. This is for unit testing purposes
	 * @return
	 */
	public QuotationResponse deleteAllReferenceData() {

		LOG.log(Level.INFO, "Calling Config Service deleteAllReferenceData()");
		QuotationResponse quotation = new QuotationResponse();
		referenceDataRepository.deleteAll();
		quotation.addMessage(msgController.createMsg("info.RDSD"));
		quotation.setProcessSuccessful(true);
		return quotation;

	}
	
	
	
	/*

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
	
	*/
	
}
