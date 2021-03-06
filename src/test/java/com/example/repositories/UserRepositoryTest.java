package com.example.repositories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.Entity;

import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.example.model.User;

public class UserRepositoryTest extends AbstractRepositoryTest{
	
	private static final String NEW_USER_CONTENT = "{\n\t\"firstName\": \"Juan\", \n\t\"lastName\":"
    			+ " \"Palomo\",\n\t\"username\": \"palomo_50\",\n\t\"password\":"
    			+ " \"123456\",\n\t\"authority\": "
    			+ "\"http://localhost/authorities/2\"\n}";
	
	/**
	 * TEST WITH ROLE: USER
	 */
    public static final String RESOURCE_NAME = "users";
    

	@Override
	protected String getResourceName() {
		return RESOURCE_NAME;
	}
    
	/*
	 * Test 1. Get details from registered User with Role User
	 */
    @Test
    public void Should_getUserDetails_When_UserWithRoleUser() throws Exception{
    	verify(read(user()), isOk());
    	
    }
    
	/*
	 * Test 2. Get details of other user from registered User with Role User
	 */
    @Test
    public void Should_getUserDetails_When_UserDetailsFromANotherUserWithRoleUser() throws Exception{
    	verify(read(user(), "/1"), isOk());
    }
	
	/*
	 * Test 3. Post/Put User with registered User with Role User
	 */
	@Test 
	public void Should_getErrorMessage_When_PostUserWithRoleUser() throws Exception{    	    	
    	verify(create(user(), NEW_USER_CONTENT), isForbidden());
	}
	
    
	/*
	 * Test 4. Delete User with registered User with Role User
	 */
	@Test
	public void Should_getErrorMessage_When_DeleteUserWithRoleUser() throws Exception{ 
		verify(remove(user(), "/2"), isForbidden());
	}
	
	
	
	/**
	 * TESTS WITH ROLE: ADMIN
	 */
	
	/*
	 * Test 1. Get details from registered User with Role Admin
	 */	
	@Test
	public void Should_getAllUserDetails_When_UserWithRoleAdmin() throws Exception{		
		ResultActions result = read(admin());
		
		verify(result, isOk());
		
		List<? extends Entity> users = getEntitiesList(result.andReturn().getResponse().getContentAsString());
		assertNotNull(users);
		assertTrue(users.size() > 1);
		
	}
	
	/*
	 * Test 2. Get details of other user from registered User with Role Admin
	 */
	@Test
	public void Should_getUserDetailsOfAnotherUser_When_UserWithRoleAdmin() throws Exception{
		ResultActions result = read(admin(), "/4");
		
		verify(result, isOk());
		
    	User user = mapper.readValue(result.andReturn().getResponse().getContentAsByteArray(), User.class);
    	
    	assertNotNull(user);
    	assertEquals(user.getUsername(), "victor_50");
	}
	
	/*
	 * Test 3. Post/Put User with registered User with Role Admin
	 */
	@Test
	public void Should_addNewuser_When_UserWithRoleAdmin() throws Exception{    	
		verify(create(admin(), NEW_USER_CONTENT), isCreated());
	}
	

	
	
	/*
	 * Test 4. Delete User with registered User with Role Admin
	 */
	@Test
	public void Should_deleteUser_When_UserWithRoleAdmin() throws Exception{
		ResultActions result = read(admin(), "search/findByUsername?username="+"palomo_50" );
    	
		verify(result, isOk());
		
    	JSONObject json = new JSONObject(result.andReturn().getResponse().getContentAsString());
    	String link = json.getJSONObject("_links").getJSONObject("self").getString("href");
    	String varId = link.substring(link.lastIndexOf("/")+1, link.length());
    	
    	verify(remove(admin(), varId), isNoContent());
	}	
}
