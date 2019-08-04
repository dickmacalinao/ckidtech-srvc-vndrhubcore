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

import com.ckidtech.quotation.service.app.service.AppUserService;
import com.ckidtech.quotation.service.app.service.VendorService;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.ReturnMessage;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
@ComponentScan({"com.ckidtech.quotation.service.app.service"})
@AutoConfigureDataMongo
public class AppUserServiceTest {
	
	@Autowired
	VendorService vendorService;	
	
	@Autowired
	AppUserService appUserService;
	
	public static AppUser MAIN_ADMIN = new AppUser("MAIN_ADMIN", "Administrator", "testpass", "", UserRole.ADMIN.toString());
	public static AppUser ADMIN_USER = new AppUser("ADMIN", "Administrator", "testpass", "", UserRole.ADMIN.toString());
	public static Vendor TEST_VENDOR1 = new Vendor("TEST_VENDOR1", "Test Vendor 1", "Address", "9999999999", "imagelink");
	public static Vendor TEST_VENDOR2 = new Vendor("TEST_VENDOR2", "Test Vendor 2", "Address", "9999999999", "imagelink");
	
	@Before
	public  void initTest() {		
		
		MAIN_ADMIN.setActiveIndicator(true);
		
		vendorService.deleteAllVendors();
		vendorService.addVendor(MAIN_ADMIN, TEST_VENDOR1);
		vendorService.activateVendor(MAIN_ADMIN, "TEST_VENDOR1");
		
		vendorService.addVendor(MAIN_ADMIN, TEST_VENDOR2);
		vendorService.activateVendor(MAIN_ADMIN, "TEST_VENDOR2");
		
		appUserService.deleteAllAppUser();
		
		
	}
	

	@Test
	public void adminFindAllAppUsersTest() {
		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, ADMIN_USER);
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_ADMIN", "Vendor Admin", "password", "TEST_VENDOR1", "VENDOR"));
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "Vendor User", "password", "TEST_VENDOR1", "USER"));
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		List<AppUser> allAppUser = appUserService.adminFindAllAppUsers();
		assertEquals(3, allAppUser.size());
		
	}
	
	@Test
	public void adminSearchAppUsersTest() {
		
		appUserService.addAppUser(MAIN_ADMIN, ADMIN_USER);
		ADMIN_USER.setActiveIndicator(true);
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_ADMIN", "Vendor Admin", "password", "TEST_VENDOR1", "VENDOR"));
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "Vendor User", "password", "TEST_VENDOR1", "USER"));
		
		List<AppUser> allAppUser = appUserService.adminSearchAppUsers("Vendor");
		assertEquals(2, allAppUser.size());
	}
	
	
	@Test
	public void vendorFindAllAppUsersTest() {		
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "Vendor 1 Admin", "password", TEST_VENDOR1.getId(), "VENDOR");
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin);
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "Vendor User", "password", TEST_VENDOR1.getId(), "USER"));
		appUserVendor1Admin.setActiveIndicator(true);
		
		AppUser appUserVendor2Admin = new AppUser("TEST_VENDOR2_ADMIN", "Vendor 2 Admin", "password", TEST_VENDOR2.getId(), "VENDOR");
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor2Admin);
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER", "Vendor User", "password", TEST_VENDOR2.getId(), "USER"));
		appUserVendor2Admin.setActiveIndicator(true);
		
		List<AppUser> allAppUser1 = appUserService.vendorFindAllAppUsers(appUserVendor1Admin);
		assertEquals(1, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorFindAllAppUsers(appUserVendor2Admin);
		assertEquals(1, allAppUser2.size());
	}
	
	
	@Test
	public void vendorSearchAppUsersTest() {
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "Vendor Admin", "password", "TEST_VENDOR1", "VENDOR");
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin);
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "Vendor User", "password", "TEST_VENDOR1", "USER"));
		appUserVendor1Admin.setActiveIndicator(true);
		
		AppUser appUserVendor2Admin = new AppUser(TEST_VENDOR2.getId() + "_ADMIN", "Vendor Admin", "password", "TEST_VENDOR2", "VENDOR");		
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor2Admin);
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER1", "Vendor User 1", "password", "TEST_VENDOR2", "USER"));
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER2", "Vendor User 2", "password", "TEST_VENDOR2", "USER"));
		appUserVendor2Admin.setActiveIndicator(true);
		
		List<AppUser> allAppUser1 = appUserService.vendorSearchAppUsers(appUserVendor1Admin, "Vendor");
		assertEquals(1, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorSearchAppUsers(appUserVendor2Admin, "Vendor");
		assertEquals(2, allAppUser2.size());
	}
	
	@Test
	public void addAppUserSuccessfulTest() {		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR_ADMIN", "Vendor Admin", "password", "TEST_VENDOR1", "VENDOR"));
		assertTrue("User created.", response.getMessages().contains(new ReturnMessage("User created.", ReturnMessage.MessageTypeEnum.INFO)));
		
		AppUser appUser = appUserService.getAppUserById("TEST_VENDOR_ADMIN");
		assertEquals("TEST_VENDOR_ADMIN", appUser.getId());
		assertEquals("Vendor Admin", appUser.getName());
		assertEquals("TEST_VENDOR1", appUser.getVendor());
		assertEquals("VENDOR", appUser.getRole());
		
	}
	
	@Test
	public void addAppUserWithDuplicateTest() {		
		addAppUserSuccessfulTest();		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR_ADMIN", "Vendor Admin", "password", "TEST_VENDOR1", "VENDOR"));		
		assertTrue("User already exists.", response.getMessages().contains(new ReturnMessage("User already exists.", ReturnMessage.MessageTypeEnum.ERROR)));		
	}
	
	@Test
	public void addAppUserWithMissingMandatoryFieldsTest() {		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, new AppUser("", "", "", "", ""));		
		assertTrue("User Name is required.", response.getMessages().contains(new ReturnMessage("User Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Password is required.", response.getMessages().contains(new ReturnMessage("Password is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Name is required.", response.getMessages().contains(new ReturnMessage("Name is required.", ReturnMessage.MessageTypeEnum.ERROR)));
		assertTrue("Role is required.", response.getMessages().contains(new ReturnMessage("Role is required.", ReturnMessage.MessageTypeEnum.ERROR)));		
	}
	
	@Test
	public void addAppUserWithMissingMandatoryVendorCodeTest() {		
		QuotationResponse response = appUserService.addAppUser(MAIN_ADMIN, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin", "password", "", "VENDOR"));		
		assertTrue("Vendor ID is required.", response.getMessages().contains(new ReturnMessage("Vendor ID is required.", ReturnMessage.MessageTypeEnum.ERROR)));
	}
	
	
	@Test
	public void updateAppUserSuccessfulTest() {
		
		addAppUserSuccessfulTest();
		
		QuotationResponse response = appUserService.updateAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR_ADMIN", "Vendor Admin New", "password", "TEST_VENDOR2", "USER"));
		assertTrue("User updated.", response.getMessages().contains(new ReturnMessage("User updated.", ReturnMessage.MessageTypeEnum.INFO)));
		
		AppUser appUser = appUserService.getAppUserById("TEST_VENDOR_ADMIN");
		assertEquals("TEST_VENDOR_ADMIN", appUser.getId());
		assertEquals("Vendor Admin New", appUser.getName());
		assertEquals("TEST_VENDOR2", appUser.getVendor());
		assertEquals("USER", appUser.getRole());
		
	}
	
	@Test
	public void updateAppUseruserNotFoundTest() {
		
		QuotationResponse response = appUserService.updateAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_ADMIN", "Vendor Admin New", "password", "TEST_VENDOR2", "USER"));
		assertTrue("User not found.", response.getMessages().contains(new ReturnMessage("User not found.", ReturnMessage.MessageTypeEnum.ERROR)));
		
	}
	
	@Test
	public void deleteAppUserSuccessfulTest() {
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "Vendor 1 Admin", "password", "TEST_VENDOR1", "ADMIN");
		AppUser appUserVendor1User = new AppUser("TEST_VENDOR1_USER", "Vendor 2 User", "password", "TEST_VENDOR1", "USER");
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin);
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor1User);
		appUserVendor1Admin.setActiveIndicator(true);
		
		AppUser appUserVendor2Admin = new AppUser("TEST_VENDOR2_ADMIN", "Vendor 2 Admin", "password", "TEST_VENDOR2", "ADMIN");
		AppUser appUserVendor2User = new AppUser("TEST_VENDOR2_USER", "Vendor 2 User", "password", "TEST_VENDOR2", "USER");
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor2Admin);
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor2User);
		appUserVendor2Admin.setActiveIndicator(true);
		
		List<AppUser> allAppUser1 = appUserService.vendorFindAllAppUsers(appUserVendor1Admin);
		assertEquals(1, allAppUser1.size());
		
		appUserService.deleteAllAppUser(MAIN_ADMIN, "TEST_VENDOR1");
		
		allAppUser1 = appUserService.vendorFindAllAppUsers(appUserVendor1Admin);
		assertEquals(0, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorFindAllAppUsers(appUserVendor2Admin);
		assertEquals(1, allAppUser2.size());
		
	}
	
	@Test
	public void activateAppUserSucessfulTest() {
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "Vendor 1 Admin", "password", "TEST_VENDOR1", "ADMIN");
		AppUser appUserVendor1User = new AppUser("TEST_VENDOR1_USER", "Vendor 2 User", "password", "TEST_VENDOR1", "USER");
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor1Admin);
		appUserService.addAppUser(MAIN_ADMIN, appUserVendor1User);
		appUserVendor1Admin.setActiveIndicator(true);
		
		AppUser appUser = appUserService.getAppUserById("TEST_VENDOR1_USER");
		assertEquals(false, appUser.isActiveIndicator());
		
		appUserService.activateAppUser(appUserVendor1Admin, "TEST_VENDOR1_USER");
		
		appUser = appUserService.getAppUserById("TEST_VENDOR1_USER");
		assertEquals(true, appUser.isActiveIndicator());
		
	}
	
	@Test
	public void deActivateAppUserSuccessfulTest() {
		
		activateAppUserSucessfulTest();
		
		AppUser appUserVendor1Admin = new AppUser("TEST_VENDOR1_ADMIN", "Vendor 1 Admin", "password", "TEST_VENDOR1", "ADMIN");
		appUserVendor1Admin.setActiveIndicator(true);
		
		appUserService.deActivateAppUser(appUserVendor1Admin, "TEST_VENDOR1_USER");
		
		AppUser appUser = appUserService.getAppUserById("TEST_VENDOR1_USER");
		assertEquals(false, appUser.isActiveIndicator());
		
	}
	
	@Test
	public void deActivateAllAppUserToOneVendorOnlyTest() {
		
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR1_USER", "Vendor User", "password", "TEST_VENDOR1", "USER"));
		appUserService.addAppUser(MAIN_ADMIN, new AppUser("TEST_VENDOR2_USER", "Vendor User", "password", "TEST_VENDOR2", "USER"));
		
		AppUser appUser1 = appUserService.getAppUserById("TEST_VENDOR1_USER");
		assertEquals(false, appUser1.isActiveIndicator());
		
		AppUser appUser2 = appUserService.getAppUserById("TEST_VENDOR2_USER");
		assertEquals(false, appUser2.isActiveIndicator());
		
		appUserService.activateAppUser(MAIN_ADMIN, "TEST_VENDOR1_USER");
		appUserService.activateAppUser(MAIN_ADMIN, "TEST_VENDOR2_USER");
				
		appUser1 = appUserService.getAppUserById("TEST_VENDOR1_USER");
		assertEquals(true, appUser1.isActiveIndicator());
		
		appUser2 = appUserService.getAppUserById("TEST_VENDOR2_USER");
		assertEquals(true, appUser2.isActiveIndicator());
		
		appUserService.deActivateAllAppUser(MAIN_ADMIN, "TEST_VENDOR1");
		
		appUser1 = appUserService.getAppUserById("TEST_VENDOR1_USER");
		assertEquals(false, appUser1.isActiveIndicator());
		
		appUser2 = appUserService.getAppUserById("TEST_VENDOR2_USER");
		assertEquals(true, appUser2.isActiveIndicator());
		

	}
	
}
