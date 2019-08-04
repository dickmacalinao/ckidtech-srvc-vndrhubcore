package com.ckidtech.quotation.service.app.controller;

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
import com.ckidtech.quotation.service.app.service.ProductService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerProduct {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerProduct.class.getName());
	
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/product/vendor/listactiveproducts")
	public ResponseEntity<Object> findActiveProducts(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listactiveproducts");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);	
		return new ResponseEntity<Object>(productService.listProducts(loginUser, true), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/listinactiveproducts")
	public ResponseEntity<Object> finInActiveProducts(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listinactiveproducts");	

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);	
		return new ResponseEntity<Object>(productService.listProducts(loginUser, false), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/listactiveproductsbygroup")
	public ResponseEntity<Object> findActiveProductsByGroup(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listactiveproductsbygroup");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(productService.listProductsByGroup(loginUser, true), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/listinactiveproductsbygroup")
	public ResponseEntity<Object> findInActiveProductsByGroup(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listinactiveproductsbygroup");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(productService.listProductsByGroup(loginUser, false), HttpStatus.OK);		
	}
		
	@RequestMapping(value = "/product/vendor/createproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> createProduct(@RequestHeader("authorization") String authorization, 
			@RequestBody Product product) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/createproduct:" + product + ")");	
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, product.getVendorCode());		
		return new ResponseEntity<Object>(productService.addVendorProduct(loginUser, product), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/product/vendor/updateproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> updateProduct(@RequestHeader("authorization") String authorization,
			@RequestBody Product product) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/updateproduct:" + product + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, product.getVendorCode());				
		return new ResponseEntity<Object>(productService.updateVendorProduct(loginUser, product), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/activateproduct/{productId}", method = RequestMethod.POST)
	public ResponseEntity<Object> activateProduct(@RequestHeader("authorization") String authorization,
			@PathVariable("productId") String productId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/activateproduct/" + productId + ")");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(productService.activateVendorProduct(loginUser, productId), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/deactivateproduct/{productId}", method = RequestMethod.POST)
	public ResponseEntity<Object> deActivateProduct(@RequestHeader("authorization") String authorization,
			@PathVariable("productId") String productId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/deactivateproduct/" + productId + ")");

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(productService.deActivateVendorProduct(loginUser, productId), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/createproducts", method = RequestMethod.POST)
	public ResponseEntity<Object> createProducts(@RequestHeader("authorization") String authorization,
			@RequestBody Product[] products) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/createproducts:" + products + ")");	
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		
		AppUser loginUser = new AppUser(authorization);
		for(Product product : products) {
			Util.checkAccessGrant(loginUser, UserRole.VENDOR, product.getVendorCode());
			quotations.add(productService.addVendorProduct(loginUser, product));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
		
	@RequestMapping(value = "/product/vendor/deletevendorproduct/{productId}")
	public ResponseEntity<Object> deleteVendorProduct(@RequestHeader("authorization") String authorization,
			@PathVariable("productId") String productId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /product/vendor/deletevendorproduct/" + productId);
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR, null);
		return new ResponseEntity<Object>(productService.deleteVendorProduct(loginUser, productId), HttpStatus.OK);		
	}
	
}