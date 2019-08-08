package com.ckidtech.quotation.service.vendorhubcommon.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({ "com.ckidtech.quotation.service.core.controller" })
@EnableMongoRepositories("com.ckidtech.quotation.service.core.dao")
@Service
public class VendorService {

	private static final Logger LOG = Logger.getLogger(VendorService.class.getName());

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private MessageController msgController;

	/**
	 * View all vendor records by Administrator user
	 * 
	 * @return
	 */
	public List<Vendor> viewAllVendors() {
		LOG.log(Level.INFO, "Calling Vendor Service viewAllVendors()");
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");		
		return vendorRepository.findByName("", pageable);
	}

	/**
	 * Search Vendor by Name
	 * 
	 * @param name
	 * @return
	 */
	public List<Vendor> searchVendors(String name) {
		LOG.log(Level.INFO, "Calling Vendor Service searchVendors()");
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		Map<String, List<Vendor>> vendors = new HashMap<String, List<Vendor>>();
		return vendorRepository.findByName(name, pageable);
	}

	/**
	 * View all active records
	 * 
	 * @return
	 */
	public List<Vendor> viewActiveVendors() {
		
		LOG.log(Level.INFO, "Calling Vendor Service viewActiveVendors()");
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
		return vendorRepository.findByActiveIndicatorAndName(true, "", pageable);
	}

	public Vendor getVendorById(String id) {
		LOG.log(Level.INFO, "Calling Vendor Service getVendorById()");
		return vendorRepository.findById(id).orElse(null);
	}

	/**
	 * Add new Vendor
	 * 
	 * @param vendor
	 * @return
	 */
	public QuotationResponse addVendor(AppUser appUser, Vendor vendor) {

		LOG.log(Level.INFO, "Calling Vendor Service addVendor()");
		
		Util.checkIfAlreadyActivated(appUser);

		QuotationResponse quotation = new QuotationResponse();

		if (vendor.getName() == null || "".equals(vendor.getName()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Name"));
		if (vendor.getAddress() == null || "".equals(vendor.getAddress()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Address"));
		if (vendor.getContactNo() == null || "".equals(vendor.getContactNo()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Contact No"));

		if ( quotation.getMessages().isEmpty() ) {
			
			Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "name");
			List<Vendor> vendors = vendorRepository.findByName(vendor.getName(), pageable);			
			if ( vendors.size()>0 ) {				
				quotation.addMessage(msgController.createMsg("error.VAEE"));
			} else {				
				Util.initalizeCreatedInfo(vendor, appUser.getUsername(), msgController.getMsg("info.VRC"));
				vendor.setActiveIndicator(false);
				vendorRepository.save(vendor);
				quotation.setVendor(vendor);
				quotation.addMessage(msgController.createMsg("info.VRC"));
			}

		}

		return quotation;

	}

	/**
	 * Update Vendor record
	 * 
	 * @param vendor
	 * @return
	 */
	public QuotationResponse updateVendor(AppUser appUser, Vendor vendor) {
		LOG.log(Level.INFO, "Calling Vendor Service updateVendor()");
		
		Util.checkIfAlreadyActivated(appUser);

		QuotationResponse quotation = new QuotationResponse();

		if (vendor.getId() == null || "".equals(vendor.getId()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));
		if (vendor.getName() == null || "".equals(vendor.getName()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Name"));
		if (vendor.getAddress() == null || "".equals(vendor.getAddress()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Address"));
		if (vendor.getContactNo() == null || "".equals(vendor.getContactNo()))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Contact No"));

		if (quotation.getMessages().isEmpty()) {

			Vendor vendorRep = vendorRepository.findById(vendor.getId()).orElse(null);

			if (vendorRep == null) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {

				Util.initalizeUpdatedInfo(vendorRep, appUser.getUsername(), vendorRep.getDifferences(vendor));				
				vendorRep.setActiveIndicator(vendor.isActiveIndicator());
				vendorRep.setName(vendor.getName());
				vendorRep.setAddress(vendor.getAddress());
				vendorRep.setContactNo(vendor.getContactNo());
				vendorRep.setImgLocation(vendor.getImgLocation());

				vendorRepository.save(vendorRep);
				quotation.addMessage(msgController.createMsg("info.VRU"));
				quotation.setVendor(vendorRep);
			}

		}

		return quotation;

	}

	/**
	 * Should not be called in the service. This is for unit testing purposes
	 * @return
	 */
	public QuotationResponse deleteAllVendors() {

		LOG.log(Level.INFO, "Calling Vendor Service deleteAllVendors()");
		QuotationResponse quotation = new QuotationResponse();
		vendorRepository.deleteAll();
		quotation.addMessage(msgController.createMsg("info.AVSD"));
		return quotation;

	}

	/**
	 * Delete vendor record
	 * 
	 * @param vendorID
	 * @return
	 */
	public QuotationResponse deleteVendor(AppUser appUser, String vendorID) {
		LOG.log(Level.INFO, "Calling Vendor Service deleteVendor()");

		Util.checkIfAlreadyActivated(appUser);
		
		QuotationResponse quotation = new QuotationResponse();

		if (vendorID == null || "".equals(vendorID))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));

		if (quotation.getMessages().isEmpty()) {

			Vendor vendorRep = vendorRepository.findById(vendorID).orElse(null);

			if (vendorRep == null) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				
				//appUserService.deleteAllAppUser(appUser, vendorID); // Delete all users under that vendor
				//productService.deleteAllVendorProducts(appUser, vendorID); // Delete all products under that vendor
				
				vendorRepository.delete(vendorRep);
				quotation.addMessage(msgController.createMsg("info.VRD"));

			}
			quotation.setVendor(vendorRep);

		}

		return quotation;
	}
	
	/**
	 * Use to Deactivate and activate vendor
	 * @param vendorID
	 * @return
	 */	
	
	public QuotationResponse activateVendor(AppUser appUser, String vendorID) {
		
		LOG.log(Level.INFO, "Calling Vendor Service activateVendor()");
		
		Util.checkIfAlreadyActivated(appUser);

		QuotationResponse quotation = new QuotationResponse();

		if (vendorID == null || "".equals(vendorID))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));

		if (quotation.getMessages().isEmpty()) {

			Vendor vendorRep = vendorRepository.findById(vendorID).orElse(null);

			if (vendorRep == null) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {	
				
				if ( vendorRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.VAAE"));
				} else {
					vendorRep.setActiveIndicator(true);
					Util.initalizeUpdatedInfo(vendorRep, appUser.getUsername(), msgController.getMsg("info.VRA"));
					vendorRepository.save(vendorRep);
					quotation.addMessage(msgController.createMsg("info.VRA"));				
				}

			}
			quotation.setVendor(vendorRep);

		}

		return quotation;
	}
	
	/**
	 * Use to Deactivate and activate vendor
	 * @param vendorID
	 * @return
	 */	
	
	public QuotationResponse deActivateVendor(AppUser appUser, String vendorID) {
		
		LOG.log(Level.INFO, "Calling Vendor Service deActivateVendor()");
		
		Util.checkIfAlreadyActivated(appUser);

		QuotationResponse quotation = new QuotationResponse();

		if (vendorID == null || "".equals(vendorID))
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));

		if (quotation.getMessages().isEmpty()) {

			Vendor vendorRep = vendorRepository.findById(vendorID).orElse(null);

			if (vendorRep == null) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {	
				
				if ( !vendorRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.VADAE"));
				} else {
					vendorRep.setActiveIndicator(false);
					Util.initalizeUpdatedInfo(vendorRep, appUser.getUsername(), msgController.getMsg("info.VRDA"));
					
					//appUserService.deActivateAllAppUser(appUser, vendorID); // Deactivate all users under that vendor
					
					vendorRepository.save(vendorRep);
					quotation.addMessage(msgController.createMsg("info.VRDA"));
				}
				

			}
			quotation.setVendor(vendorRep);

		}

		return quotation;
	}
	
}
