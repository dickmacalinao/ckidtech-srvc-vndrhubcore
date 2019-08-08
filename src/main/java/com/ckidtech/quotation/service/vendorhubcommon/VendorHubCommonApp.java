package com.ckidtech.quotation.service.vendorhubcommon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class VendorHubCommonApp {
	
	@Autowired
	private Environment env;
	
	public static void main(String[] args) {
		SpringApplication.run(VendorHubCommonApp.class, args);
	}

	@RequestMapping("/")
	public String index() {
		return "Welcome to Vendor Hub Common Service at port " + env.getProperty("local.server.port") + ".";
	}

}
