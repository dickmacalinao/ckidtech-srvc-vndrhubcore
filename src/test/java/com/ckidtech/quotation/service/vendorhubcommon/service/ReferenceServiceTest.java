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
import com.ckidtech.quotation.service.core.dao.AppUserRepository;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.vendorhubcommon.service", "com.ckidtech.quotation.service.appuser.service"})
@AutoConfigureDataMongo
public class ReferenceServiceTest {
		
	
	@Autowired
	private VendorService vendorService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private AppUserRepository appUserRepository;
	
	
	@Autowired
	private ReferenceDataService referenceDataService;
	
	public static AppUser USER_ADMIN = new AppUser("USER_ADMIN", "Administrator", "testpass", UserRole.APP_ADMIN, "VendorHub", "");		
	
	public static Vendor TEST_VENDOR = new Vendor("Test Vendor", "imagelink");	
	
	private String TEST_VENDOR_ID;
	private String VENDOR_ADMIN_ID;
	
	@Before
	public  void initTest() {
		vendorService.deleteAllVendors();		
		productService.deleteAllProducts();
		appUserRepository.deleteAll();
		
		referenceDataService.deleteAllReferenceData();		
		
		USER_ADMIN.setActiveIndicator(true);
		
		// Create Test Vendor
		QuotationResponse response = vendorService.addVendor(USER_ADMIN, TEST_VENDOR);
		TEST_VENDOR_ID = response.getVendor().getId();
		assertTrue("Vendor created.", response.getMessages().contains(new ReturnMessage("Vendor created.", ReturnMessage.MessageTypeEnum.INFO)));
				
		// Create Vendor Admin User
		AppUser appUserVendorAdmin = new AppUser("USER_VENDOR_ADMIN", "Administrator", "testpass", UserRole.VENDOR_ADMIN, "VendorHub", TEST_VENDOR_ID);
		appUserVendorAdmin.setActiveIndicator(true);
		appUserRepository.save(appUserVendorAdmin);
		VENDOR_ADMIN_ID = appUserVendorAdmin.getId();		
		assertEquals(true, appUserVendorAdmin.isActiveIndicator());

	}
	
	
	@Test
	public void viewAllReferenceDataTest() {		
		List<ReferenceData> allReferenceData = referenceDataService.viewAllReferenceData();
		assertEquals(0, allReferenceData.size());		
	}
	
	@Test
	public void viewReferenceDataByRefGroupTest() {}
		
	@Test
	public void viewReferenceDataByRoleAndRefGroupTest() {}
		
	@Test
	public void createReferenceDataSuccefulTest() {
		
		// Activate Test Vendor
						
		QuotationResponse response = vendorService.activateVendor(USER_ADMIN, TEST_VENDOR_ID);
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
	
		response = referenceDataService.createReferenceData(userVendorAdmin, 
				new ReferenceData(TEST_VENDOR.getId(), "ProductGroup", "Food", "Food", true));
		
		assertTrue("Reference data record created.", response.getMessages().contains(new ReturnMessage("Reference data record created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		ReferenceData refData = response.getReferenceData();
		
		assertNotEquals(null, refData.getId());
		assertEquals(TEST_VENDOR_ID, refData.getGrantTo());
		assertEquals("ProductGroup", refData.getRefGroup());
		assertEquals("Food", refData.getName());
		assertEquals("Food", refData.getValue());
		assertEquals(true, refData.getDefaultFlag());
		
	}
	
	
	@Test
	public void createReferenceDataMissingMandatoryTest() {
		
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		QuotationResponse response = referenceDataService.createReferenceData(userVendorAdmin, 
				new ReferenceData("", "", "", "", true));
		
		assertTrue("Grant To is required.", response.getMessages().contains(new ReturnMessage("Grant To is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Reference Group is required.", response.getMessages().contains(new ReturnMessage("Reference Group is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Value is required.", response.getMessages().contains(new ReturnMessage("Value is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void createReferenceDataWithDuplicateTest() {
		
		createReferenceDataSuccefulTest();
		
		List<Vendor> vendors = vendorService.viewAllVendors();
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		QuotationResponse response = referenceDataService.createReferenceData(userVendorAdmin, 
				new ReferenceData(vendors.get(0).getId(), "ProductGroup", "Food", "Food", true));
		
		assertTrue("Reference data already exists.", response.getMessages().contains(new ReturnMessage("Reference data already exists.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
		
	@Test
	public void updateReferenceDataSuccessfulTest() {
		
		createReferenceDataSuccefulTest();
		
		List<Vendor> vendors = vendorService.viewAllVendors();
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		List<ReferenceData> listRef = referenceDataService.viewAllReferenceData();
		
		QuotationResponse response = referenceDataService.updateReferenceData(userVendorAdmin, 
				new ReferenceData(listRef.get(0).getId(), vendors.get(0).getId(), "ProductGroupNew", "Food New", "Food New", false));
		
		ReferenceData refData = response.getReferenceData();
		
		assertEquals(listRef.get(0).getId(), refData.getId());
		assertEquals(userVendorAdmin.getObjectRef(), refData.getGrantTo());
		assertEquals("ProductGroupNew", refData.getRefGroup());
		assertEquals("Food New", refData.getName());
		assertEquals("Food New", refData.getValue());
		assertEquals(false, refData.getDefaultFlag());
		
	}
	
	@Test
	public void updateReferenceDataWithMissingMandatoryTest() {
		
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		QuotationResponse response = referenceDataService.updateReferenceData(userVendorAdmin, 
				new ReferenceData("", "", "", "", true));
		
		assertEquals(5, response.getMessages().size());
		assertTrue("Grant To is required.", response.getMessages().contains(new ReturnMessage("Grant To is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Reference Group is required.", response.getMessages().contains(new ReturnMessage("Reference Group is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("ID is required.", response.getMessages().contains(new ReturnMessage("ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Name is required.", response.getMessages().contains(new ReturnMessage("Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Value is required.", response.getMessages().contains(new ReturnMessage("Value is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void updateReferenceDataNoDataFoundTest() {
		
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		QuotationResponse response = referenceDataService.updateReferenceData(userVendorAdmin, 
				new ReferenceData("TESTREF", userVendorAdmin.getObjectRef(), "ProductGroupNew", "Food New", "Food New", false));
		
		assertTrue("Reference data not found.", response.getMessages().contains(new ReturnMessage("Reference data not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void deleteReferenceDataSucessfulTest() {
		
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		createReferenceDataSuccefulTest();
		
		List<ReferenceData> listRef = referenceDataService.viewAllReferenceData();
		
		List<ReferenceData> allReferenceData = referenceDataService.viewAllReferenceData();
		assertEquals(1, allReferenceData.size());
		
		referenceDataService.deleteReferenceData(userVendorAdmin, listRef.get(0).getId());
		
		allReferenceData = referenceDataService.viewAllReferenceData();
		assertEquals(0, allReferenceData.size());
		
	}
		
	@Test
	public void deleteReferenceDataMissingMandatoryTest() {
		
		AppUser userVendorAdmin = appUserRepository.findById(VENDOR_ADMIN_ID).orElse(null);
		
		QuotationResponse response = referenceDataService.deleteReferenceData(userVendorAdmin, "");
		assertEquals(1, response.getMessages().size());
		assertTrue("ID is required.", response.getMessages().contains(new ReturnMessage("ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void checkForObjectReferenceTest() {}
	
	@Test 
	public void checkForProductReferenceTest() {}
	

}
