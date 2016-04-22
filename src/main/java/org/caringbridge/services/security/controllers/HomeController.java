package org.caringbridge.services.security.controllers;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping({ "/api/v1/", "" , "/test"})
public class HomeController {

	@RequestMapping(value = "/secured/hello", method = {RequestMethod.POST,RequestMethod.GET})
	public Authentication hello( 
                @RequestHeader Map<String,String> headers) {
		    for (String elem: headers.keySet()) {
			     System.out.println(elem + " : " + headers.get(elem));
			    }
		return SecurityContextHolder.getContext().getAuthentication();
	}



}
