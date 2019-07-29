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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.app.service.ReferenceDataService;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;

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
	public ResponseEntity<Object> viewAllRefernceData(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/admin/viewallreferncedata");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(
			referenceDataService.viewAllReferenceData(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/admin/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceData(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/admin/createreferencedata");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(refData), HttpStatus.OK);		
	}
	
	
	@RequestMapping(value = "/config/admin/createreferencedatamultiple", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataMultiple(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData[] refDataMutiple) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/admin/createreferencedatamultiple");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		List<QuotationResponse> listRefData = new ArrayList<QuotationResponse>();
		for (ReferenceData refData : refDataMutiple) {
			listRefData.add(referenceDataService.createReferenceData(refData));
		}
		return new ResponseEntity<Object>(listRefData, HttpStatus.OK);		
	}
	
	// Service for VENDOR only
	
	@RequestMapping(value = "/config/vendor/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendor/createreferencedata");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendor/updatereferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> updateReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendor/updatereferencedata");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(referenceDataService.updateReferenceData(refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendor/viewreferencedatabygroup/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByVendor(@RequestHeader("authorization") String authorization,
			@PathVariable("refGroup") String refGroup) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendor/viewreferencedatabygroup");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(vendorId, refGroup), HttpStatus.OK);		
	}
	
	// Service for USER only
		
	@RequestMapping(value = "/config/user/viewreferencedatabygroup/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByUser(@RequestHeader("authorization") String authorization,
			@PathVariable("refGroup") String refGroup) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/user/viewreferencedatabygroup");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(vendorId, refGroup), HttpStatus.OK);		
	}
			
}
