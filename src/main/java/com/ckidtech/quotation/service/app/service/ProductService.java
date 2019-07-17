package com.ckidtech.quotation.service.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import com.ckidtech.quotation.service.core.controller.MessageController;
import com.ckidtech.quotation.service.core.controller.QuotationResponse;
import com.ckidtech.quotation.service.core.dao.ProductRepository;
import com.ckidtech.quotation.service.core.dao.ReferenceDataRepository;
import com.ckidtech.quotation.service.core.dao.VendorRepository;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.model.ProductGroup;
import com.ckidtech.quotation.service.core.model.ReferenceData;
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
	private ReferenceDataRepository referenceDataRepository;
	
	
	@Autowired
	private MessageController msgController;
	
	public QuotationResponse listProducts(String vendorCode, boolean flag) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service viewAllProducts()");
		QuotationResponse quotation = new QuotationResponse();	
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			quotation.setProducts(productRepository.listProducts(vendorCode, flag));
		}
				
		return quotation;
		
	}	
	
	public QuotationResponse listProductsByGroup(String vendorCode, boolean flag) {
		
		LOG.log(Level.INFO, "Calling Vendor Service viewAllProducts()");
		QuotationResponse quotation = new QuotationResponse();
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			
			List<ProductGroup> prodGroups = new ArrayList<ProductGroup>();
			ProductGroup prodGroup;
			
			@SuppressWarnings("deprecation")
			Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
			List<ReferenceData> groups =  referenceDataRepository.searchByRoleAndRefGroup(vendorCode, "ProductGroup", pageable);
			for ( ReferenceData group : groups ) {
				prodGroup = new ProductGroup();
				prodGroup.setTitle(group.getValue());				
				prodGroup.setProducts(productRepository.listProductsByGroup(vendorCode, flag, group.getValue()));
				prodGroups.add(prodGroup);
			}
			
			quotation.setProdGroups(prodGroups);
			
		}		
				
		return quotation;
		
	}
	
	public QuotationResponse searchProductsByName(String vendorCode, boolean flag, String productName) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service findByProductName()");
		QuotationResponse quotation = new QuotationResponse();	
		
		// TODO: Verify that the session vendor id is same with the vendor id from json
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			quotation.setProducts(productRepository.searchProductsByName(vendorCode, flag, productName));
		}
		
		return quotation;
		
	}	
	
	/**
	 * Create New Product
	 * @param product
	 * @return
	 */
	public QuotationResponse addVendorProduct(Product product) {		
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
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Update Vendor Product
	 * @param product
	 * @return
	 */
	public QuotationResponse updateVendorProduct(Product product) {		
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
			}
		}
		
		return quotation;
			
	}
	
	/**
	 * Delete specific Product
	 * @param productCode
	 * @return
	 */
	public QuotationResponse deleteVendorProduct(String productCode) {		
		LOG.log(Level.INFO, "Calling Vendor Service deleteVendorProduct()");	
		
		QuotationResponse quotation = new QuotationResponse();

		if ( productCode==null || "".equals(productCode) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Code"));			
		
		if( quotation.getMessages().isEmpty() ) {
			
			Product productRep = productRepository.findById(productCode).orElse(null);
				
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
		
		return quotation;
	}

	
	/**
	 * Delete all products under given vendor
	 * @param vendorCode
	 */
	public void deleteAllVendorProducts(String vendorCode) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service deleteAllVendorProducts()");
		
		// Delete active products
		QuotationResponse quotation = listProducts(vendorCode, true);		
		for (Product product : quotation.getProducts() ) {
			deleteVendorProduct(product.getId());
		}
		
		// Delete inactive products
		quotation = listProducts(vendorCode, false);		
		for (Product product : quotation.getProducts() ) {
			deleteVendorProduct(product.getId());
		}
		
	}
	
}
