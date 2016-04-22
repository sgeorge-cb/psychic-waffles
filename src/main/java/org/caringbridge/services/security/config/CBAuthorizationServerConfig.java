package org.caringbridge.services.security.config;

import javax.annotation.Resource;

import org.caringbridge.services.security.services.CBAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
public class CBAuthorizationServerConfig  extends AuthorizationServerConfigurerAdapter{

	@Autowired
	private CBAuthenticationProvider authenticationProvider;
	

	@Resource(name="authenticationManager")
	private ProviderManager authenticationManager;
	
//	@Autowired
//	AuthenticationManagerBuilder authenticationManager;
	
	private static final String SERVER_RESOURCE_ID = "cboauth2/%s";

	@Autowired
	private TokenStore tokenStore;
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
	    tokenStore = tokenStore();
	    authenticationManager.getProviders().add(authenticationProvider);
	    endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore);
	}
	
	 @Override
	  public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
	    oauthServer.allowFormAuthenticationForClients();
	    oauthServer.checkTokenAccess("permitAll()");
	  }

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		// We can use JdbcClientDetailsServiceBuilder if we have more clients
	 	clients.inMemory()
	        .withClient("trusted-hallmark")
	            .authorizedGrantTypes("password", "authorization_code", "refresh_token")
	            .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
	            .scopes("read", "write", "trust")
	            .resourceIds(String.format(SERVER_RESOURCE_ID,"trusted"))
	            .accessTokenValiditySeconds(60)
		    .and()
	        .withClient("hallmark-redirect")
	            .authorizedGrantTypes("authorization_code", "implicit")
	            .authorities("ROLE_CLIENT")
	            .autoApprove(true) //or can specify scopes to autoapprove
	            .scopes("read", "trust")
	            .resourceIds(String.format(SERVER_RESOURCE_ID,"redirect"))
	            .redirectUris("http://www.kong.dev:8001") //Use any url. 
		    .and()
	        .withClient("hallmark")
	            .authorizedGrantTypes("client_credentials", "password")
	            .authorities("ROLE_CLIENT")
	            .scopes("read", "write", "trust")
	            .resourceIds(String.format(SERVER_RESOURCE_ID,"secret"))
	            .secret("secret_password");
	}

	@Bean
	public ApprovalStore approvalStore() throws Exception {
		TokenApprovalStore store = new TokenApprovalStore();
		store.setTokenStore(tokenStore);
		return store;
	}
	
	@Bean
	public TokenStore tokenStore() {
	    return new InMemoryTokenStore();
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
	    return new JwtAccessTokenConverter();
	}
}
