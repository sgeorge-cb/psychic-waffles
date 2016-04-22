package org.caringbridge.services.security.config;

import javax.annotation.Priority;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
@Order(1)
public class CBResourceServerConfig2 extends ResourceServerConfigurerAdapter {

	private static final String SERVER_RESOURCE_ID = "cboauth2/secret";

    	@Autowired
    	private TokenStore tokenStore;

	@Override
	public void configure(HttpSecurity http) throws Exception {
	    System.out.println("+++++++++++++++++++++++++++++++++++");

	    http.csrf().disable();
	    http.httpBasic().disable();
	    http.
		requestMatchers().antMatchers("/secured/**").and().
		authorizeRequests().antMatchers("/**").hasRole("CLIENT")
		.and().authorizeRequests()
                .anyRequest().authenticated();
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
