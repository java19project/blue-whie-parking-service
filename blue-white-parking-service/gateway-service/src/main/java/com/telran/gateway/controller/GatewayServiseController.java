package com.telran.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.telran.gateway.service.IProxyService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GatewayServiseController {
	@Autowired
	IProxyService service;
	
	@GetMapping("/**")
	ResponseEntity<byte[]>getResult(ProxyExchange<byte[]> proxy, HttpServletRequest request){
		return service.proxyRouting(proxy, request);
	}
}