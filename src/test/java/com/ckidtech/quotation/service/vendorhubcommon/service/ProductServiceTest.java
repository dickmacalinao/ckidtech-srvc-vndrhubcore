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
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.vendorhubcommon.service", "com.ckidtech.quotation.service.appuser.service"})
@AutoConfigureDataMongo
public class ProductServiceTest {
	
	@Autowired
	private VendorService vendorService;

	@Autowired
	private ProductService productService;	
	
	@Autowired
	private AppUserRepository appUserRepository;
	
	public static AppUser APP_ADMIN = new AppUser("APP_ADMIN", "Administrator", "testpass", UserRole.APP_ADMIN, "VendorHub", "", "");
	
	public static Vendor TEST_VENDOR = new Vendor("Test Vendor", "Address", "9999999999", "imagelink");	
	public static Product TEST_PRODUCT = new Product("TEST_VENDOR", "Food", "Product", "imgLocation");
	
	private String APP_ADMIN_ID = "";
	private String TEST_VENDOR_ID = "";;
	
	
	@Before
	public  void initTest() {	
		
		vendorService.deleteAllVendors();		
		productService.deleteAllProducts();
		appUserRepository.deleteAll();
		
		APP_ADMIN.setActiveIndicator(true);
		
		// Create Test Vendor
		QuotationResponse response = vendorService.addVendor(APP_ADMIN, TEST_VENDOR);
		TEST_VENDOR_ID = response.getVendor().getId();
		assertTrue("Vendor created.", response.getMessages().contains(new ReturnMessage("Vendor created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		// Create Vendor Admin User
		AppUser appUserVendorAdmin = new AppUser("USER_VENDOR_ADMIN", "Administrator", "testpass", UserRole.VENDOR_ADMIN, "VendorHub", "TEST", TEST_VENDOR_ID);
		appUserVendorAdmin.setActiveIndicator(true);
		appUserRepository.save(appUserVendorAdmin);
		APP_ADMIN_ID = appUserVendorAdmin.getId();		
		assertEquals(true, appUserVendorAdmin.isActiveIndicator());
		
	}
	
	@Test
	public void listProducts() {}
	
	@Test
	public void listProductsByGroup() {}
	
	@Test
	public void searchProductsByName() {}
	
	@Test
	public void addVendorProductSuccessful() {
		
		// Activate Test Vendor		
		QuotationResponse response = vendorService.activateVendor(APP_ADMIN, TEST_VENDOR_ID);
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
		
		response = productService.addVendorProduct(userVendorAdmin, TEST_PRODUCT);		
		assertTrue("Product created.", response.getMessages().contains(new ReturnMessage("Product created.", ReturnMessage.MessageTypeEnum.INFO)));
				
		assertNotEquals(null, response.getProduct().getId());
		assertEquals(TEST_VENDOR_ID, response.getProduct().getVendorCode());
		assertEquals("Food", response.getProduct().getGroup());
		assertEquals("Product", response.getProduct().getName());
		assertEquals("imgLocation", response.getProduct().getImgLocation());
		assertEquals(false, response.getProduct().isActiveIndicator());
		
	}
	
	@Test
	public void addVendorProductDuplicateTest() {
		
		addVendorProductSuccessful();
		
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
		
		QuotationResponse response = productService.addVendorProduct(userVendorAdmin, TEST_PRODUCT);
		assertEquals(1, response.getMessages().size());
		assertTrue("Product name already exists.", response.getMessages().contains(new ReturnMessage("Product name already exists.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void addVendorProductMissingMandatoryTest() {
		
		// Activate Test Vendor		
		QuotationResponse response = vendorService.activateVendor(APP_ADMIN, TEST_VENDOR_ID);
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
		
		response = productService.addVendorProduct(userVendorAdmin, new Product("", "", "", ""));
		assertEquals(2, response.getMessages().size());
		assertTrue("Product Name is required.", response.getMessages().contains(new ReturnMessage("Product Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Product Group is required.", response.getMessages().contains(new ReturnMessage("Product Group is required.", ReturnMessage.MessageTypeEnum.ERROR)));		
	}
	
	@Test
	public void updateVendorProductSuccessfulTest() {
		
		addVendorProductSuccessful();
		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		QuotationResponse response= productService.listProducts(userVendorAdmin, false);
		
		response = productService.updateVendorProduct(userVendorAdmin, new Product(response.getProducts().get(0).getId(), "TEST_VENDOR","Food New", "Product New", "imgLocation New"));
		assertTrue("Product updated.", response.getMessages().contains(new ReturnMessage("Product updated.", ReturnMessage.MessageTypeEnum.INFO)));
		
	}
	
	@Test
	public void updateVendorProductRecordNotFoundTest() {
		
		addVendorProductSuccessful();
		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		
		QuotationResponse response = productService.updateVendorProduct(userVendorAdmin, new Product("TEST", "TEST_VENDOR","Food", "Product", "imgLocation"));
		assertTrue("Pproduct not found.", response.getMessages().contains(new ReturnMessage("Product not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void updateVendorProductMissingMandatoryTest() {
		
		// Activate Test Vendor
		List<Vendor> vendors = vendorService.viewAllVendors();
		QuotationResponse response = vendorService.activateVendor(APP_ADMIN, vendors.get(0).getId());
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
						
		response = productService.updateVendorProduct(userVendorAdmin, new Product("", "", "", "", ""));
		assertEquals(3, response.getMessages().size());
		assertTrue("Product ID is required.", response.getMessages().contains(new ReturnMessage("Product ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Product Name is required.", response.getMessages().contains(new ReturnMessage("Product Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Product Group is required.", response.getMessages().contains(new ReturnMessage("Product Group is required.", ReturnMessage.MessageTypeEnum.ERROR)));		
	}
	
	@Test
	public void activateVendorProductSuccessfulTest() {
		
		addVendorProductSuccessful();
		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		
		QuotationResponse response = productService.listProducts(userVendorAdmin, false);
		
		response = productService.activateVendorProduct(userVendorAdmin, response.getProducts().get(0).getId());
		
		assertTrue("Product activated.", response.getMessages().contains(new ReturnMessage("Product activated.", ReturnMessage.MessageTypeEnum.INFO)));
		assertEquals(true, response.getProduct().isActiveIndicator());
	}
	
	@Test
	public void activateVendorProductNotFoundTest() {
		
		// Activate Test Vendor
		List<Vendor> vendors = vendorService.viewAllVendors();
		QuotationResponse response = vendorService.activateVendor(APP_ADMIN, vendors.get(0).getId());
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
		
		response = productService.activateVendorProduct(userVendorAdmin, "TEST");
		assertEquals(1, response.getMessages().size());
		assertTrue("Product not found.", response.getMessages().contains(new ReturnMessage("Product not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void activateVendorProductAlreadyActivatedTest() {
		
		activateVendorProductSuccessfulTest();
		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		QuotationResponse response = productService.listProducts(userVendorAdmin, true);
		
		response = productService.activateVendorProduct(userVendorAdmin, response.getProducts().get(0).getId());
		assertEquals(1, response.getMessages().size());
		assertTrue("Product is already active.", response.getMessages().contains(new ReturnMessage("Product is already active.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void deActivateVendorProductSuccessfulTest() {
		
		activateVendorProductSuccessfulTest();
		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		
		QuotationResponse response = productService.listProducts(userVendorAdmin, true);
		
		response = productService.deActivateVendorProduct(userVendorAdmin, response.getProducts().get(0).getId());
		assertEquals(1, response.getMessages().size());
		assertTrue("Product deactivated.", response.getMessages().contains(new ReturnMessage("Product deactivated.", ReturnMessage.MessageTypeEnum.INFO)));
		assertEquals(true, response.getProduct().isActiveIndicator());
	}
	
	@Test
	public void deActivateVendorProductNotFoundTest() {
		
		// Activate Test Vendor
		List<Vendor> vendors = vendorService.viewAllVendors();
		QuotationResponse response = vendorService.activateVendor(APP_ADMIN, vendors.get(0).getId());
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
		
		response = productService.deActivateVendorProduct(userVendorAdmin, "TEST");
		assertEquals(1, response.getMessages().size());
		assertTrue("Product not found.", response.getMessages().contains(new ReturnMessage("Product not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	@Test
	public void deActivateVendorProductAlreadyDeactivatedTest() {
		
		addVendorProductSuccessful();		
		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		
		QuotationResponse response = productService.listProducts(userVendorAdmin, false);
		
		response = productService.deActivateVendorProduct(userVendorAdmin, response.getProducts().get(0).getId());
		assertEquals(1, response.getMessages().size());
		assertTrue("Product is already deactivated.", response.getMessages().contains(new ReturnMessage("Product is already deactivated.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void deleteVendorProductSuccessfulTest() {
		
		addVendorProductSuccessful();	
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		
		QuotationResponse response = productService.listProducts(userVendorAdmin, false);
		
		response = productService.deleteVendorProduct(userVendorAdmin, response.getProducts().get(0).getId());
		assertTrue("Product deleted.", response.getMessages().contains(new ReturnMessage("Product deleted.", ReturnMessage.MessageTypeEnum.INFO)));
	}
	
	@Test
	public void deleteVendorProductNotFoundTest() {
		
		// Activate Test Vendor
		List<Vendor> vendors = vendorService.viewAllVendors();
		QuotationResponse response = vendorService.activateVendor(APP_ADMIN, vendors.get(0).getId());
		assertEquals(true, response.getVendor().isActiveIndicator());
				
		// Validate Vendor Admin		
		AppUser userVendorAdmin = appUserRepository.findById(APP_ADMIN_ID).orElse(null);
		assertEquals(true, userVendorAdmin.isActiveIndicator());
				
		response = productService.deleteVendorProduct(userVendorAdmin, "TEST");
		assertEquals(1, response.getMessages().size());
		assertTrue("Product not found.", response.getMessages().contains(new ReturnMessage("Product not found.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
}
