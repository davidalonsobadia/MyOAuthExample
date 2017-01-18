package com.example.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.config.OAuthHelper;
import com.example.model.User;
import com.example.test.security.OAuthTokenResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class UserRepositoryTest extends AbstractRepositoryTest{
	
	/**
	 * TEST WITH ROLE: USER
	 */
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private FilterChainProxy springSecurityFilterChain;
    
    private MockMvc mvc;
        
    private OAuthHelper oauthHelper;
    
    
    private static final String ADMIN_NAME = "alonso_50";
    private static final String ADMIN_PASSWORD = "123456";
    private static final String USER_NAME = "victor_50";
    private static final String USER_PASSWORD = "123456";
    private static final String CLIENT_ID = "davidapp";
    
    private static final String RESOURCE_NAME = "users";
    
    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(springSecurityFilterChain).build();
        oauthHelper = new OAuthHelper(mvc);
    }
    
	/*
	 * Test 1. Get details from registered User with Role User
	 */
    @Test
    public void Should_getUserDetails_When_UserWithRoleUser() throws Exception{
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(USER_NAME, USER_PASSWORD, CLIENT_ID);
    	
    	MockHttpServletResponse response = mvc.perform(
    			get("/users")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    			)
    			.andExpect(status().isOk())
    			.andReturn().getResponse();
    	
    	
    	JSONObject json = new JSONObject(response.getContentAsString());    	    	
    	JSONArray arrays = json.getJSONObject("_embedded").getJSONArray("users");
    	
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	List<User> users = mapper.readValue(arrays.toString(), new TypeReference<List<User>>(){});
    	
    	assertNotNull(users);
    	assertEquals(users.size(), 1);
    	assertEquals(users.get(0).getUsername(), USER_NAME);
    }
	
	/*
	 * Test 2. Get details of other user from registered User with Role User
	 */
    @Test
    public void Should_getErrorMessage_When_UserDetailsFromANotherUserWithRoleUser() throws Exception{
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(USER_NAME, USER_PASSWORD, CLIENT_ID);
    	
    	mvc.perform(
    			get("/users/1")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    			)		
    			.andExpect(status().isForbidden());
    
    }
	
	/*
	 * Test 3. Post/Put User with registered User with Role User
	 */
	@Test
	public void Should_getErrorMessage_When_PostUserWithRoleUser() throws Exception{
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(USER_NAME, USER_PASSWORD, CLIENT_ID);
    	
    	String content = "{\n\t\"firstName\": \"Juan\", \n\t\"lastName\":"
    			+ " \"Palomo\",\n\t\"username\": \"palomo_50\",\n\t\"password\":"
    			+ " \"123456\",\n\t\"authority\": "
    			+ "\"http://localhost/authorities/2\"\n}";
    	
    	mvc.perform(
    			post("/users")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    				.header("Content-Type", "application/json")
    				.content(content)
    			)		
    			.andExpect(status().isForbidden());
	}
    
	/*
	 * Test 4. Delete User with registered User with Role User
	 */
	@Test
	public void Should_getErrorMessage_When_DeleteUserWithRoleUser() throws Exception{ 
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(USER_NAME, USER_PASSWORD, CLIENT_ID);
    	
    	mvc.perform(
    			delete("/users/1")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    			)		
    			.andExpect(status().isForbidden());
	}
	
	
	
	/**
	 * TESTS WITH ROLE: ADMIN
	 */
	
	/*
	 * Test 1. Get details from registered User with Role Admin
	 */
	@Test
	public void Should_getAllUserDetails_When_UserWithRoleAdmin() throws Exception{
		OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(ADMIN_NAME, ADMIN_PASSWORD, CLIENT_ID);
		
    	MockHttpServletResponse response = mvc.perform(
    			get("/users")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    			)
    			.andReturn().getResponse();
    	
    	
    	JSONObject json = new JSONObject(response.getContentAsString());    	    	
    	JSONArray arrays = json.getJSONObject("_embedded").getJSONArray("users");
    	
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	List<User> users = mapper.readValue(arrays.toString(), new TypeReference<List<User>>(){});
    	
    	assertEquals(response.getStatus(), 200);
    	assertNotNull(users);
    	assertTrue(users.size() > 1);
	}
	
	/*
	 * Test 2. Get details of other user from registered User with Role Admin
	 */
	@Test
	public void Should_getUserDetailsOfAnotherUser_When_UserWithRoleAdmin() throws Exception{
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(ADMIN_NAME, ADMIN_PASSWORD, CLIENT_ID);
    	
    	MockHttpServletResponse response = mvc.perform(
    			get("/users/2")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    			)		
    			.andExpect(status().isOk())
    			.andExpect(content().contentTypeCompatibleWith("application/hal+json;charset=UTF-8"))
    			.andReturn().getResponse();
    	
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	User user = mapper.readValue(response.getContentAsByteArray(), User.class);
    	
    	assertNotNull(user);
    	assertEquals(user.getUsername(), "sergi_50");
	}
	
	/*
	 * Test 3. Post/Put User with registered User with Role Admin
	 */
	@Test
	public void Should_addNewuser_When_UserWithRoleAdmin() throws Exception{
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(ADMIN_NAME, ADMIN_PASSWORD, CLIENT_ID);
    	
    	String content = "{\n\t\"firstName\": \"Juan\", \n\t\"lastName\":"
    			+ " \"Palomo\",\n\t\"username\": \"palomo_50\",\n\t\"password\":"
    			+ " \"123456\",\n\t\"authority\": "
    			+ "\"http://localhost/authorities/2\"\n}";
    	
    	MockHttpServletResponse response = mvc.perform(
    			post("/users")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    				.header("Content-Type", "application/json")
    				.content(content)
    			)		
    			.andExpect(status().isCreated())
    			.andDo(print())
    			.andReturn().getResponse();
    	
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    	    	
    	String locationHeader = response.getHeader("Location");
	}
	
	/*
	 * Test 4. Delete User with registered User with Role Admin
	 */
	@Test
	public void Should_deleteUser_When_UserWithRoleAdmin() throws Exception{
    	OAuthTokenResponse tokenResponse = oauthHelper.getAccessToken(ADMIN_NAME, ADMIN_PASSWORD, CLIENT_ID);
    	
    	String userContent = mvc.perform(
    			get("/users/search/findByUsername?username="+"palomo_50")
    				.header("Authorization", "Bearer " + tokenResponse.accessToken))
    			.andReturn().getResponse()
    			.getContentAsString();

    	JSONObject json = new JSONObject(userContent);    	    	
    	String locationUser = json.getJSONObject("_links").
    			getJSONObject("self")
    			.getString("href");
    	
    	mvc.perform(
    			delete(locationUser)
    				.header("Authorization", "Bearer " + tokenResponse.accessToken)
    			)		
    			.andExpect(status().isNoContent());
	}

	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}
	
}
