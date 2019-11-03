package com.ckidtech.quotation.service.vendorhubcommon.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.vendorhubcommon.service.VendorService;;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.vendorhubcommon.service"})
@AutoConfigureDataMongo
public class VendorServiceTest {
	
	@Autowired
	VendorService vendorService;	
	
	public static AppUser ADMIN_USER = new AppUser("ADMIN", "Administrator", "testpass", "", UserRole.APP_ADMIN, "VendorHub", "");
	
	@Before
	public  void initTest() {
		ADMIN_USER.setActiveIndicator(true);
		vendorService.deleteAllVendors();			
	}
	
	@Test
	public void viewAllVendorsTest(){			
		vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor 1", "imagelink"));
		vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor 2", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(2, allVendors.size());
	}
	
	@Test
	public void searchVendorsTest() {			
		vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor 1", "imagelink"));
		vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor 2", "imagelink"));
		
		List<Vendor> allVendors = vendorService.searchVendors("Test Vendor");
		assertEquals(2, allVendors.size());
	}
	
	@Test
	public void viewActiveVendorsTest() {			
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor 1", "imagelink"));
		vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor 2", "imagelink"));
		vendorService.activateVendor(ADMIN_USER, response.getVendor().getId());
		
		List<Vendor> activeVendors = vendorService.viewActiveVendors();
		assertEquals(1, activeVendors.size());
	}
	
	@Test
	public void getVendorByIdTest() {				
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor", "imagelink"));
		String id = response.getVendor().getId();
		
		Vendor vendor = vendorService.getVendorById(id);
		assertEquals(id, vendor.getId());
		assertEquals("Test Vendor", vendor.getName());
		assertEquals("imagelink", vendor.getImgLocation());
	}

	@Test
	public void addVendorTest() {	
		
		// Missing Mandatory fields
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("", ""));
		assertEquals(1, response.getMessages().size());
		assertTrue("Vendor Name is required.", response.getMessages().contains(new ReturnMessage("Vendor Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));		
		
		// Successful Scenario
		response = vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor", "imagelink"));
		assertTrue("Vendor created.", response.getMessages().contains(new ReturnMessage("Vendor created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		Vendor vendor = allVendors.get(0);
		
		assertNotEquals(null, vendor.getId());
		assertEquals("Test Vendor", vendor.getName());
		assertEquals("imagelink", vendor.getImgLocation());
		
		// Duplicate vendor test
		response = vendorService.addVendor(ADMIN_USER, new Vendor(vendor.getId(), "Test Vendor", "imagelink"));
		assertTrue("Vendor name already exists.", response.getMessages().contains(new ReturnMessage("Vendor name already exists.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	
	@Test
	public void updateVendorTest() {
		
		addVendorTest();
		
		// Missing Mandatory fields
		QuotationResponse response = vendorService.updateVendor(ADMIN_USER, new Vendor("", "", ""));
		assertEquals(2, response.getMessages().size());
		assertTrue("Vendor ID is required.", response.getMessages().contains(new ReturnMessage("Vendor ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Name is required.", response.getMessages().contains(new ReturnMessage("Vendor Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));		
		
		// Successful scenario		
		Vendor vendor = vendorService.viewAllVendors().get(0);
		
		response = vendorService.updateVendor(ADMIN_USER, new Vendor(vendor.getId(), "Test Vendor New", "imagelink New", 10, 20, 30));
		assertTrue("Vendor updated.", response.getMessages().contains(new ReturnMessage("Vendor updated.", ReturnMessage.MessageTypeEnum.INFO)));
		
		assertEquals("Test Vendor New", response.getVendor().getName());
		assertEquals("imagelink New", response.getVendor().getImgLocation());
		assertEquals(10, response.getVendor().getMaxSearchResult());
		assertEquals(20, response.getVendor().getMaxUserAllowed());
		assertEquals(30, response.getVendor().getMaxProductAllowed());
		
		// Vendor not found
		response = vendorService.updateVendor(ADMIN_USER, new Vendor("TEST2", "Test Vendor New", "imagelink New"));		
		assertEquals("Vendor not found.", response.getMessages().get(0).getMessage());
	}
	
	@Test
	public void deleteVendorTest() {		
		
		// Successful delete
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		vendorService.deleteVendor(ADMIN_USER, response.getVendor().getId());
		
		List<Vendor> allVendors2 = vendorService.viewAllVendors();
		assertEquals(0, allVendors2.size());
		
		
		// Vendor not found
		response = vendorService.deleteVendor(ADMIN_USER, "TEST2");		
		assertTrue("Vendor not found.", response.getMessages().contains(new ReturnMessage("Vendor not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	
	@Test
	public void activateVendorTest() {	
		
		
		// Successful activation
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor", "imagelink"));
		
		Vendor vendor = response.getVendor();		
		assertEquals(false, vendor.isActiveIndicator());
		
		vendorService.activateVendor(ADMIN_USER, vendor.getId());
		
		vendor = vendorService.getVendorById(vendor.getId());
		assertEquals(true, vendor.isActiveIndicator());
		
		
		// Vendor not found
		response = vendorService.activateVendor(ADMIN_USER, "TEST");
		assertTrue("Vendor not found.", response.getMessages().contains(new ReturnMessage("Vendor not found.", ReturnMessage.MessageTypeEnum.ERROR)));		
		
		
		// Already activated vendor
		response = vendorService.activateVendor(ADMIN_USER, vendor.getId());
		assertTrue("Vendor is already active.", response.getMessages().contains(new ReturnMessage("Vendor is already active.", ReturnMessage.MessageTypeEnum.ERROR)));		
	}
		
	@Test
	public void deActivateVendorTest() {				
		
		// Successful de-activation
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("Test Vendor", "imagelink"));
		
		Vendor vendor = vendorService.getVendorById(response.getVendor().getId());		
		assertEquals(false, vendor.isActiveIndicator());
		
		vendorService.activateVendor(ADMIN_USER, vendor.getId());
		
		vendor = vendorService.getVendorById(vendor.getId());
		assertEquals(true, vendor.isActiveIndicator());
		
		vendorService.deActivateVendor(ADMIN_USER, vendor.getId());
		
		vendor = vendorService.getVendorById(vendor.getId());
		assertEquals(false, vendor.isActiveIndicator());
		
		
		// Vendor not found
		response = vendorService.deActivateVendor(ADMIN_USER, "TEST");		
		assertTrue("Vendor not found.", response.getMessages().contains(new ReturnMessage("Vendor not found.", ReturnMessage.MessageTypeEnum.ERROR)));
		
		
		// Vendor already deactivated
		response = vendorService.deActivateVendor(ADMIN_USER, vendor.getId());			
		assertTrue("Vendor is already deactivated.", response.getMessages().contains(new ReturnMessage("Vendor is already deactivated.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
}
