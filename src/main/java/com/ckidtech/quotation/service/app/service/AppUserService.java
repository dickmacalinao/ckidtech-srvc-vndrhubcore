package com.ckidtech.quotation.service.app.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.AppUserRepository;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({"com.ckidtech.quotation.service.core.controller"})
@EnableMongoRepositories ("com.ckidtech.quotation.service.core.dao")
@Service
public class AppUserService {

	private static final Logger LOG = Logger.getLogger(AppUserService.class.getName());

	@Autowired
	private AppUserRepository appUserRepository;
	
	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private MessageController msgController;
	
	/**
	 * View all AppUser records
	 * 
	 * @return
	 */
	public List<AppUser> adminFindAllAppUsers() {

		LOG.log(Level.INFO, "Calling Vendor Service adminFindAllAppUsers()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName("", pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> adminSearchAppUsers(String name) {

		LOG.log(Level.INFO, "Calling Vendor Service adminSearchAppUsers()");	
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName(name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> vendorFindAllAppUsers(String vendor) {

		LOG.log(Level.INFO, "Calling Vendor Service vendorFindAllAppUsers()");	
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(vendor, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> vendorSearchAppUsers(String vendor, String name) {

		LOG.log(Level.INFO, "Calling Vendor Service vendorSearchAppUsers()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorSearchByName(vendor, name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public QuotationResponse addAppUser(AppUser appUser) {		
		LOG.log(Level.INFO, "Calling Product Service addAppUser()");
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		QuotationResponse quotation = new QuotationResponse();
		
		if ( appUser.getUsername()==null || "".equals(appUser.getUsername()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User Name"));	
		if ( appUser.getPassword()==null || "".equals(appUser.getPassword()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Password"));
		if ( appUser.getName()==null || "".equals(appUser.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Name"));
		
		
		if ( appUser.getRole()==null || "".equals(appUser.getRole()) ) {
			quotation.addMessage(msgController.createMsg("error.MFE", "Role"));
		} else {
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) &&
					(appUser.getVendor()==null || "".equals(appUser.getVendor())) ) 
				quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Code"));
		}
		
		
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
		
			Vendor vendorRep = vendorRepository.findById(appUser.getVendor().toUpperCase()).orElse(null);
			
			LOG.log(Level.INFO, "appUser.getRole():" + appUser.getRole());
			
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) && 
					(vendorRep==null || !vendorRep.isActiveIndicator() )) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				
				AppUser appUserRep = appUserRepository.findById(appUser.getUsername()).orElse(null);
				
				if  ( appUserRep!=null ) {
					
					if ( appUserRep.isActiveIndicator() ) {
						quotation.addMessage(msgController.createMsg("error.AUAEE"));
					} else {
						appUserRep.setActiveIndicator(true);
						appUserRep.setPassword(encoder.encode(appUser.getPassword())); //encode password
						appUserRep.setName(appUser.getName());
						appUserRep.setVendor(appUser.getVendor().toUpperCase());
						appUserRep.setRole(appUser.getRole().toUpperCase());
						Util.initalizeUpdatedInfo(appUserRep, msgController.getMsg("info.AURR"));
						appUserRepository.save(appUserRep);
						quotation.addAppUser(appUserRep);
					}
						
				} else {
					appUser.setPassword(encoder.encode(appUser.getPassword())); //encode password
					appUser.setVendor(appUser.getVendor().toUpperCase());
					appUser.setRole(appUser.getRole().toUpperCase());
					Util.initalizeCreatedInfo(appUser, msgController.getMsg("info.AURC"));					
					appUserRepository.save(appUser);
					quotation.addAppUser(appUser);
				}
				
				
				quotation.addVendor(vendorRep);
			}
		}
		
		return quotation;
			
	}
	
}
