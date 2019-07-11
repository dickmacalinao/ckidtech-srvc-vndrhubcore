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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.app.service.ProductService;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class QuotationControllerProduct {
	
	private static final Logger LOG = Logger.getLogger(QuotationControllerProduct.class.getName());
	
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/product/vendor/viewallproducts/{vendorCode}")
	public ResponseEntity<Object> viewAllProducts(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/viewallproducts/" + vendorCode);	
		return new ResponseEntity<Object>(productService.viewAllProducts(vendorCode), HttpStatus.OK);		
	}
	
	
	@RequestMapping(value = "/product/vendor/viewactiveproducts/{vendorCode}")
	public ResponseEntity<Object> viewAllActiveProducts(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/viewactiveproducts/" + vendorCode);	
		return new ResponseEntity<Object>(productService.viewActiveProducts(vendorCode), HttpStatus.OK);		
	}
	
	
	@RequestMapping(value = "/product/vendor/createproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> createProduct(@RequestBody Product product) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/createproduct:" + product + ")");				
		return new ResponseEntity<Object>(productService.addProduct(product), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/product/vendor/updateproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> updateProduct(@RequestBody Product product) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/updateproduct:" + product + ")");				
		return new ResponseEntity<Object>(productService.updateProduct(product), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/createproducts", method = RequestMethod.POST)
	public ResponseEntity<Object> createProducts(@RequestBody Product[] products) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/createproducts:" + products + ")");	
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		for(Product product : products) {
			quotations.add(productService.addProduct(product));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
		
	@RequestMapping(value = "/product/vendor/deletevendorproduct/{vendorCode}/{productCode}")
	public ResponseEntity<Object> deleteVendorProduct(@PathVariable("vendorCode") String vendorCode,
			@PathVariable("productCode") String productCode) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/deleteVendorProduct/" + vendorCode);
		return new ResponseEntity<Object>(productService.deleteVendorProduct(vendorCode, productCode), HttpStatus.OK);		
	}
	
}
