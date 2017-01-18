package com.example.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

public abstract class AbstractRepositoryTest extends MockMvcTest{
	
	abstract protected String getResourceName();

}
