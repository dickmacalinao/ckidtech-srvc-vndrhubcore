package com.ckidtech.quotation.service.app.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.ProductRepository;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.utils.Util;

@ComponentScan({"com.ckidtech.quotation.service.core.controller"})
@EnableMongoRepositories ("com.ckidtech.quotation.service.core.dao")
@Service
public class ProductService {

	private static final Logger LOG = Logger.getLogger(ProductService.class.getName());
		
	@Autowired
	private VendorRepository vendorRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private MessageController msgController;
	
	public QuotationResponse viewAllProducts(String vendorCode) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service viewAllProducts()");
		QuotationResponse quotation = new QuotationResponse();	
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			//quotation.addVendor(vendor);
			quotation.setProducts(productRepository.findAllProducts(vendorCode));
		}
				
		return quotation;
		
	}	
	
	public QuotationResponse viewActiveProducts(String vendorCode) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service viewActiveProducts()");
		QuotationResponse quotation = new QuotationResponse();	
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			//quotation.addVendor(vendor);
			quotation.setProducts(productRepository.findActiveProducts(vendorCode, true));
		}
		return quotation;
		
	}
	
	public QuotationResponse findByProductName(String productName) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service findByProductName()");
		QuotationResponse quotation = new QuotationResponse();	
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		quotation.setProducts(productRepository.findByProductName(productName, true));
		
		return quotation;
		
	}	
	
	public QuotationResponse addProduct(Product product) {		
		LOG.log(Level.INFO, "Calling Product Service addProduct()");
		
		QuotationResponse quotation = new QuotationResponse();
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		if ( product.getId()==null || "".equals(product.getId()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Code"));	
		if ( product.getName()==null || "".equals(product.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Name"));
		if ( product.getGroup()==null || "".equals(product.getGroup()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Group"));
		if ( product.getVendorCode()==null || "".equals(product.getVendorCode()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Code"));
		
		if( quotation.getMessages().isEmpty() ) {
		
			Vendor vendorRep = vendorRepository.findById(product.getVendorCode()).orElse(null);
			
			if ( vendorRep==null || !vendorRep.isActiveIndicator() ) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				Product productRep = productRepository.findById(product.getId()).orElse(null);
				
				if  ( productRep!=null ) {
					
					if ( productRep.isActiveIndicator() ) {
						quotation.addMessage(msgController.createMsg("error.VPAEE"));
					} else {
						productRep.setActiveIndicator(true);
						Util.initalizeUpdatedInfo(productRep, msgController.getMsg("info.VPRR"));
						productRepository.save(productRep);
						quotation.addMessage(msgController.createMsg("info.VPRR"));
						quotation.setProduct(productRep);
					}
						
				} else {
					Util.initalizeCreatedInfo(product, msgController.getMsg("info.VPRC"));
					productRepository.save(product);					
					quotation.addMessage(msgController.createMsg("info.VPRC"));
					quotation.setProduct(product);
				}				
				//quotation.addVendor(vendorRep);
			}
		}
		
		return quotation;
			
	}
	
	public QuotationResponse updateProduct(Product product) {		
		LOG.log(Level.INFO, "Calling Product Service updateProduct()");
		
		QuotationResponse quotation = new QuotationResponse();
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		if ( product.getId()==null || "".equals(product.getId()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Code"));	
		if ( product.getGroup()==null || "".equals(product.getGroup()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Group"));
		if ( product.getName()==null || "".equals(product.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Name"));
		if ( product.getVendorCode()==null || "".equals(product.getVendorCode()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Code"));
		
		if( quotation.getMessages().isEmpty() ) {
		
			Vendor vendorRep = vendorRepository.findById(product.getVendorCode()).orElse(null);
			
			if ( vendorRep==null || !vendorRep.isActiveIndicator() ) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				Product productRep = productRepository.findById(product.getId()).orElse(null);
				
				if  ( productRep==null ) {
					quotation.addMessage(msgController.createMsg("error.VPNEE"));
				} else {	
					
					if ( !productRep.isActiveIndicator() ) {
						quotation.addMessage(msgController.createMsg("error.VPNEE"));
					} else {
						productRep.setActiveIndicator(true);
						productRep.setName(product.getName());
						productRep.setGroup(product.getGroup());
						productRep.setImgLocation(product.getImgLocation());
						Util.initalizeUpdatedInfo(productRep, msgController.getMsg("info.VPRU"));
						productRepository.save(productRep);
						quotation.addMessage(msgController.createMsg("info.VPRU"));
						quotation.setProduct(productRep);
					}						
				} 				
				//quotation.addVendor(vendorRep);
			}
		}
		
		return quotation;
			
	}
	
	public QuotationResponse deleteVendorProduct(String vendorCode, String productCode) {		
		LOG.log(Level.INFO, "Calling Vendor Service deleteVendorProduct()");	
		
		QuotationResponse quotation = new QuotationResponse();
				
		if ( vendorCode==null || "".equals(vendorCode) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor Code"));	
		if ( productCode==null || "".equals(productCode) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Code"));	
		
		
		if( quotation.getMessages().isEmpty() ) {
			Vendor vendorRep = vendorRepository.findById(vendorCode).orElse(null);
			
			if ( vendorRep==null ) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				
				Product productRep = productRepository.findProduct(vendorCode, productCode);
				
				if ( productRep==null ) {
					quotation.addMessage(msgController.createMsg("error.VPNFE"));
				} else {
					if ( productRep.isActiveIndicator() ) { 
						productRep.setActiveIndicator(false);
						Util.initalizeUpdatedInfo(productRep, msgController.getMsg("info.VPRD"));				
						productRepository.save(productRep);
					} else {
						quotation.addMessage(msgController.createMsg("error.VPADE"));
					}
					
					quotation.addProduct(productRep);
					
				}
				
			}
			quotation.addVendor(vendorRep);
			
		}			
		
		return quotation;
	}

	public void deleteAllVendorProducts(String vendorCode) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service deleteAllVendorProducts()");
		QuotationResponse quotation = viewAllProducts(vendorCode);
		
		for (Product product : quotation.getProducts() ) {
			deleteVendorProduct(vendorCode, product.getId());
		}
		
	}
	
}
