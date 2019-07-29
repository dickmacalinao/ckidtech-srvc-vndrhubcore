package com.ckidtech.quotation.service.app.controller;

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
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.utils.Util;
import com.ckidtech.quotation.service.app.service.VendorService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerVendor {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerVendor.class.getName());
	
	@Autowired
	private VendorService vendorService;
		
	
	// Admin services
	
	@RequestMapping(value = "/vendor/admin/viewallvendors")
	public ResponseEntity<Object> viewAllVendors(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/viewallvendors");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.viewAllVendors(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/viewactivevendors")
	public ResponseEntity<Object> viewActiveVendors(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/viewactivevendors");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.viewActiveVendors(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/searchvendorbyname/{name}")
	public ResponseEntity<Object> searchVendorsByName(@RequestHeader("authorization") String authorization, 
			@PathVariable("name") String name) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/searchvendorbyname");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.searchVendors(name), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/getvendorbyid/{id}")
	public ResponseEntity<Object> getVendorById(@RequestHeader("authorization") String authorization, 
			@PathVariable("id") String id) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/getvendorbyid");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.getVendorById(id), HttpStatus.OK);		
	}
	

	
	@RequestMapping(value = "/vendor/admin/createvendor", method = RequestMethod.POST)
	public ResponseEntity<Object> createVendor(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor vendor) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/createvendor:" + vendor + ")");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.addVendor(vendor), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/vendor/admin/createvendors", method = RequestMethod.POST)
	public ResponseEntity<Object> createVendors(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor[] vendors) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/createvendors");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		for (Vendor vendor : vendors) {
			quotations.add(vendorService.addVendor(vendor));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/vendor/admin/updatevendor", method = RequestMethod.POST)
	public ResponseEntity<Object> updateVendor(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor vendor) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/updatevendor:" + vendor + ")");	
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.updateVendor(vendor), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/activatevendor/{vendorCode}")
	public ResponseEntity<Object> activateeVendor(@RequestHeader("authorization") String authorization, 
			@PathVariable("vendorCode") String vendorCode) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/activateevendor:" + vendorCode + ")");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.activateVendor(vendorCode), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/deactivatevendor/{vendorCode}")
	public ResponseEntity<Object> deActivateeVendor(@RequestHeader("authorization") String authorization, 
			@PathVariable("vendorCode") String vendorCode) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/deactivateevendor:" + vendorCode + ")");
		Util.checkAccessGrant(authorization, "ADMIN", "");
		return new ResponseEntity<Object>(vendorService.deActivateVendor(vendorCode), HttpStatus.OK);		
	}
	
	// Vendor Services
	
	@RequestMapping(value = "/vendor/vendor/getvendorbyid")
	public ResponseEntity<Object> getVendorByIdByVendor(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/vendor/getvendorbyid");
		Util.checkAccessGrant(authorization, "VENDOR", null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(vendorService.getVendorById(vendorId), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/vendor/updatevendor", method = RequestMethod.POST)
	public ResponseEntity<Object> updateVendorByVendor(@RequestHeader("authorization") String authorization, 
			@RequestBody Vendor vendor) throws Exception {		
		LOG.log(Level.INFO, "Calling API /vendor/vendor/updatevendor:" + vendor + ")");		
		Util.checkAccessGrant(authorization, "VENDOR", vendor.getId());
		return new ResponseEntity<Object>(vendorService.updateVendor(vendor), HttpStatus.OK);		
	}
	
}
