package com.ckidtech.quotation.service.vendorhubcommon.controller;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;
import com.ckidtech.quotation.service.vendorhubcommon.service.ProductService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class VendorHubCommonControllerProduct {
	
	private static final Logger LOG = Logger.getLogger(VendorHubCommonControllerProduct.class.getName());
	
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/product/vendoradmin/listactiveproducts")
	public ResponseEntity<Object> findActiveProducts(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/listactiveproducts");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);	
		return new ResponseEntity<Object>(productService.listProducts(loginUser, true), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/listinactiveproducts")
	public ResponseEntity<Object> finInActiveProducts(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/listinactiveproducts");	

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);	
		return new ResponseEntity<Object>(productService.listProducts(loginUser, false), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/listactiveproductsbygroup")
	public ResponseEntity<Object> findActiveProductsByGroup(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/listactiveproductsbygroup");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(productService.listProductsByGroup(loginUser, true), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/listinactiveproductsbygroup")
	public ResponseEntity<Object> findInActiveProductsByGroup(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/listinactiveproductsbygroup");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(productService.listProductsByGroup(loginUser, false), HttpStatus.OK);		
	}
		
	@RequestMapping(value = "/product/vendoradmin/createproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> createProduct(@RequestHeader("authorization") String authorization, 
			@RequestBody Product product) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/createproduct:" + product + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, product.getVendorCode());		
		return new ResponseEntity<Object>(productService.addVendorProduct(loginUser, product), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/updateproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> updateProduct(@RequestHeader("authorization") String authorization,
			@RequestBody Product product) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/updateproduct:" + product + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, product.getVendorCode());				
		return new ResponseEntity<Object>(productService.updateVendorProduct(loginUser, product), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/activateproduct/{productId}", method = RequestMethod.POST)
	public ResponseEntity<Object> activateProduct(@RequestHeader("authorization") String authorization,
			@PathVariable("productId") String productId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/activateproduct/" + productId + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(productService.activateVendorProduct(loginUser, productId), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/deactivateproduct/{productId}", method = RequestMethod.POST)
	public ResponseEntity<Object> deActivateProduct(@RequestHeader("authorization") String authorization,
			@PathVariable("productId") String productId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/deactivateproduct/" + productId + ")");

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(productService.deActivateVendorProduct(loginUser, productId), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendoradmin/createproducts", method = RequestMethod.POST)
	public ResponseEntity<Object> createProducts(@RequestHeader("authorization") String authorization,
			@RequestBody Product[] products) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/createproducts:" + products + ")");	
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		
		AppUser loginUser = new AppUser(authorization);
		for(Product product : products) {
			Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, product.getVendorCode());
			quotations.add(productService.addVendorProduct(loginUser, product));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
		
	@RequestMapping(value = "/product/vendoradmin/deletevendorproduct/{productId}")
	public ResponseEntity<Object> deleteVendorProduct(@RequestHeader("authorization") String authorization,
			@PathVariable("productId") String productId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendoradmin/deletevendorproduct/" + productId);
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(productService.deleteVendorProduct(loginUser, productId), HttpStatus.OK);		
	}
	
}