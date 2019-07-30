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
import com.ckidtech.quotation.service.core.exception.ServiceAccessResourceFailureException;
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
	
	public AppUser getAppUserById(String id) {
		LOG.log(Level.INFO, "Calling Vendor Service getAppUserById()");
		return appUserRepository.findById(id).orElse(null);
	}
	
	/**
	 * View all AppUser records
	 * 
	 * @return
	 */
	public List<AppUser> adminFindAllAppUsers() {

		LOG.log(Level.INFO, "Calling AppUser Service adminFindAllAppUsers()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "vendor, role, name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName("", pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> adminSearchAppUsers(String name) {

		LOG.log(Level.INFO, "Calling AppUser Service adminSearchAppUsers()");	
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "vendor, role, name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName(name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> vendorFindAllAppUsers(String vendor) {

		LOG.log(Level.INFO, "Calling AppUser Service vendorFindAllAppUsers()");	
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(vendor, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public List<AppUser> vendorSearchAppUsers(String vendor, String name) {

		LOG.log(Level.INFO, "Calling AppUser Service vendorSearchAppUsers()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorSearchByName(vendor, name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	public QuotationResponse addAppUser(String userId, AppUser appUser) {		
		
		LOG.log(Level.INFO, "Calling AppUser Service addAppUser()");
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
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
		
		// Proceed to creation if validation is successful
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
			
			// Verify if vendor exists and active
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) ) {				
				Vendor vendorRep = vendorRepository.findById(appUser.getVendor().toUpperCase()).orElse(null);
				if (vendorRep==null || !vendorRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.VNFE"));
				}
			}
			
			if( quotation.getMessages().isEmpty() ) {
				
				AppUser appUserRep = appUserRepository.findById(appUser.getUsername()).orElse(null);
				
				if  ( appUserRep!=null ) {
					quotation.addMessage(msgController.createMsg("error.AUAEE"));	
				} else {
					appUser.setPassword(encoder.encode(appUser.getPassword())); //encode password
					appUser.setVendor( appUser.getVendor()!=null ? appUser.getVendor().toUpperCase() : null);
					appUser.setRole(appUser.getRole().toUpperCase());
					appUser.setActiveIndicator(false);
					Util.initalizeCreatedInfo(appUser, userId, msgController.getMsg("info.AURC"));					
					appUserRepository.save(appUser);
					
					appUser.setPassword("[Protected]");
					quotation.setAppUser(appUser);
					
					quotation.addMessage(msgController.createMsg("info.AURC"));
					
				}
			}
		}
		
		return quotation;
			
	}
	
	public QuotationResponse updateAppUser(String userId, AppUser appUser) {		
		LOG.log(Level.INFO, "Calling AppUser Service updateAppUser()");
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
		if ( appUser.getUsername()==null || "".equals(appUser.getUsername()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User Name"));	
		//if ( appUser.getPassword()==null || "".equals(appUser.getPassword()) ) 
		//	quotation.addMessage(msgController.createMsg("error.MFE", "Password"));
		if ( appUser.getName()==null || "".equals(appUser.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Name"));
				
		if ( appUser.getRole()==null || "".equals(appUser.getRole()) ) {
			quotation.addMessage(msgController.createMsg("error.MFE", "Role"));
		} else {
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) &&
					(appUser.getVendor()==null || "".equals(appUser.getVendor())) ) 
				quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Code"));
		}
		
		// Proceed to creation if validation is successful
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
		
			Vendor vendorRep = vendorRepository.findById(appUser.getVendor().toUpperCase()).orElse(null);
			// Verify if vendor exists and active
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) && 
					(vendorRep==null || !vendorRep.isActiveIndicator() )) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} 
			
			AppUser appUserRep = appUserRepository.findById(appUser.getUsername()).orElse(null);
			// Verify if App User exists
			if ( appUserRep==null ) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			}
			
			if( quotation.getMessages().isEmpty() ) {
				
				Util.initalizeUpdatedInfo(appUserRep, userId, appUserRep.getDifferences(appUser));	
				appUserRep.setActiveIndicator(appUser.isActiveIndicator());
				appUserRep.setName(appUser.getName());
				appUserRep.setVendor(appUser.getVendor().toUpperCase());
				appUserRep.setRole(appUser.getRole().toUpperCase());									
				appUserRepository.save(appUserRep);
				
				appUserRep.setPassword("[Protected]");
				quotation.setAppUser(appUserRep);	
				
				quotation.addMessage(msgController.createMsg("info.AURU"));
				
			}
		}
		
		return quotation;
			
	}
	
	public QuotationResponse deleteAppUser(UserRole role, String vendorId, String appUserId) {
		LOG.log(Level.INFO, "Calling AppUser Service deleteAppUser()");

		QuotationResponse quotation = new QuotationResponse();

		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {
				
				if ( UserRole.VENDOR.equals(role) && vendorId!=null && !vendorId.equals(appUserRep.getVendor()) ) {
					throw new ServiceAccessResourceFailureException();
				}
			
				appUserRepository.delete(appUserRep);
				quotation.addMessage(msgController.createMsg("info.AURD"));
				appUserRep.setPassword("[Protected]");
			}
			
			quotation.setAppUser(appUserRep);

		}

		return quotation;
	}
	
	/**
	 * Activate App User
	 * @param appUserId
	 * @return
	 */
	public QuotationResponse activateAppUser(UserRole role, String vendorId, String userId, String appUserId) {
		
		LOG.log(Level.INFO, "Calling AppUser Service activateAppUser()");
		
		QuotationResponse quotation = new QuotationResponse();
		
		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {	
				
				if ( appUserRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.AUAAE"));
				} else {
					
					if ( UserRole.VENDOR.equals(role) && vendorId!=null && !vendorId.equals(appUserRep.getVendor()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					appUserRep.setActiveIndicator(true);
					Util.initalizeUpdatedInfo(appUserRep, userId, msgController.getMsg("info.AURA"));
					appUserRepository.save(appUserRep);
					quotation.addMessage(msgController.createMsg("info.AURA"));				
				}

			}
			appUserRep.setPassword("[Protected]");
			quotation.setAppUser(appUserRep);

		}
		
		return quotation;
	}
	
	
	
	
	/**
	 * Deactivate App User
	 * @param appUserId
	 * @return
	 */
	public QuotationResponse deActivateAppUser(UserRole role, String vendorId, String userId, String appUserId) {
		
		LOG.log(Level.INFO, "Calling AppUser Service deActivateAppUser()");
		
		QuotationResponse quotation = new QuotationResponse();
		
		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {	
				
				if ( !appUserRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.AUADAE"));
				} else {
					
					if ( UserRole.VENDOR.equals(role) && vendorId!=null && !vendorId.equals(appUserRep.getVendor()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					appUserRep.setActiveIndicator(false);
					Util.initalizeUpdatedInfo(appUserRep, userId, msgController.getMsg("info.AURDA"));
					appUserRepository.save(appUserRep);
					quotation.addMessage(msgController.createMsg("info.AURDA"));				
				}

			}
			appUserRep.setPassword("[Protected]");
			quotation.setAppUser(appUserRep);

		}
		
		return quotation;
	}
	
	public void deActivateAllAppUser(String userId, String vendor) {

		LOG.log(Level.INFO, "Calling AppUser Service deActivateAllAppUser()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(vendor, pageable);
		for(AppUser appUser : listAppUser) {
			deActivateAppUser(UserRole.ADMIN, appUser.getVendor(), userId, appUser.getId());
		}

	}
	
	public void deleteAllAppUser(String vendor) {

		LOG.log(Level.INFO, "Calling AppUser Service deActivateAllAppUser()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(vendor, pageable);
		for(AppUser appUser : listAppUser) {
			deleteAppUser(UserRole.ADMIN, null, appUser.getId());
		}

	}
	
	/**
	 * Delete all appuser records Should not be called
	 */
	public QuotationResponse deleteAllAppUser() {

		LOG.log(Level.INFO, "Calling Vendor Service deleteAllAppUser()");
		QuotationResponse quotation = new QuotationResponse();
		appUserRepository.deleteAll();
		quotation.addMessage(msgController.createMsg("info.AAUSD"));
		return quotation;

	}
	
	
	
}
