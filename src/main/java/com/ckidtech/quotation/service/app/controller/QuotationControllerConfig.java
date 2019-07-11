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
import com.ckidtech.quotation.service.core.security.UserRole;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerConfig {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerConfig.class.getName());
	
	@Autowired
	private ReferenceDataService referenceDataService;
	
	
	@RequestMapping(value = "/config/open/getrestconnectionconfig")
	public ResponseEntity<Object> getRESTConnectionConfig() {		
		LOG.log(Level.INFO, "Calling API /config/open/getrestconnectionconfig");
		return new ResponseEntity<Object>(
			referenceDataService.viewRESTConnectionConfig(), HttpStatus.OK);		
	}
		
	
	@RequestMapping(value = "/config/admin/viewallreferncedata")
	public ResponseEntity<Object> viewAllRefernceDataByAdmin() {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewallreferncedata");
		return new ResponseEntity<Object>(
			referenceDataService.viewAllRefernceData(UserRole.ADMIN.toString()), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/admin/viewreferencedatabygroup")
	public ResponseEntity<Object> viewReferenceDataByGroupByAdmin(@PathVariable("group") String group) {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewreferencedatabygroup");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByGroup(UserRole.ADMIN.toString(), group), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendor/viewallreferncedata")
	public ResponseEntity<Object> viewAllRefernceDataByVendor() {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewallreferncedata");
		return new ResponseEntity<Object>(
			referenceDataService.viewAllRefernceData(UserRole.VENDOR.toString()), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendor/viewreferencedatabygroup")
	public ResponseEntity<Object> viewReferenceDataByGroupByVendor(@PathVariable("group") String group) {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewreferencedatabygroup");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByGroup(UserRole.VENDOR.toString(), group), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/user/viewallreferncedata")
	public ResponseEntity<Object> viewAllRefernceDataByUser() {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewallreferncedata");
		return new ResponseEntity<Object>(
			referenceDataService.viewAllRefernceData(UserRole.USER.toString()), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/user/viewreferencedatabygroup")
	public ResponseEntity<Object> viewReferenceDataByGroupByUser(@PathVariable("group") String group) {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewreferencedatabygroup");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByGroup(UserRole.USER.toString(), group), HttpStatus.OK);		
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
			
}
