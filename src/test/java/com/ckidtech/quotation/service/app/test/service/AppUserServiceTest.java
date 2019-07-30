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

import com.ckidtech.quotation.service.app.service.AppUserService;
import com.ckidtech.quotation.service.app.service.VendorService;
import com.ckidtech.quotation.service.core.model.AppUser;
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
	
	public static String ADMIN_USER = "ADMIN";
	public static String TEST_VENDOR1 = "TEST-VENDOR1";
	public static String TEST_VENDOR2 = "TEST-VENDOR2";
	
	@Before
	public  void initTest() {		
		vendorService.deleteAllVendors();
		vendorService.addVendor(ADMIN_USER, new Vendor(TEST_VENDOR1, "Test Vendor 1", "Address", "9999999999", "imagelink"));
		vendorService.activateVendor(ADMIN_USER, TEST_VENDOR1);
		
		vendorService.addVendor(ADMIN_USER, new Vendor(TEST_VENDOR2, "Test Vendor 2", "Address", "9999999999", "imagelink"));
		vendorService.activateVendor(ADMIN_USER, TEST_VENDOR2);
		
		appUserService.deleteAllAppUser();
	}
	

	@Test
	public void adminFindAllAppUsersTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(ADMIN_USER, "Administrator", "password", null, "ADMIN"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR1, "VENDOR"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		
		List<AppUser> allAppUser = appUserService.adminFindAllAppUsers();
		assertEquals(3, allAppUser.size());
		
	}
	
	@Test
	public void adminSearchAppUsersTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(ADMIN_USER, "Administrator", "password", null, "ADMIN"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR1, "VENDOR"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		
		List<AppUser> allAppUser = appUserService.adminSearchAppUsers("Vendor");
		assertEquals(2, allAppUser.size());
	}
	
	
	@Test
	public void vendorFindAllAppUsersTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR1, "VENDOR"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR2, "VENDOR"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_USER", "Vendor User", "password", TEST_VENDOR2, "USER"));
		
		List<AppUser> allAppUser1 = appUserService.vendorFindAllAppUsers(TEST_VENDOR1);
		assertEquals(1, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorFindAllAppUsers(TEST_VENDOR2);
		assertEquals(1, allAppUser2.size());
	}
	
	
	@Test
	public void vendorSearchAppUsersTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR1, "VENDOR"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR2, "VENDOR"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_USER1", "Vendor User 1", "password", TEST_VENDOR2, "USER"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_USER2", "Vendor User 2", "password", TEST_VENDOR2, "USER"));
		
		List<AppUser> allAppUser1 = appUserService.vendorSearchAppUsers(TEST_VENDOR1, "Vendor");
		assertEquals(1, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorSearchAppUsers(TEST_VENDOR2, "Vendor");
		assertEquals(2, allAppUser2.size());
	}
	
	@Test
	public void addAppUserTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin", "password", TEST_VENDOR1, "VENDOR"));
		
		AppUser appUser = appUserService.getAppUserById(TEST_VENDOR1 + "_ADMIN");
		assertEquals(TEST_VENDOR1 + "_ADMIN", appUser.getId());
		assertEquals("Vendor Admin", appUser.getName());
		assertEquals(TEST_VENDOR1, appUser.getVendor());
		assertEquals("VENDOR", appUser.getRole());
		
	}
	
	@Test
	public void updateAppUserTest() {
		
		addAppUserTest();
		
		appUserService.updateAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_ADMIN", "Vendor Admin New", "password", TEST_VENDOR2, "USER"));
		
		AppUser appUser = appUserService.getAppUserById(TEST_VENDOR1 + "_ADMIN");
		assertEquals(TEST_VENDOR1 + "_ADMIN", appUser.getId());
		assertEquals("Vendor Admin New", appUser.getName());
		assertEquals(TEST_VENDOR2, appUser.getVendor());
		assertEquals("USER", appUser.getRole());
		
	}
	
	@Test
	public void deleteAppUserTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_USER1", "Vendor User 1", "password", TEST_VENDOR2, "USER"));
		
		List<AppUser> allAppUser1 = appUserService.vendorFindAllAppUsers(TEST_VENDOR1);
		assertEquals(1, allAppUser1.size());
		
		appUserService.deleteAllAppUser(TEST_VENDOR1);
		
		allAppUser1 = appUserService.vendorFindAllAppUsers(TEST_VENDOR1);
		assertEquals(0, allAppUser1.size());
		
		List<AppUser> allAppUser2 = appUserService.vendorFindAllAppUsers(TEST_VENDOR2);
		assertEquals(1, allAppUser2.size());
		
	}
	
	@Test
	public void activateAppUserTest() {
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		
		AppUser appUser = appUserService.getAppUserById(TEST_VENDOR1 + "_USER");
		assertEquals(false, appUser.isActiveIndicator());
		
		appUserService.activateAppUser(UserRole.VENDOR, TEST_VENDOR1, ADMIN_USER, TEST_VENDOR1 + "_USER");
		
		appUser = appUserService.getAppUserById(TEST_VENDOR1 + "_USER");
		assertEquals(true, appUser.isActiveIndicator());
		
	}
	
	@Test
	public void deActivateAppUserTest() {
		
		activateAppUserTest();
		
		appUserService.deActivateAppUser(UserRole.VENDOR, TEST_VENDOR1, ADMIN_USER, TEST_VENDOR1 + "_USER");
		
		AppUser appUser = appUserService.getAppUserById(TEST_VENDOR1 + "_USER");
		assertEquals(false, appUser.isActiveIndicator());
		
	}
	
	@Test
	public void deActivateAllAppUserTest() {
		
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR1 + "_USER", "Vendor User", "password", TEST_VENDOR1, "USER"));
		appUserService.addAppUser(ADMIN_USER, new AppUser(TEST_VENDOR2 + "_USER", "Vendor User", "password", TEST_VENDOR2, "USER"));
		
		AppUser appUser1 = appUserService.getAppUserById(TEST_VENDOR1 + "_USER");
		assertEquals(false, appUser1.isActiveIndicator());
		
		AppUser appUser2 = appUserService.getAppUserById(TEST_VENDOR2 + "_USER");
		assertEquals(false, appUser2.isActiveIndicator());
		
		appUserService.activateAppUser(UserRole.ADMIN, TEST_VENDOR1, ADMIN_USER, TEST_VENDOR1 + "_USER");
		appUserService.activateAppUser(UserRole.ADMIN, TEST_VENDOR2, ADMIN_USER, TEST_VENDOR2 + "_USER");
				
		appUser1 = appUserService.getAppUserById(TEST_VENDOR1 + "_USER");
		assertEquals(true, appUser1.isActiveIndicator());
		
		appUser2 = appUserService.getAppUserById(TEST_VENDOR2 + "_USER");
		assertEquals(true, appUser2.isActiveIndicator());
		
		appUserService.deActivateAllAppUser(ADMIN_USER, TEST_VENDOR1);
		
		appUser1 = appUserService.getAppUserById(TEST_VENDOR1 + "_USER");
		assertEquals(false, appUser1.isActiveIndicator());
		
		appUser2 = appUserService.getAppUserById(TEST_VENDOR2 + "_USER");
		assertEquals(true, appUser2.isActiveIndicator());
		

	}
	
}
