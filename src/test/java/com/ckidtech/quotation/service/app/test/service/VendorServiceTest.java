package com.ckidtech.quotation.service.app.test.service;

import static org.junit.Assert.assertEquals;

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
	public void addVendorTest() {			
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
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
		
		addVendorTest();
		
		QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		assertEquals("Vendor already exists.", response.getMessages().get(0).getMessage());
		
	}
	
	@Test
	public void updateVendorTest() {
		
		addVendorTest();
		
		vendorService.updateVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor New", "Address New", "9999999999 New", "imagelink New"));
		
		Vendor updatedVendor = vendorService.getVendorById("TEST");
		assertEquals("TEST", updatedVendor.getId());
		assertEquals("Test Vendor New", updatedVendor.getName());
		assertEquals("Address New", updatedVendor.getAddress());
		assertEquals("9999999999 New", updatedVendor.getContactNo());
		assertEquals("imagelink New", updatedVendor.getImgLocation());
	}
	
	@Test
	public void deleteVendorTest() {			
		vendorService.addVendor(ADMIN_USER, new Vendor("TEST", "Test Vendor", "Address", "9999999999", "imagelink"));
		
		List<Vendor> allVendors = vendorService.viewAllVendors();
		assertEquals(1, allVendors.size());
		
		vendorService.deleteVendor("TEST");
		
		List<Vendor> allVendors2 = vendorService.viewAllVendors();
		assertEquals(0, allVendors2.size());
	}
	
	@Test
	public void activateVendorTest() {				
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
