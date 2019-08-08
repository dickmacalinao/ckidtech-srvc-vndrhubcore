package com.ckidtech.quotation.service.vendorhubcommon.controller;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;
import com.ckidtech.quotation.service.vendorhubcommon.service.ReferenceDataService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class VendorHubCommonControllerConfig {
	
	private static final Logger LOG = Logger.getLogger(VendorHubCommonControllerConfig.class.getName());
	
	@Autowired
	private ReferenceDataService referenceDataService;
	
	
	// Service open to all 
	/*
	@RequestMapping(value = "/config/open/getrestconnectionconfig")
	public ResponseEntity<Object> getRESTConnectionConfig() {		
		LOG.log(Level.INFO, "Calling API /config/open/getrestconnectionconfig");
		return new ResponseEntity<Object>(
			referenceDataService.viewRESTConnectionConfig(), HttpStatus.OK);		
	}
	*/
		
	
	// Services for ADMIN only
	
	/*
	@RequestMapping(value = "/config/admin/viewallreferncedata")
	public ResponseEntity<Object> viewAllRefernceData(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewallreferncedata");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(
			referenceDataService.viewAllReferenceData(), HttpStatus.OK);		
	}
	*/
	
	@RequestMapping(value = "/config/appadmin/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceData(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/appadmin/createreferencedata");
		
		AppUser loginUser = new AppUser(authorization);		
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(loginUser, refData), HttpStatus.OK);		
	}
	
	
	@RequestMapping(value = "/config/appadmin/createreferencedatamultiple", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataMultiple(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData[] refDataMutiple) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/appadmin/createreferencedatamultiple");
		
		AppUser loginUser = new AppUser(authorization);		
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		List<QuotationResponse> listRefData = new ArrayList<QuotationResponse>();
		for (ReferenceData refData : refDataMutiple) {
			listRefData.add(referenceDataService.createReferenceData(loginUser, refData));
		}
		return new ResponseEntity<Object>(listRefData, HttpStatus.OK);		
	}
	
	// Service for VENDOR only
	
	@RequestMapping(value = "/config/vendoradmin/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/createreferencedata");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(loginUser, refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendoradmin/updatereferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> updateReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/updatereferencedata");		
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.updateReferenceData(loginUser, refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendoradmin/viewreferencedatabygroup/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByVendor(@RequestHeader("authorization") String authorization,
			@PathVariable("refGroup") String refGroup) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/viewreferencedatabygroup");

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(loginUser.getObjectRef(), refGroup), HttpStatus.OK);		
	}
	
	// Service for USER only
		
	@RequestMapping(value = "/config/vendoruser/viewreferencedatabygroup/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByUser(@RequestHeader("authorization") String authorization,
			@PathVariable("refGroup") String refGroup) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoruser/viewreferencedatabygroup");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(loginUser.getObjectRef(), refGroup), HttpStatus.OK);		
	}
			
}
