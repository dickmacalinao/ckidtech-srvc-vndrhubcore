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
	
	@RequestMapping(value = "/product/vendor/listactiveproducts/{vendorCode}")
	public ResponseEntity<Object> findActiveProducts(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listactiveproducts/" + vendorCode);	
		return new ResponseEntity<Object>(productService.listProducts(vendorCode, true), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/listinactiveproducts/{vendorCode}")
	public ResponseEntity<Object> finInActiveProducts(@PathVariable("vendorCode") String vendorCode) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listinactiveproducts/" + vendorCode);	
		return new ResponseEntity<Object>(productService.listProducts(vendorCode, false), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/listactiveproductsbygroup/{vendorCode}/{group}")
	public ResponseEntity<Object> findActiveProductsByGroup(@PathVariable("vendorCode") String vendorCode,
			@PathVariable("group") String group) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listactiveproductsbygroup/" + vendorCode + "/" + group);	
		return new ResponseEntity<Object>(productService.listProductsByGroup(vendorCode, true, group), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/listinactiveproductsbygroup/{vendorCode}/{group}")
	public ResponseEntity<Object> findInActiveProductsByGroup(@PathVariable("vendorCode") String vendorCode,
			@PathVariable("group") String group) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/listinactiveproductsbygroup/" + vendorCode + "/" + group);	
		return new ResponseEntity<Object>(productService.listProductsByGroup(vendorCode, false, group), HttpStatus.OK);		
	}
	

	
	@RequestMapping(value = "/product/vendor/createproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> createProduct(@RequestBody Product product) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/createproduct:" + product + ")");				
		return new ResponseEntity<Object>(productService.addVendorProduct(product), HttpStatus.CREATED);		
	}
	
	@RequestMapping(value = "/product/vendor/updateproduct", method = RequestMethod.POST)
	public ResponseEntity<Object> updateProduct(@RequestBody Product product) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/updateproduct:" + product + ")");				
		return new ResponseEntity<Object>(productService.updateVendorProduct(product), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/product/vendor/createproducts", method = RequestMethod.POST)
	public ResponseEntity<Object> createProducts(@RequestBody Product[] products) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/createproducts:" + products + ")");	
		ArrayList<QuotationResponse> quotations = new ArrayList<QuotationResponse>();  
		for(Product product : products) {
			quotations.add(productService.addVendorProduct(product));
		}
		return new ResponseEntity<Object>(quotations, HttpStatus.CREATED);		
	}
		
	@RequestMapping(value = "/product/vendor/deletevendorproduct/{productCode}")
	public ResponseEntity<Object> deleteVendorProduct(@PathVariable("productCode") String productCode) {		
		LOG.log(Level.INFO, "Calling API /product/vendor/deletevendorproduct/" + productCode);
		return new ResponseEntity<Object>(productService.deleteVendorProduct(productCode), HttpStatus.OK);		
	}
	
}