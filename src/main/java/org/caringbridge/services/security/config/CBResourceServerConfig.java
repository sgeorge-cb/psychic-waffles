package org.caringbridge.services.security.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
@EnableResourceServer
@Order(2)
public class CBResourceServerConfig extends ResourceServerConfigurerAdapter {

	private static final String SERVER_RESOURCE_ID = "cboauth2/redirect";

    	@Autowired
    	private TokenStore tokenStore;
    	
	private TokenExtractor tokenExtractor = new BearerTokenExtractor();

	@Override
	public void configure(HttpSecurity http) throws Exception {
	    System.out.println("=======================================");
		http.addFilterAfter(new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
					HttpServletResponse response, FilterChain filterChain)
					throws ServletException, IOException {
			    System.out.println("++++++++++=======================================");

			    // We don't want to allow access to a resource with no token so clear
				// the security context in case it is actually an OAuth2Authentication
				if (tokenExtractor.extract(request) == null) {
					SecurityContextHolder.clearContext();
				}
				filterChain.doFilter(request, response);
			}
		}, AbstractPreAuthenticatedProcessingFilter.class);
		http.csrf().disable();
		http.authorizeRequests().anyRequest().authenticated();
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.tokenStore(tokenStore)
		.resourceId(SERVER_RESOURCE_ID);

	}
	
	@Bean
	public AccessTokenConverter accessTokenConverter() {
		return new DefaultAccessTokenConverter();
	}

}
