package com.example.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.test.security.OAuthTokenResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthHelper {
		    
	    private MockMvc mvc;
	    
	    
	    public OAuthHelper(MockMvc mvc){
	    	this.mvc = mvc;
	    }
	    
	    
	    public OAuthTokenResponse getAccessToken(String username, String password, String clientId) throws Exception {    
	        MockHttpServletResponse response = mvc
	                .perform(post("/oauth/token")
	                        .param("username", username)
	                        .param("password", password)
	                        .param("grant_type", "password")
	                		.param("client_id", clientId))
	                .andReturn().getResponse();

	        ObjectMapper object = new ObjectMapper(); 
	        object.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	        OAuthTokenResponse oauthTokenResponse =  object.readValue(
	        		response.getContentAsByteArray(), 
	        		OAuthTokenResponse.class);
	        return oauthTokenResponse;
	    }

}
