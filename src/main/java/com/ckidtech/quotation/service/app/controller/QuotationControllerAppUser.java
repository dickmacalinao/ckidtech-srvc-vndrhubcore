package com.ckidtech.quotation.service.app.controller;

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

import com.ckidtech.quotation.service.app.service.AppUserService;
import com.ckidtech.quotation.service.core.model.AppUser;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerAppUser {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerAppUser.class.getName());
	
	@Autowired
	private AppUserService appUserService;
		
	@RequestMapping(value = "/appuser/admin/findallappusers")
	public ResponseEntity<Object> adminFindAppUsers() {
		LOG.log(Level.INFO, "Calling API /appuser/admin/findallappusers");
		return new ResponseEntity<Object>(appUserService.adminFindAllAppUsers(), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appuser/admin/searchappusers/{name}")
	public ResponseEntity<Object> adminSearchAppUsers(@PathVariable("name") String name) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/searchappusers");
		return new ResponseEntity<Object>(appUserService.adminSearchAppUsers(name), HttpStatus.OK);		
	}	

	@RequestMapping(value = "/appuser/admin/createappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> adminCreateAppUser(@RequestBody AppUser appUser) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/createappuser:" + appUser + ")");				
		return new ResponseEntity<Object>(appUserService.addAppUser(appUser), HttpStatus.CREATED);		
	}	
	
	@RequestMapping(value = "/appuser/vendor/findallappusers/{vendor}")
	public ResponseEntity<Object> vendorFindAppUsers(@PathVariable("vendor") String vendor) {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/findallappusers");
		return new ResponseEntity<Object>(appUserService.vendorFindAllAppUsers(vendor), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appuser/vendor/searchappusers/{vendor}/{name}")
	public ResponseEntity<Object> vendorSearchAppUsers(@PathVariable("vendor") String vendor, @PathVariable("name") String name) {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/searchappusers");
		return new ResponseEntity<Object>(appUserService.vendorSearchAppUsers(vendor, name), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appuser/vendor/createappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> vendorCreateAppUser(@RequestBody AppUser appUser) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/createappuser:" + appUser + ")");				
		return new ResponseEntity<Object>(appUserService.addAppUser(appUser), HttpStatus.CREATED);		
	}	
			
}
