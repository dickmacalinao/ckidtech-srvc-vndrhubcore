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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.app.service.VendorService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerVendor {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerVendor.class.getName());
	
	@Autowired
	private VendorService vendorService;
		
	@RequestMapping(value = "/vendor/admin/viewallvendors")
	public ResponseEntity<Object> viewAllVendors() {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/viewallvendors");
		return new ResponseEntity<Object>(vendorService.viewAllVendors(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/viewactivevendors")
	public ResponseEntity<Object> viewActiveVendors() {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/viewactivevendors");			
		return new ResponseEntity<Object>(vendorService.viewActiveVendors(), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/searchvendorbyname/{name}")
	public ResponseEntity<Object> searchVendorsByName(@PathVariable("name") String name) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/searchvendorbyname");
		return new ResponseEntity<Object>(vendorService.searchVendors(name), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/getvendorbyid/{id}")
	public ResponseEntity<Object> getVendorById(@PathVariable("id") String id) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/getvendorbyid");
		return new ResponseEntity<Object>(vendorService.getVendorById(id), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/vendor/getvendorbyid/{id}")
	public ResponseEntity<Object> getVendorByIdByVendor(@PathVariable("id") String id) {		
		LOG.log(Level.INFO, "Calling API /vendor/vendor/getvendorbyid");
		return new ResponseEntity<Object>(vendorService.getVendorById(id), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/createvendor", method = RequestMethod.POST)
	public ResponseEntity<Object> createVendor(@RequestBody Vendor vendor) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/createvendor:" + vendor + ")");
		return new ResponseEntity<Object>(vendorService.addVendor(vendor), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/vendor/admin/createvendors", method = RequestMethod.POST)
	public ResponseEntity<Object> createVendors(@RequestBody Vendor[] vendors) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/createvendors");
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		for (Vendor vendor : vendors) {
			quotations.add(vendorService.addVendor(vendor));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/vendor/admin/updatevendor", method = RequestMethod.POST)
	public ResponseEntity<Object> updateVendor(@RequestBody Vendor vendor) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/updatevendor:" + vendor + ")");		
		return new ResponseEntity<Object>(vendorService.updateVendor(vendor), HttpStatus.OK);		
	}

	@RequestMapping(value = "/vendor/vendor/updatevendor", method = RequestMethod.POST)
	public ResponseEntity<Object> updateVendorByVendor(@RequestBody Vendor vendor) {		
		LOG.log(Level.INFO, "Calling API /vendor/vendor/updatevendor:" + vendor + ")");		
		return new ResponseEntity<Object>(vendorService.updateVendor(vendor), HttpStatus.OK);		
	}

	
	@RequestMapping(value = "/vendor/admin/deletevendor/{vendorCode}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteVendor(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/deletevendor:" + vendorCode + ")");
		return new ResponseEntity<Object>(vendorService.deleteVendor(vendorCode), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/activatevendor/{vendorCode}")
	public ResponseEntity<Object> activateeVendor(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/activateevendor:" + vendorCode + ")");
		return new ResponseEntity<Object>(vendorService.activateVendor(vendorCode), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/vendor/admin/deactivatevendor/{vendorCode}")
	public ResponseEntity<Object> deActivateeVendor(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /vendor/admin/deactivateevendor:" + vendorCode + ")");
		return new ResponseEntity<Object>(vendorService.deActivateVendor(vendorCode), HttpStatus.OK);		
	}
	
	
}
