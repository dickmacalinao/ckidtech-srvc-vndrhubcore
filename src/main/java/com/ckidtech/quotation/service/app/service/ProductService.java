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
	
	public QuotationResponse listProducts(String vendorCode, boolean flag) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service listProducts(" + vendorCode + "," + flag +")");
		QuotationResponse quotation = new QuotationResponse();	
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			quotation.setProducts(productRepository.listProducts(vendorCode, flag));
		}
				
		return quotation;
		
	}	
	
	public QuotationResponse listProductsByGroup(String vendorCode, boolean flag) {
		
		LOG.log(Level.INFO, "Calling Vendor Service listProductsByGroup(" + vendorCode + "," + flag + ")");
		QuotationResponse quotation = new QuotationResponse();
		
		Vendor vendor = vendorRepository.findById(vendorCode).orElse(null);
		if ( vendor==null || !vendor.isActiveIndicator() ) {
			quotation.addMessage(msgController.createMsg("error.VNFE"));
		} else {
			
			List<ProductGroup> prodGroups = new ArrayList<ProductGroup>();
			ProductGroup prodGroup;
			
			@SuppressWarnings("deprecation")
			Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "grantTo", "value");
			List<ReferenceData> groups =  referenceDataRepository.searchByRoleAndRefGroup(vendorCode, "ProductGroup", pageable);
			int index = 0;
			for ( ReferenceData group : groups ) {
				prodGroup = new ProductGroup();
				prodGroup.setTitle(group.getValue());			
				prodGroup.setKey(group.getValue() + index);
				prodGroup.setProducts(productRepository.listProductsByGroup(vendorCode, flag, group.getValue()));
				prodGroups.add(prodGroup);
				index++;
			}
			
			quotation.setProdGroups(prodGroups);
			
		}		
				
		return quotation;
		
	}
	
	public QuotationResponse searchProductsByName(String vendorCode, boolean flag, String productName) {	
		
		LOG.log(Level.INFO, "Calling Vendor Service searchProductsByName()");
		QuotationResponse quotation = new QuotationResponse();	
		
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
	public QuotationResponse addVendorProduct(String userId, Product product) {		
		LOG.log(Level.INFO, "Calling Product Service addVendorProduct()");
		
		QuotationResponse quotation = new QuotationResponse();
		
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
					quotation.addMessage(msgController.createMsg("error.VPAEE"));						
				} else {
					Util.initalizeCreatedInfo(product, userId, msgController.getMsg("info.VPRC"));
					product.setActiveIndicator(false);
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
	public QuotationResponse updateVendorProduct(String userId, Product product) {		
		LOG.log(Level.INFO, "Calling Product Service updateVendorProduct()");
		
		QuotationResponse quotation = new QuotationResponse();
		
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
					quotation.addMessage(msgController.createMsg("error.VPNFE"));
				} else {	
						
					productRep.setName(product.getName());
					productRep.setGroup(product.getGroup());
					productRep.setImgLocation(product.getImgLocation());
					productRep.setProdComp(product.getProdComp());
					
					Util.initalizeUpdatedInfo(productRep, userId, msgController.getMsg("info.VPRU"));
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
	public QuotationResponse activateVendorProduct(String vendorId, String userId, String productId) {		
		LOG.log(Level.INFO, "Calling Product Service activateVendorProduct()");
		
		QuotationResponse quotation = new QuotationResponse();

		if ( vendorId==null || "".equals(vendorId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));	
		if ( userId==null || "".equals(userId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User ID"));	
		if ( productId==null || "".equals(productId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product ID"));
		
		if( quotation.getMessages().isEmpty() ) {
		
			Vendor vendorRep = vendorRepository.findById(vendorId).orElse(null);
			
			if ( vendorRep==null || !vendorRep.isActiveIndicator() ) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				Product productRep = productRepository.findById(productId).orElse(null);
				
				if  ( productRep==null ) {
					quotation.addMessage(msgController.createMsg("error.VPNFE"));
				} else {
					
					if ( vendorId!=null && !vendorId.equals(productRep.getVendorCode()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					if ( productRep.isActiveIndicator() ) {
						quotation.addMessage(msgController.createMsg("error.AUAAE"));
					} else {
						productRep.setActiveIndicator(true);						
						Util.initalizeUpdatedInfo(productRep, userId, msgController.getMsg("info.VPRA"));
						productRepository.save(productRep);
						quotation.addMessage(msgController.createMsg("info.VPRA"));
						quotation.setProduct(productRep);
						
					}
					
					
				} 	
			}
		}
		
		return quotation;
			
	}
	
	public QuotationResponse deActivateVendorProduct(String vendorId, String userId, String productId) {		
		LOG.log(Level.INFO, "Calling Product Service deActivateVendorProduct()");
		
		QuotationResponse quotation = new QuotationResponse();

		if ( vendorId==null || "".equals(vendorId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Vendor ID"));	
		if ( userId==null || "".equals(userId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "User ID"));	
		if ( productId==null || "".equals(productId) ) 
			quotation.addMessage(msgController.createMsg("error.MFE", "Product ID"));
		
		if( quotation.getMessages().isEmpty() ) {
		
			Vendor vendorRep = vendorRepository.findById(vendorId).orElse(null);
			
			if ( vendorRep==null || !vendorRep.isActiveIndicator() ) {
				quotation.addMessage(msgController.createMsg("error.VNFE"));
			} else {
				Product productRep = productRepository.findById(productId).orElse(null);
				
				if  ( productRep==null ) {
					quotation.addMessage(msgController.createMsg("error.VPNFE"));
				} else {
					
					if ( vendorId!=null && !vendorId.equals(productRep.getVendorCode()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					if ( !productRep.isActiveIndicator() ) {
						quotation.addMessage(msgController.createMsg("error.VPADAE"));
					} else {
						productRep.setActiveIndicator(true);						
						Util.initalizeUpdatedInfo(productRep, userId, msgController.getMsg("info.VPRD"));
						productRepository.save(productRep);
						quotation.addMessage(msgController.createMsg("info.VPRD"));
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
	public QuotationResponse deleteVendorProduct(UserRole role, String vendorId, String productCode) {		
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
					
					if ( UserRole.VENDOR.equals(role) && vendorId!=null && !vendorId.equals(productRep.getVendorCode()) ) {
						throw new ServiceAccessResourceFailureException();
					}
					
					productRepository.delete(productRep);
					quotation.addMessage(msgController.createMsg("info.VPRD"));
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
			deleteVendorProduct(UserRole.VENDOR, product.getVendorCode(), product.getId());
		}
		
		// Delete inactive products
		quotation = listProducts(vendorCode, false);		
		for (Product product : quotation.getProducts() ) {
			deleteVendorProduct(UserRole.VENDOR, product.getVendorCode(), product.getId());
		}
		
	}
	
}
