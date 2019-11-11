package com.ckidtech.quotation.service.vendorhubcommon.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
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
import com.ckidtech.quotation.service.core.model.Connection;
import com.ckidtech.quotation.service.core.model.ReferenceData;
import com.ckidtech.quotation.service.core.model.ReferenceGroup;
import com.ckidtech.quotation.service.core.security.UserRole;
import com.ckidtech.quotation.service.core.utils.Util;
import com.ckidtech.quotation.service.vendorhubcommon.service.ReferenceDataService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan({"com.ckidtech.quotation.service.core.service"})
@RestController
public class VendorHubCommonControllerConfig {
	
	private static final Logger LOG = Logger.getLogger(VendorHubCommonControllerConfig.class.getName());
	
	@Autowired
	private ReferenceDataService referenceDataService;
	
	@Value("classpath:/resources/static/json/test.json")
    private Resource testMockup;
	
	
	// Open services
	@RequestMapping(value = "/config/open/getConnection/{env}")
	public ResponseEntity<Object> opengetConnection(@PathVariable("env") String env) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/open/getConnection");
		
		// read json file data to String
		byte[] jsonData = Files.readAllBytes(Paths.get("src/main/resources/static/json/connections.json"));
		
		
		// create ObjectMapper instance
		ObjectMapper objectMapper = new ObjectMapper();
		
		// Convert json string to object
		List<Connection> connections = objectMapper.readValue(jsonData, new TypeReference<List<Connection>>() {});
		
		Connection retConn = null;
		for (Connection conn : connections) {
			if ( env.equalsIgnoreCase(conn.getId()) ) {
				retConn = conn;
				break;
			}
			//LOG.log(Level.INFO, "Connection:" + conn);
		}			
		
		return new ResponseEntity<Object>(retConn, HttpStatus.OK);		
	}
	
	
	// Service for ADMIN
	
	@RequestMapping(value = "/config/appadmin/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceData(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/appadmin/createreferencedata");
		
		AppUser loginUser = new AppUser(authorization);		
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(loginUser, refData), HttpStatus.OK);		
	}
	
	
	@RequestMapping(value = "/config/appadmin/createreferencedatamultiple", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataMultiple(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData[] refDataMutiple) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/appadmin/createreferencedatamultiple");
		
		AppUser loginUser = new AppUser(authorization);		
		Util.checkAccessGrant(loginUser, UserRole.APP_ADMIN, null);
		List<QuotationResponse> listRefData = new ArrayList<QuotationResponse>();
		for (ReferenceData refData : refDataMutiple) {
			listRefData.add(referenceDataService.createReferenceData(loginUser, refData));
		}
		return new ResponseEntity<Object>(listRefData, HttpStatus.OK);		
	}
	
	// Service for VENDOR only
	
	@RequestMapping(value = "/config/vendoradmin/createreferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> createReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/createreferencedata");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.createReferenceData(loginUser, refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendoradmin/updatereferencedata", method = RequestMethod.POST)
	public ResponseEntity<Object> updateReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@RequestBody ReferenceData refData) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/updatereferencedata");		
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.updateReferenceData(loginUser, refData), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendoradmin/deleteReferencedata/{refId}", method = RequestMethod.DELETE)
	public ResponseEntity<Object> deleteReferenceDataByVendor(@RequestHeader("authorization") String authorization,
			@PathVariable("refId") String refId) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/deleteReferencedata");		
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(referenceDataService.deleteReferenceData(loginUser, refId), HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendoradmin/viewreferencedatabyvendor")
	public ResponseEntity<Object> viewReferenceDataByVendor(@RequestHeader("authorization") String authorization) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/viewreferencedatabyvendor");

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		
		
		List<ReferenceGroup> refGroups = new ArrayList<ReferenceGroup>();
		ReferenceGroup refGroup;
		
		String[] refGroupNames = {"Product Group", "Discount"};
		int index = 0;
		List<ReferenceData> data;
		
		//Map<String, List<ReferenceData>> refGroups = new HashMap<String, List<ReferenceData>>();
		
		LOG.log(Level.INFO, loginUser.toString());
		for (String refGroupName : refGroupNames) {
			refGroup = new ReferenceGroup();
			refGroup.setTitle(refGroupName);			
			refGroup.setKey(refGroupName + index);
			data = referenceDataService.viewReferenceDataByRoleAndRefGroup(loginUser.getObjectRef(), refGroupName.replace(" ", ""));
			LOG.log(Level.INFO, refGroupName);
			if ( data.size()>0 ) {
				refGroup.setData(data);
				refGroups.add(refGroup);
				index++;
				LOG.log(Level.INFO, data.toString());
			}		
			
		}
				
		return new ResponseEntity<Object>(refGroups, HttpStatus.OK);		
	}
	
	@RequestMapping(value = "/config/vendoradmin/viewreferencedatabygroup/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByVendor(@RequestHeader("authorization") String authorization,
			@PathVariable("refGroup") String refGroup) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoradmin/viewreferencedatabygroup");

		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(loginUser.getObjectRef(), refGroup), HttpStatus.OK);		
	}
	
	// Service for USER only
		
	@RequestMapping(value = "/config/vendoruser/viewreferencedatabygroup/{refGroup}")
	public ResponseEntity<Object> viewReferenceDataByGroupByUser(@RequestHeader("authorization") String authorization,
			@PathVariable("refGroup") String refGroup) throws Exception {		
		LOG.log(Level.INFO, "Calling API /config/vendoruser/viewreferencedatabygroup");
		
		AppUser loginUser = new AppUser(authorization);
		Util.checkAccessGrant(loginUser, UserRole.VENDOR_ADMIN, null);
		
		return new ResponseEntity<Object>(
			referenceDataService.viewReferenceDataByRoleAndRefGroup(loginUser.getObjectRef(), refGroup), HttpStatus.OK);		
	}
			
}
