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
	
	// Admin service
		
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
	
	@RequestMapping(value = "/appuser/admin/updateappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> adminUpdateAppUser(@RequestBody AppUser appUser) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/updateappuser:" + appUser + ")");				
		return new ResponseEntity<Object>(appUserService.updateAppUser(appUser), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/admin/deleteappuser/{appuserid}")
	public ResponseEntity<Object> adminDeleteAppUser(@PathVariable("appuserid") String appuserid) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/deleteappuser:" + appuserid + ")");				
		return new ResponseEntity<Object>(appUserService.deleteAppUser(appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/admin/activateappuser/{appuserid}")
	public ResponseEntity<Object> adminActivateAppUser(@PathVariable("appuserid") String appuserid) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/activateappuser:" + appuserid + ")");				
		return new ResponseEntity<Object>(appUserService.activateAppUser(appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/admin/deactivateappuser/{appuserid}")
	public ResponseEntity<Object> adminDeActivateAppUser(@PathVariable("appuserid") String appuserid) {
		LOG.log(Level.INFO, "Calling API /appuser/admin/deactivateappuser:" + appuserid + ")");				
		return new ResponseEntity<Object>(appUserService.deActivateAppUser(appuserid), HttpStatus.OK);		
	}
	
	// Vendor Services
	
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
	
	@RequestMapping(value = "/appuser/vendor/updateappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> vendorUpdateAppUser(@RequestBody AppUser appUser) {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/updateappuser:" + appUser + ")");				
		return new ResponseEntity<Object>(appUserService.updateAppUser(appUser), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/vendor/deleteappuser/{appuserid}")
	public ResponseEntity<Object> vendorDeleteAppUser(@PathVariable("appuserid") String appuserid) {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/deleteappuser:" + appuserid + ")");				
		return new ResponseEntity<Object>(appUserService.deleteAppUser(appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/vendor/activateappuser/{appuserid}")
	public ResponseEntity<Object> vendorActivateAppUser(@PathVariable("appuserid") String appuserid) {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/activateappuser:" + appuserid + ")");				
		return new ResponseEntity<Object>(appUserService.activateAppUser(appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/vendor/deactivateappuser/{appuserid}")
	public ResponseEntity<Object> vendorDeActivateAppUser(@PathVariable("appuserid") String appuserid) {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/deactivateappuser:" + appuserid + ")");				
		return new ResponseEntity<Object>(appUserService.deActivateAppUser(appuserid), HttpStatus.OK);		
	}
			
}
