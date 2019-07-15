package com.ckidtech.quotation.service.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.app.service.ReferenceDataService;
import com.ckidtech.quotation.service.core.model.ReferenceData;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerConfig {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerConfig.class.getName());
	
	@Autowired
	private ReferenceDataService referenceDataService;
	
	
	// Service open to all 
	
	@RequestMapping(value = "/config/open/getrestconnectionconfig")
	public ResponseEntity<Object> getRESTConnectionConfig() {		
		LOG.log(Level.INFO, "Calling API /config/open/getrestconnectionconfig");
		return new ResponseEntity<Object>(
			referenceDataService.viewRESTConnectionConfig(), HttpStatus.OK);		
	}
		
	
	// Services for ADMIN only
	
	@RequestMapping(value = "/config/admin/viewallreferncedata")
	public ResponseEntity<Object> viewAllRefernceData() {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewallreferncedata");
		return new ResponseEntity<Object>(
			referenceDataService.viewAllReferenceData(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/admin/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceData(@RequestBody ReferenceData refData) {		
		LOG.log(Level.INFO, "Calling API /config/admin/createreferencedata");
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/admin/createreferencedatamultiple", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataMultiple(@RequestBody ReferenceData[] refDataMutiple) {		
		LOG.log(Level.INFO, "Calling API /config/admin/createreferencedatamultiple");
		List<ReferenceData> listRefData = new ArrayList<ReferenceData>();
		for (ReferenceData refData : refDataMutiple) {
			listRefData.add(referenceDataService.createReferenceData(refData));
		}
		return new ResponseEntity<Object>(listRefData, HttpStatus.OK);		
	}
	
	// Service for VENDOR only
	
	@RequestMapping(value = "/config/vendor/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataByVendor(@RequestBody ReferenceData refData) {		
		LOG.log(Level.INFO, "Calling API /config/vendor/createreferencedata");
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendor/viewreferencedatabygroup/{vendorId}/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByVendor(@PathVariable("vendorId") String vendorId, @PathVariable("refGroup") String refGroup) {		
		LOG.log(Level.INFO, "Calling API /config/vendor/viewreferencedatabygroup");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(vendorId, refGroup), HttpStatus.OK);		
	}
	
	// Service for USER only
		
	@RequestMapping(value = "/config/user/viewreferencedatabygroup/{vendorId}/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByUser(@PathVariable("vendorId") String vendorId, @PathVariable("refGroup") String refGroup) {		
		LOG.log(Level.INFO, "Calling API /config/user/viewreferencedatabygroup");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(vendorId, refGroup), HttpStatus.OK);		
	}
			
}
