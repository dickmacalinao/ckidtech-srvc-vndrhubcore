package com.ckidtech.quotation.service.app.test.service;

import static org.junit.Assert.assertEquals;
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

import com.ckidtech.quotation.service.app.service.VendorService;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.app.service"})
@AutoConfigureDataMongo
public class VendorServiceTest {
	
	@Autowired
	VendorService vendorService;	
	
	public static String ADMIN_USER = "ADMIN";
	
	@Before
	public  void initTest() {
		vendorService.deleteAllVendors();			
	}
	
	@Test
	public void viewAllVendorsTest(){			
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST1", "Test Vendor 1", "Address", "9999999999", "imagelink"));
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST2", "Test Vendor 2", "Address", "9999999999", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(2, allVendors.size());
	}
	
	@Test
	public void searchVendorsTest() {			
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST1", "Test Vendor 1", "Address", "9999999999", "imagelink"));
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST2", "Test Vendor 2", "Address", "9999999999", "imagelink"));
		
		List<Vendor> allVendors = vendorService.searchVendors("Test Vendor");
		assertEquals(2, allVendors.size());
	}
	
	@Test
	public void viewActiveVendorsTest() {			
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST1", "Test Vendor 1", "Address", "9999999999", "imagelink"));
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST2", "Test Vendor 2", "Address", "9999999999", "imagelink"));
		vendorService.activateVendor("TESTUSER", "TEST1");
		
		List<Vendor> activeVendors = vendorService.viewActiveVendors();
		assertEquals(1, activeVendors.size());
	}
	
	@Test
	public void getVendorByIdTest() {				
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		Vendor vendor = vendorService.getVendorById("TEST");
		assertEquals("TEST", vendor.getId());
		assertEquals("Test Vendor", vendor.getName());
		assertEquals("Address", vendor.getAddress());
		assertEquals("9999999999", vendor.getContactNo());
		assertEquals("imagelink", vendor.getImgLocation());
	}

	@Test
	public void addVendorSuccessfulTest() {			
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		assertTrue("Vendor record created.", response.getMessages().contains(new ReturnMessage("Vendor record created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		Vendor vendor = allVendors.get(0);
		assertEquals("TEST", vendor.getId());
		assertEquals("Test Vendor", vendor.getName());
		assertEquals("Address", vendor.getAddress());
		assertEquals("9999999999", vendor.getContactNo());
		assertEquals("imagelink", vendor.getImgLocation());
		
	}
	
	@Test
	public void addVendorWithDuplicateTest() {
		
		addVendorSuccessfulTest();
		
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		assertEquals("Vendor already exists.", response.getMessages().get(0).getMessage());
		
	}
	
	@Test
	public void addVendorWithMissingMandatoryFieldsTest() {
		
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("", "", "", "", ""));
		assertTrue("Vendor Code is required.", response.getMessages().contains(new ReturnMessage("Vendor Code is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Name is required.", response.getMessages().contains(new ReturnMessage("Vendor Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Address is required.", response.getMessages().contains(new ReturnMessage("Vendor Address is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Contact No is required.", response.getMessages().contains(new ReturnMessage("Vendor Contact No is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void updateVendorSuccessfulTest() {
		
		addVendorSuccessfulTest();
		
		QuotationResponse response = vendorService.updateVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor New", "Address New", "9999999999 New", "imagelink New"));
		assertTrue("Vendor record updated.", response.getMessages().contains(new ReturnMessage("Vendor record updated.", ReturnMessage.MessageTypeEnum.INFO)));
		
		Vendor updatedVendor = vendorService.getVendorById("TEST");
		assertEquals("TEST", updatedVendor.getId());
		assertEquals("Test Vendor New", updatedVendor.getName());
		assertEquals("Address New", updatedVendor.getAddress());
		assertEquals("9999999999 New", updatedVendor.getContactNo());
		assertEquals("imagelink New", updatedVendor.getImgLocation());
	}
	
	@Test
	public void updateVendorWithMissingMandatoryFieldsTest() {
		
		addVendorSuccessfulTest();
		
		QuotationResponse response = vendorService.updateVendor(ADMIN_USER, new Vendor("", "", "", "", ""));
		assertTrue("Vendor Code is required.", response.getMessages().contains(new ReturnMessage("Vendor Code is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Name is required.", response.getMessages().contains(new ReturnMessage("Vendor Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Address is required.", response.getMessages().contains(new ReturnMessage("Vendor Address is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Vendor Contact No is required.", response.getMessages().contains(new ReturnMessage("Vendor Contact No is required.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void updateVendorButNoVendorFoundTest() {
		
		QuotationResponse response = vendorService.updateVendor(ADMIN_USER, new Vendor("TEST2", "Test Vendor New", "Address New", "9999999999 New", "imagelink New"));		
		assertEquals("Vendor not found.", response.getMessages().get(0).getMessage());
	}
	
	@Test
	public void deleteVendorSuccessfulTest() {			
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		vendorService.deleteVendor("TEST");
		
		List<Vendor> allVendors2 = vendorService.viewAllVendors();
		assertEquals(0, allVendors2.size());
	}
	
	@Test
	public void deleteVendorButNoVendorFoundTest() {	
		
		QuotationResponse response = vendorService.deleteVendor("TEST2");		
		assertTrue("Vendor not found.", response.getMessages().contains(new ReturnMessage("Vendor not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void activateVendorSuccessfulTest() {				
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		Vendor vendor = vendorService.getVendorById("TEST");		
		assertEquals(false, vendor.isActiveIndicator());
		
		vendorService.activateVendor(ADMIN_USER, "TEST");
		
		vendor = vendorService.getVendorById("TEST");
		assertEquals(true, vendor.isActiveIndicator());
	}
	
	@Test
	public void deActivateVendorTest() {				
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		Vendor vendor = vendorService.getVendorById("TEST");		
		assertEquals(false, vendor.isActiveIndicator());
		
		vendorService.activateVendor(ADMIN_USER, "TEST");
		
		vendor = vendorService.getVendorById("TEST");
		assertEquals(true, vendor.isActiveIndicator());
		
		vendorService.deActivateVendor(ADMIN_USER, "TEST");
		
		vendor = vendorService.getVendorById("TEST");
		assertEquals(false, vendor.isActiveIndicator());
	}
	
}
