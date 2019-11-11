package com.ckidtech.quotation.service.vendorhubcommon.controller;

import java.util.ArrayList;
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
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;
import com.ckidtech.quotation.service.vendorhubcommon.service.VendorService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class VendorHubCommonControllerVendor {
	
	private static final Logger LOG = Logger.getLogger(VendorHubCommonControllerVendor.class.getName());
	
	@Autowired
	private VendorService vendorService;
		
	
	// Admin services
	
	@RequestMapping(value = "/vendor/appadmin/viewallvendors")
	public ResponseEntity<Object> viewAllVendors(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/viewallvendors");
		Util.checkAccessGrant(new AppUser(authorization), UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.viewAllVendors(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/viewactivevendors")
	public ResponseEntity<Object> viewActiveVendors(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/viewactivevendors");
		Util.checkAccessGrant(new AppUser(authorization), UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.viewActiveVendors(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/searchvendorbyname/{name}")
	public ResponseEntity<Object> searchVendorsByName(@RequestHeader("authorization") String authorization, 
			@PathVariable("name") String name) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/searchvendorbyname");
		Util.checkAccessGrant(new AppUser(authorization), UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.searchVendors(name), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/getvendorbyid/{id}")
	public ResponseEntity<Object> getVendorById(@RequestHeader("authorization") String authorization, 
			@PathVariable("id") String id) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/getvendorbyid");
		Util.checkAccessGrant(new AppUser(authorization), UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.getObjectById(id), HttpStatus.OK);		
	}
		
	@RequestMapping(value = "/vendor/appadmin/createvendor", method = RequestMethod.POST)
	public ResponseEntity<Object> createVendor(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor vendor) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/createvendor:" + vendor + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.addVendor(loginUser, vendor), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/createvendors", method = RequestMethod.POST)
	public ResponseEntity<Object> createVendors(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor[] vendors) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/createvendors");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		for (Vendor vendor : vendors) {
			quotations.add(vendorService.addVendor(loginUser, vendor));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/updatevendor", method = RequestMethod.POST)
	public ResponseEntity<Object> updateVendor(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor vendor) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/updatevendor:" + vendor + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.updateVendor(loginUser, vendor), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/activatevendor/{vendorCode}")
	public ResponseEntity<Object> activateVendor(@RequestHeader("authorization") String authorization, 
			@PathVariable("vendorCode") String vendorCode) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/activateevendor:" + vendorCode + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.activateVendor(loginUser, vendorCode), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/deactivatevendor/{vendorCode}")
	public ResponseEntity<Object> deActivateVendor(@RequestHeader("authorization") String authorization, 
			@PathVariable("vendorCode") String vendorCode) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/deactivateevendor:" + vendorCode + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		
		return new ResponseEntity<Object>(vendorService.deActivateVendor(loginUser, vendorCode), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/appadmin/deletevendor/{vendorCode}")
	public ResponseEntity<Object> deleteVendor(@RequestHeader("authorization") String authorization, 
			@PathVariable("vendorCode") String vendorCode) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/appadmin/deactivateevendor:" + vendorCode + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.deleteVendor(loginUser, vendorCode), HttpStatus.OK);		
	}
	
	// Vendor Services
	
	@RequestMapping(value = "/vendor/vendoradmin/getvendorbyid")
	public ResponseEntity<Object> getVendorByIdByVendor(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/vendoradmin/getvendorbyid");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(vendorService.getObjectById(loginUser.getObjectRef()), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/vendoradmin/updatevendor", method = RequestMethod.POST)
	public ResponseEntity<Object> updateVendorByVendor(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor vendor) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/vendoradmin/updatevendor:" + vendor + ")");		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, vendor.getId());
		return new ResponseEntity<Object>(vendorService.updateVendor(loginUser, vendor), HttpStatus.OK);		
	}
	
}
