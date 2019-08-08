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
import com.ckidtech.quotation.service.core.exception.ServiceAccessResourceFailureException;
import com.ckidtech.quotation.service.core.model.AppUser;
import com.ckidtech.quotation.service.core.model.Product;
import com.ckidtech.quotation.service.core.model.ProductGroup;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.model.Vendor;
import com.ckidtech.quotation.service.core.security.UserRole;
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
	
	public QuotationResponse listProducts(AppUser loginUser, boolean flag) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service listProducts(" + loginUser + "," + flag +")");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();		
		validateVendor(quotation, loginUser.getObjectRef());					
		
		quotation.setProducts(productRepository.listProducts(loginUser.getObjectRef(), flag));		
				
		return quotation;
		
	}	
	
	public QuotationResponse listProductsByGroup(AppUser loginUser, boolean flag) {
		
		LOG.log(Level.INFO, "Calling Vendor Service listProductsByGroup(" + loginUser + "," + flag + ")");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();		
		validateVendor(quotation, loginUser.getObjectRef());	
			
		List<ProductGroup> prodGroups = new ArrayList<ProductGroup>();
		ProductGroup prodGroup;
		
		@SuppressWarnings("deprecation")
		Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
		List<ReferenceData> groups =  referenceDataRepository.searchByRoleAndRefGroup(loginUser.getObjectRef(), "ProductGroup", pageable);
		int index = 0;
		for ( ReferenceData group : groups ) {
			prodGroup = new ProductGroup();
			prodGroup.setTitle(group.getValue());			
			prodGroup.setKey(group.getValue() + index);
			prodGroup.setProducts(productRepository.listProductsByGroup(loginUser.getObjectRef(), flag, group.getValue()));
			prodGroups.add(prodGroup);
			index++;
		}
		
		quotation.setProdGroups(prodGroups);
				
		return quotation;
		
	}
	
	public QuotationResponse searchProductsByName(AppUser loginUser, boolean flag, String productName) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service searchProductsByName()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();		
		validateVendor(quotation, loginUser.getObjectRef());
		
		quotation.setProducts(productRepository.searchProductsByName(loginUser.getObjectRef(), flag, productName));
		
		return quotation;
		
	}	
	
	/**
	 * Create New Product
	 * @param product
	 * @return
	 */
	public QuotationResponse addVendorProduct(AppUser loginUser, Product product) {		
		LOG.log(Level.INFO, "Calling Product Service addVendorProduct()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();		
		
		if ( product.getName()==null || "".equals(product.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Name"));
		if ( product.getGroup()==null || "".equals(product.getGroup()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Group"));
		
		validateVendor(quotation, loginUser.getObjectRef());
		
		if( quotation.getMessages().isEmpty() ) {					

			List<Product> products = productRepository.searchProductsByNameOnly(loginUser.getObjectRef(), product.getName());
			
			if  ( products.size()>0 ) {
				quotation.addMessage(msgController.createMsg("error.VPAEE"));						
			} else {
				Util.initalizeCreatedInfo(product, loginUser.getUsername(), msgController.getMsg("info.VPRC"));
				product.setActiveIndicator(false);
				product.setVendorCode(loginUser.getObjectRef());
				productRepository.save(product);					
				quotation.addMessage(msgController.createMsg("info.VPRC"));
				quotation.setProduct(product);
			}	
		}
		
		return quotation;
			
	}
	
	/**
	 * Update Vendor Product
	 * @param product
	 * @return
	 */
	public QuotationResponse updateVendorProduct(AppUser loginUser, Product product) {		
		LOG.log(Level.INFO, "Calling Product Service updateVendorProduct()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		if ( product.getId()==null || "".equals(product.getId()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product ID"));	
		if ( product.getGroup()==null || "".equals(product.getGroup()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Group"));
		if ( product.getName()==null || "".equals(product.getName()) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product Name"));
		
		validateVendor(quotation, loginUser.getObjectRef());
		
		if( quotation.getMessages().isEmpty() ) {
			
			Product productRep = productRepository.findById(product.getId()).orElse(null);
			
			if  ( productRep==null ) {
				quotation.addMessage(msgController.createMsg("error.VPNFE"));
			} else {
				
				// Check if same Name exists
				List<Product> listPord = productRepository.searchProductsByNameOnly(loginUser.getObjectRef(), product.getName());				
				for ( Product prod : listPord ) {
					if ( prod.getName().equalsIgnoreCase(product.getName()) && prod.getId().equalsIgnoreCase(product.getId()) ) {
						quotation.addMessage(msgController.createMsg("error.VPAEE"));
					}					
				}
				
				if( quotation.getMessages().isEmpty() ) {
					productRep.setName(product.getName());
					productRep.setGroup(product.getGroup());
					productRep.setVendorCode(loginUser.getObjectRef());
					productRep.setImgLocation(product.getImgLocation());
					productRep.setProdComp(product.getProdComp());
					
					Util.initalizeUpdatedInfo(productRep, loginUser.getUsername(), msgController.getMsg("info.VPRU"));
					productRepository.save(productRep);
					quotation.addMessage(msgController.createMsg("info.VPRU"));
					quotation.setProduct(productRep);
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
	public QuotationResponse activateVendorProduct(AppUser loginUser, String productId) {		
		LOG.log(Level.INFO, "Calling Product Service activateVendorProduct()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();

		if ( productId==null || "".equals(productId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product ID"));
		
		validateVendor(quotation, loginUser.getObjectRef());
		
		if( quotation.getMessages().isEmpty() ) {
			
			Product productRep = productRepository.findById(productId).orElse(null);
			
			if  ( productRep==null ) {
				quotation.addMessage(msgController.createMsg("error.VPNFE"));
			} else {
				
				if ( loginUser.getObjectRef()!=null && !loginUser.getObjectRef().equals(productRep.getVendorCode()) ) {
					throw new ServiceAccessResourceFailureException();
				}
				
				if ( productRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.VPAAE"));
				} else {
					productRep.setActiveIndicator(true);						
					Util.initalizeUpdatedInfo(productRep, loginUser.getUsername(), msgController.getMsg("info.VPRA"));
					productRepository.save(productRep);
					quotation.addMessage(msgController.createMsg("info.VPRA"));
					quotation.setProduct(productRep);
					
				}
				
				
			} 	
			
		}
		
		return quotation;
			
	}
	
	public QuotationResponse deActivateVendorProduct(AppUser loginUser, String productId) {		
		LOG.log(Level.INFO, "Calling Product Service deActivateVendorProduct()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();
		
		if ( productId==null || "".equals(productId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product ID"));
		
		validateVendor(quotation, loginUser.getObjectRef());
		
		if( quotation.getMessages().isEmpty() ) {
			
			Product productRep = productRepository.findById(productId).orElse(null);
			
			if  ( productRep==null ) {
				quotation.addMessage(msgController.createMsg("error.VPNFE"));
			} else {
				
				if ( loginUser.getObjectRef()!=null && !loginUser.getObjectRef().equals(productRep.getVendorCode()) ) {
					throw new ServiceAccessResourceFailureException();
				}
				
				if ( !productRep.isActiveIndicator() ) {
					quotation.addMessage(msgController.createMsg("error.VPADAE"));
				} else {
					productRep.setActiveIndicator(true);						
					Util.initalizeUpdatedInfo(productRep, loginUser.getUsername(), msgController.getMsg("info.VPRDA"));
					productRepository.save(productRep);
					quotation.addMessage(msgController.createMsg("info.VPRDA"));
					quotation.setProduct(productRep);						
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
	public QuotationResponse deleteVendorProduct(AppUser loginUser, String productCode) {		
		LOG.log(Level.INFO, "Calling Vendor Service deleteVendorProduct()");	
		
		Util.checkIfAlreadyActivated(loginUser);
		
		QuotationResponse quotation = new QuotationResponse();

		if ( productCode==null || "".equals(productCode) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product ID"));	
		
		validateVendor(quotation, loginUser.getObjectRef());
		
		if( quotation.getMessages().isEmpty() ) {
			
			Product productRep = productRepository.findById(productCode).orElse(null);
				
			if ( productRep==null ) {
				quotation.addMessage(msgController.createMsg("error.VPNFE"));
			} else {
				if ( UserRole.VENDOR_ADMIN.equals(loginUser.getRole()) && loginUser.getObjectRef()!=null && !loginUser.getObjectRef().equals(productRep.getVendorCode()) ) {
					throw new ServiceAccessResourceFailureException();
				}
				
				productRepository.delete(productRep);
				quotation.addMessage(msgController.createMsg("info.VPRD"));
				
			}
		}			
		
		return quotation;
	}

	
	/**
	 * Delete all products under given vendor
	 * @param vendorCode
	 */
	public void deleteAllVendorProducts(AppUser loginUser, String vendorCode) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service deleteAllVendorProducts()");
		
		Util.checkIfAlreadyActivated(loginUser);
		
		// Delete active products
		QuotationResponse quotation = listProducts(loginUser, true);		
		for (Product product : quotation.getProducts() ) {
			deleteVendorProduct(loginUser, product.getId());
		}
		
		// Delete inactive products
		quotation = listProducts(loginUser, false);		
		for (Product product : quotation.getProducts() ) {
			deleteVendorProduct(loginUser, product.getId());
		}
		
	}
	
	
	/**
	 * Should not be called in the service. This is for unit testing purposes
	 * @return
	 */
	public QuotationResponse deleteAllProducts() {

		LOG.log(Level.INFO, "Calling Vendor Service deleteAllProducts()");
		QuotationResponse quotation = new QuotationResponse();
		productRepository.deleteAll();
		quotation.addMessage(msgController.createMsg("info.AVPSD"));
		return quotation;

	}
	
	private void validateVendor(QuotationResponse quotation, String vendorId) {
		
		if ( vendorId!=null && !"".equals(vendorId) ) {
			Vendor vendor = vendorRepository.findById(vendorId).orElse(null);		
			if ( vendor==null ) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				Util.checkIfAlreadyActivated(vendor);
			}
		}
			
	}
	
}
