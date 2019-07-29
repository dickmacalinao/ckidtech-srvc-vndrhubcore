package com.ckidtech.quotation.service.app.controller;

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

import com.ckidtech.quotation.service.app.service.AppUserService;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerAppUser {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerAppUser.class.getName());
	
	@Autowired
	private AppUserService appUserService;
	
	// Admin service
		
	@RequestMapping(value = "/appuser/admin/findallappusers")
	public ResponseEntity<Object> adminFindAppUsers(@RequestHeader("authorization") String authorization) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/findallappusers");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.adminFindAllAppUsers(), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appuser/admin/searchappusers/{name}")
	public ResponseEntity<Object> adminSearchAppUsers(@RequestHeader("authorization") String authorization,
			@PathVariable("name") String name) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/searchappusers");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.adminSearchAppUsers(name), HttpStatus.OK);		
	}	

	@RequestMapping(value = "/appuser/admin/createappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> adminCreateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/createappuser:" + appUser + ")");
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.addAppUser(appUser), HttpStatus.CREATED);		
	}	
	
	@RequestMapping(value = "/appuser/admin/updateappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> adminUpdateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/updateappuser:" + appUser + ")");			
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.updateAppUser(appUser), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/admin/deleteappuser/{appuserid}")
	public ResponseEntity<Object> adminDeleteAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/deleteappuser:" + appuserid + ")");		
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.deleteAppUser(UserRole.ADMIN, null, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/admin/activateappuser/{appuserid}")
	public ResponseEntity<Object> adminActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/activateappuser:" + appuserid + ")");	
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.activateAppUser(UserRole.ADMIN, null, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/admin/deactivateappuser/{appuserid}")
	public ResponseEntity<Object> adminDeActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/deactivateappuser:" + appuserid + ")");	
		Util.checkAccessGrant(authorization, UserRole.ADMIN, null);
		return new ResponseEntity<Object>(appUserService.deActivateAppUser(UserRole.ADMIN, null, appuserid), HttpStatus.OK);		
	}
	
	// Vendor Services
	
	@RequestMapping(value = "/appuser/vendor/findallappusers")
	public ResponseEntity<Object> vendorFindAppUsers(@RequestHeader("authorization") String authorization) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/findallappusers");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(appUserService.vendorFindAllAppUsers(vendorId), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appuser/vendor/searchappusers/{name}")
	public ResponseEntity<Object> vendorSearchAppUsers(@RequestHeader("authorization") String authorization,
			@PathVariable("name") String name) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/searchappusers");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(appUserService.vendorSearchAppUsers(vendorId, name), HttpStatus.OK);		
	}	
	
	@RequestMapping(value = "/appuser/vendor/createappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> vendorCreateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/admin/createappuser:" + appUser + ")");		
		Util.checkAccessGrant(authorization, UserRole.VENDOR, appUser.getVendor());
		return new ResponseEntity<Object>(appUserService.addAppUser(appUser), HttpStatus.CREATED);		
	}	
	
	@RequestMapping(value = "/appuser/vendor/updateappuser", method = RequestMethod.POST)
	public ResponseEntity<Object> vendorUpdateAppUser(@RequestHeader("authorization") String authorization,
			@RequestBody AppUser appUser) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/updateappuser:" + appUser + ")");	
		Util.checkAccessGrant(authorization, UserRole.VENDOR, appUser.getVendor());
		return new ResponseEntity<Object>(appUserService.updateAppUser(appUser), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/vendor/deleteappuser/{appuserid}")
	public ResponseEntity<Object> vendorDeleteAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/deleteappuser:" + appuserid + ")");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(appUserService.deleteAppUser(UserRole.VENDOR, vendorId, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/vendor/activateappuser/{appuserid}")
	public ResponseEntity<Object> vendorActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/activateappuser:" + appuserid + ")");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(appUserService.activateAppUser(UserRole.VENDOR, vendorId, appuserid), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/appuser/vendor/deactivateappuser/{appuserid}")
	public ResponseEntity<Object> vendorDeActivateAppUser(@RequestHeader("authorization") String authorization,
			@PathVariable("appuserid") String appuserid) throws Exception {
		LOG.log(Level.INFO, "Calling API /appuser/vendor/deactivateappuser:" + appuserid + ")");
		Util.checkAccessGrant(authorization, UserRole.VENDOR, null);
		String vendorId = (String) Util.getClaimsValueFromToken(authorization, "vendor");
		return new ResponseEntity<Object>(appUserService.deActivateAppUser(UserRole.VENDOR, vendorId,appuserid), HttpStatus.OK);		
	}
			
}
