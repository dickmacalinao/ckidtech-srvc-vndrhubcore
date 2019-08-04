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
	
	/**
	 * Get App User by ID
	 * @param id
	 * @return
	 */
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
	
	/**
	 * Use by Admin to search App User
	 * @param name
	 * @return
	 */
	public List<AppUser> adminSearchAppUsers(String name) {

		LOG.log(Level.INFO, "Calling AppUser Service adminSearchAppUsers()");	
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "vendor, role, name");
		List<AppUser> listAppUser = appUserRepository.adminSearchByName(name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * List all App User
	 * @param loginUser - Currently login user
	 * @return
	 */
	public List<AppUser> vendorFindAllAppUsers(AppUser loginUser) {

		LOG.log(Level.INFO, "Calling AppUser Service vendorFindAllAppUsers()");	
		
		Util.checkIfAlreadyActivated(loginUser);
		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(loginUser.getVendor(), pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * Method to search App User used by Vendor Admin
	 * @param loginUser - Currently login user
	 * @param name
	 * @return
	 */
	public List<AppUser> vendorSearchAppUsers(AppUser loginUser, String name) {

		LOG.log(Level.INFO, "Calling AppUser Service vendorSearchAppUsers()");	
		
		Util.checkIfAlreadyActivated(loginUser);
		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorSearchByName(loginUser.getVendor(), name, pageable);
		for(AppUser appUser : listAppUser) {
			appUser.setPassword("[PROTECTED]");
		}
		return listAppUser;

	}
	
	/**
	 * Create new AppUser
	 * @param loginUser - Currently login user
	 * @param appUser - App User object
	 * @return
	 */
	public QuotationResponse addAppUser(AppUser loginUser, AppUser appUser) {		
		
		LOG.log(Level.INFO, "Calling AppUser Service addAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
		if ( appUser.getUsername()==null || "".equals(appUser.getUsername()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User Name"));	
		if ( appUser.getPassword()==null || "".equals(appUser.getPassword()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Password"));
		if ( appUser.getName()==null || "".equals(appUser.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Name"));				
		if ( appUser.getRole()==null || "".equals(appUser.getRole()) )
			quotation.addMessage(msgController.createMsg("error.MFE", "Role"));
		if ( UserRole.ADMIN.toString().equals(loginUser.getRole()) ) {
			if ( !UserRole.ADMIN.toString().equals(appUser.getRole()) && (appUser.getVendor()==null || "".equals(appUser.getVendor()) ) )  // Vendor ID is required for Vendor and User Type
				quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));
		}
			
		
		// Proceed to creation if validation is successful
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
			
			// Verify if vendor exists and active
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) ) {				
				Vendor vendorRep = vendorRepository.findById(appUser.getVendor().toUpperCase()).orElse(null);
				if (vendorRep==null ) {
					quotation.addMessage(msgController.createMsg("error.VNFE"));
				}
			}
			
			if( quotation.getMessages().isEmpty() ) {
				
				AppUser appUserRep = appUserRepository.findById(appUser.getUsername()).orElse(null);
				
				if  ( appUserRep!=null ) {
					quotation.addMessage(msgController.createMsg("error.AUAEE"));	
				} else {
					appUser.setPassword(encoder.encode(appUser.getPassword())); //encode password
					appUser.setVendor(UserRole.ADMIN.toString().equals(loginUser.getRole()) ? appUser.getVendor() : loginUser.getVendor()); // If admin user get vendor from json, else get from current user
					appUser.setRole(appUser.getRole().toUpperCase());
					appUser.setActiveIndicator(false);
					Util.initalizeCreatedInfo(appUser, loginUser.getUsername(), msgController.getMsg("info.AURC"));					
					appUserRepository.save(appUser);
					
					appUser.setPassword("[Protected]");
					quotation.setAppUser(appUser);
					
					quotation.addMessage(msgController.createMsg("info.AURC"));
					
				}
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Update App User
	 * @param loginUser - Currently login user
	 * @param appUser - App User object
	 * @return
	 */
	public QuotationResponse updateAppUser(AppUser loginUser, AppUser appUser) {		
		LOG.log(Level.INFO, "Calling AppUser Service updateAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		// Validate mandatory fields
		if ( appUser.getUsername()==null || "".equals(appUser.getUsername()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User Name"));
		if ( appUser.getName()==null || "".equals(appUser.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Name"));				
		if ( appUser.getRole()==null || "".equals(appUser.getRole()) )
			quotation.addMessage(msgController.createMsg("error.MFE", "Role"));
		if ( UserRole.ADMIN.toString().equals(loginUser.getRole()) ) {
			if ( !UserRole.ADMIN.toString().equals(appUser.getRole()) && (appUser.getVendor()==null || "".equals(appUser.getVendor()) ) )  // Vendor ID is required for Vendor and User Type
				quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));
		}
		
		// Proceed to creation if validation is successful
		if( quotation.getMessages().isEmpty() ) {
			
			appUser.setUsername(appUser.getUsername().toUpperCase());
			
			// Verify if vendor exists and active
			if ( !UserRole.ADMIN.toString().equalsIgnoreCase(appUser.getRole()) ) {
				Vendor vendorRep = vendorRepository.findById(appUser.getVendor().toUpperCase()).orElse(null);
				if ( vendorRep==null )
					quotation.addMessage(msgController.createMsg("error.VNFE"));
			} 
			
			AppUser appUserRep = appUserRepository.findById(appUser.getUsername()).orElse(null);
			// Verify if App User exists
			if ( appUserRep==null ) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			}
			
			if( quotation.getMessages().isEmpty() ) {
				
				Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), appUserRep.getDifferences(appUser));	
				appUserRep.setActiveIndicator(appUser.isActiveIndicator());
				appUserRep.setName(appUser.getName());
				appUserRep.setVendor(UserRole.ADMIN.toString().equals(loginUser.getRole()) ? appUser.getVendor() : loginUser.getVendor()); // Vendor ID is required for Vendor and User Type
				appUserRep.setRole(appUser.getRole().toUpperCase());									
				appUserRepository.save(appUserRep);
				
				appUserRep.setPassword("[Protected]");
				quotation.setAppUser(appUserRep);	
				
				quotation.addMessage(msgController.createMsg("info.AURU"));
				
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Delete App User
	 * @param loginUser - Currently login user
	 * @param appUserId - App User ID
	 * @return
	 */
	public QuotationResponse deleteAppUser(AppUser loginUser, String appUserId) {
		LOG.log(Level.INFO, "Calling AppUser Service deleteAppUser()");

		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();

		if (appUserId == null || "".equals(appUserId))
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);

			if (appUserRep == null) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {
				
				if ( UserRole.VENDOR.toString().equals(loginUser.getRole()) && loginUser.getVendor()!=null && !loginUser.getVendor().equals(appUserRep.getVendor()) ) {
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
	 * @param loginUser - Currently login user
	 * @param appUserId - App User ID
	 * @return
	 */
	public QuotationResponse activateAppUser(AppUser loginUser, String appUserId) {
		
		LOG.log(Level.INFO, "Calling AppUser Service activateAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		if ( appUserId == null || "".equals(appUserId) )
			quotation.addMessage(msgController.createMsg("error.MFE", "AppUser ID"));

		if (quotation.getMessages().isEmpty()) {

			AppUser appUserRep = appUserRepository.findById(appUserId).orElse(null);
			
			System.out.println("***********" + appUserRep);

			if ( appUserRep == null ) {
				quotation.addMessage(msgController.createMsg("error.AUNFE"));
			} else {	
				
				if ( appUserRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.AUAAE"));
				} else {
					
					if ( UserRole.VENDOR.toString().equals(loginUser.getRole()) && loginUser.getVendor()!=null && !loginUser.getVendor().equals(appUserRep.getVendor()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					appUserRep.setActiveIndicator(true);
					Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), msgController.getMsg("info.AURA"));
					appUserRepository.save(appUserRep);
					quotation.addMessage(msgController.createMsg("info.AURA"));				
				}
				
				appUserRep.setPassword("[Protected]");

			}
			
			quotation.setAppUser(appUserRep);
			

		}
		
		return quotation;
	}
	
		
	/**
	 * DeActivate App User
	 * @param loginUser - Currently login user
	 * @param appUserId - App User ID
	 * @return
	 */
	public QuotationResponse deActivateAppUser(AppUser loginUser, String appUserId) {
		
		LOG.log(Level.INFO, "Calling AppUser Service deActivateAppUser()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
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
					
					if ( UserRole.VENDOR.toString().equals(loginUser.getRole()) && loginUser.getVendor()!=null && !loginUser.getVendor().equals(appUserRep.getVendor()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					appUserRep.setActiveIndicator(false);
					Util.initalizeUpdatedInfo(appUserRep, loginUser.getUsername(), msgController.getMsg("info.AURDA"));
					appUserRepository.save(appUserRep);
					quotation.addMessage(msgController.createMsg("info.AURDA"));				
				}
				
				appUserRep.setPassword("[Protected]");

			}
			
			quotation.setAppUser(appUserRep);

		}
		
		return quotation;
	}
	
	/**
	 * DeActivate all App User under same Vendor
	 * @param loginUser - Currently login user
	 * @param vendor - Vendor ID
	 * @return
	 */
	public void deActivateAllAppUser(AppUser loginUser, String vendor) {

		Util.checkIfAlreadyActivated(loginUser);
		
		LOG.log(Level.INFO, "Calling AppUser Service deActivateAllAppUser()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(vendor, pageable);
		for(AppUser appUser : listAppUser) {
			deActivateAppUser(loginUser, appUser.getId());
		}

	}
	
	/**
	 * Delete all App User under same Vendor
	 * @param loginUser - Currently login user
	 * @param vendor - Vendor ID
	 * @return
	 */
	public void deleteAllAppUser(AppUser loginUser, String vendor) {
		
		Util.checkIfAlreadyActivated(loginUser);

		LOG.log(Level.INFO, "Calling AppUser Service deActivateAllAppUser()");		
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		List<AppUser> listAppUser = appUserRepository.vendorFindAllAppUsers(vendor, pageable);
		for(AppUser appUser : listAppUser) {
			deleteAppUser(loginUser, appUser.getId());
		}

	}
	
	/**
	 * Should not be called in the service. This is for unit testing purposes
	 * @return
	 */
	public QuotationResponse deleteAllAppUser() {

		LOG.log(Level.INFO, "Calling Vendor Service deleteAllAppUser()");
		QuotationResponse quotation = new QuotationResponse();
		appUserRepository.deleteAll();
		quotation.addMessage(msgController.createMsg("info.AAUSD"));
		return quotation;

	}
	
	
	
}
