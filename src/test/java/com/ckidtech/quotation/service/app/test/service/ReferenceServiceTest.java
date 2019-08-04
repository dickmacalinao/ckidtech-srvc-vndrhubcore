package com.ckidtech.quotation.service.app.test.service;

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

import com.ckidtech.quotation.service.app.service.AppUserService;
import com.ckidtech.quotation.service.app.service.ProductService;
import com.ckidtech.quotation.service.app.service.ReferenceDataService;
import com.ckidtech.quotation.service.app.service.VendorService;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.app.service"})
@AutoConfigureDataMongo
public class ReferenceServiceTest {
		
	
	@Autowired
	private VendorService vendorService;
	
	@Autowired
	private AppUserService appUserService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ReferenceDataService referenceDataService;
	
	
	//public static AppUser ADMIN_USER = new AppUser("ADMIN", "Admin", "password", "", "ADMIN");	
	//public static AppUser VENDOR_USER = new AppUser("TEST_VENDOR", "Vendor Admin", "password", "TEST_VENDOR", "VENDOR");
	//public static String TEST_VENDOR = "TEST_VENDOR";
	
	public static AppUser USER_ADMIN = new AppUser("USER_ADMIN", "Administrator", "testpass", "", UserRole.ADMIN.toString());		
	//public static AppUser USER_VENDOR_ADMIN = new AppUser("USER_VENDOR_ADMIN", "Administrator", "testpass", "TEST_VENDOR", UserRole.VENDOR.toString());
	
	public static Vendor TEST_VENDOR = new Vendor("Test Vendor", "Address", "9999999999", "imagelink");	
	
	@Before
	public  void initTest() {
		vendorService.deleteAllVendors();
		appUserService.deleteAllAppUser();
		productService.deleteAllProducts();
		referenceDataService.deleteAllReferenceData();		
		
		USER_ADMIN.setActiveIndicator(true);
		
		// Create Test Vendor
		QuotationResponse response = vendorService.addVendor(USER_ADMIN, TEST_VENDOR);
		assertTrue("Vendor created.", response.getMessages().contains(new ReturnMessage("Vendor created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		// Create Vendor Admin User
		response = appUserService.addAppUser(USER_ADMIN, new AppUser("USER_VENDOR_ADMIN", "Administrator", "testpass", response.getVendor().getId(), UserRole.VENDOR.toString()));	
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));

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
		List<Vendor> vendors = vendorService.viewAllVendors();				
		QuotationResponse response = vendorService.activateVendor(USER_ADMIN, vendors.get(0).getId());
		Vendor testVendor = response.getVendor();
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Activate Test Vendor Admin  User
		response = appUserService.activateAppUser(USER_ADMIN, "USER_VENDOR_ADMIN");
		AppUser userVendorAdmin = response.getAppUser();
		assertEquals(true, response.getAppUser().isActiveIndicator());
	
		response = referenceDataService.createReferenceData(userVendorAdmin, 
				new ReferenceData(testVendor.getId(), "ProductGroup", "Food", true));
		
		assertTrue("Reference data record created.", response.getMessages().contains(new ReturnMessage("Reference data record created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		ReferenceData refData = response.getReferenceData();
		
		assertNotEquals(null, refData.getId());
		assertEquals(vendors.get(0).getId(), refData.getGrantTo());
		assertEquals("ProductGroup", refData.getRefGroup());
		assertEquals("Food", refData.getValue());
		assertEquals(true, refData.getDefaultFlag());
		
	}
	
	
	@Test
	public void createReferenceDataMissingMandatoryTest() {
		
		AppUser userVendorAdmin = appUserService.getAppUserById("USER_VENDOR_ADMIN");
		
		QuotationResponse response = referenceDataService.createReferenceData(userVendorAdmin, 
				new ReferenceData("", "", "", true));
		
		assertTrue("Grant To is required.", response.getMessages().contains(new ReturnMessage("Grant To is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Reference Group is required.", response.getMessages().contains(new ReturnMessage("Reference Group is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Value is required.", response.getMessages().contains(new ReturnMessage("Value is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void createReferenceDataWithDuplicateTest() {
		
		createReferenceDataSuccefulTest();
		
		List<Vendor> vendors = vendorService.viewAllVendors();
		AppUser userVendorAdmin = appUserService.getAppUserById("USER_VENDOR_ADMIN");
		
		QuotationResponse response = referenceDataService.createReferenceData(userVendorAdmin, 
				new ReferenceData(vendors.get(0).getId(), "ProductGroup", "Food", true));
		
		assertTrue("Reference data already exists.", response.getMessages().contains(new ReturnMessage("Reference data already exists.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
		
	@Test
	public void updateReferenceDataSuccessfulTest() {
		
		createReferenceDataSuccefulTest();
		
		List<Vendor> vendors = vendorService.viewAllVendors();
		AppUser userVendorAdmin = appUserService.getAppUserById("USER_VENDOR_ADMIN");
		
		List<ReferenceData> listRef = referenceDataService.viewAllReferenceData();
		
		QuotationResponse response = referenceDataService.updateReferenceData(userVendorAdmin, 
				new ReferenceData(listRef.get(0).getId(), vendors.get(0).getId(), "ProductGroupNew", "Food New", false));
		
		ReferenceData refData = response.getReferenceData();
		
		assertEquals(listRef.get(0).getId(), refData.getId());
		assertEquals(userVendorAdmin.getVendor(), refData.getGrantTo());
		assertEquals("ProductGroupNew", refData.getRefGroup());
		assertEquals("Food New", refData.getValue());
		assertEquals(false, refData.getDefaultFlag());
		
	}
	
	@Test
	public void updateReferenceDataWithMissingMandatoryTest() {
		
		AppUser userVendorAdmin = appUserService.getAppUserById("USER_VENDOR_ADMIN");
		
		QuotationResponse response = referenceDataService.updateReferenceData(userVendorAdmin, 
				new ReferenceData("", "", "", "", true));
		
		assertEquals(4, response.getMessages().size());
		assertTrue("Grant To is required.", response.getMessages().contains(new ReturnMessage("Grant To is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Reference Group is required.", response.getMessages().contains(new ReturnMessage("Reference Group is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("ID is required.", response.getMessages().contains(new ReturnMessage("ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Value is required.", response.getMessages().contains(new ReturnMessage("Value is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void updateReferenceDataNoDataFoundTest() {
		
		AppUser userVendorAdmin = appUserService.getAppUserById("USER_VENDOR_ADMIN");
		
		QuotationResponse response = referenceDataService.updateReferenceData(userVendorAdmin, 
				new ReferenceData("TESTREF", userVendorAdmin.getVendor(), "ProductGroupNew", "Food New", false));
		
		assertTrue("Reference data not found.", response.getMessages().contains(new ReturnMessage("Reference data not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void deleteReferenceDataSucessfulTest() {
		
		createReferenceDataSuccefulTest();
		
		List<Vendor> vendors = vendorService.viewAllVendors();		
		List<ReferenceData> listRef = referenceDataService.viewAllReferenceData();
		
		List<ReferenceData> allReferenceData = referenceDataService.viewAllReferenceData();
		assertEquals(1, allReferenceData.size());
		
		referenceDataService.deleteReferenceData(vendors.get(0).getId(), listRef.get(0).getId());
		
		allReferenceData = referenceDataService.viewAllReferenceData();
		assertEquals(0, allReferenceData.size());
		
	}
	
	/*  Need to finalize the logic
	@Test
	public void deleteReferenceDataWithReferenceByProductTest() {
		
		createReferenceDataSuccefulTest();
		
		List<ReferenceData> allReferenceData = referenceDataService.viewAllReferenceData();
		assertEquals(1, allReferenceData.size());
		
		AppUser userVendorAdmin = appUserService.getAppUserById("USER_VENDOR_ADMIN");
		
		//QuotationResponse response = vendorService.addVendor(ADMIN_USER, new Vendor(TEST_VENDOR, "Test Vendor", "Address", "9999999", "imgLocation"));
		//System.out.println(response.getMessages().get(0).getMessage());
		//assertTrue("Vendor record created.", response.getMessages().contains(new ReturnMessage("Vendor record created.", ReturnMessage.MessageTypeEnum.INFO)));
				
		//response = vendorService.activateVendor(ADMIN_USER, TEST_VENDOR);
		//System.out.println(response.getMessages().get(0).getMessage());
		//assertEquals(true, response.getVendor().isActiveIndicator());
		
		QuotationResponse response = productService.addVendorProduct(userVendorAdmin, new Product(userVendorAdmin.getVendor(), "Food", "Ice Tea", ""));
		//System.out.println(response.getMessages().get(0).getMessage());
		assertTrue("Vendor product record created.", response.getMessages().contains(new ReturnMessage("Vendor product record created.", ReturnMessage.MessageTypeEnum.INFO)));		
		
		response = referenceDataService.deleteReferenceData(TEST_VENDOR, TEST_VENDOR + ":ProductGroup:Food");
		assertTrue("Reference data is referenced by Product. Kindly delete those referencing object first before proceeding.", 
				response.getMessages().contains(new ReturnMessage("Reference data is referenced by Product. Kindly delete those referencing object first before proceeding.", ReturnMessage.MessageTypeEnum.ERROR)));
			
	}
	*/
	
	@Test
	public void deleteReferenceDataMissingMandatoryTest() {
		
		QuotationResponse response = referenceDataService.deleteReferenceData("", "");
		assertEquals(2, response.getMessages().size());
		assertTrue("Vendor ID is required.", response.getMessages().contains(new ReturnMessage("Vendor ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("ID is required.", response.getMessages().contains(new ReturnMessage("ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void checkForObjectReferenceTest() {}
	
	@Test 
	public void checkForProductReferenceTest() {}
	

}
