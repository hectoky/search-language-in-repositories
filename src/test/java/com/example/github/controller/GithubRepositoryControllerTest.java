package com.example.github.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.github.Application;
import com.example.github.service.GithubRepositoryService;
import com.example.github.service.impl.GithubRepositoryServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
@IntegrationTest
@WebAppConfiguration
public class GithubRepositoryControllerTest {

    private MockMvc restMockMvc;

    @Autowired
    WebApplicationContext wac;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GithubRepositoryController myController = new GithubRepositoryController();
        this.restMockMvc = MockMvcBuilders.standaloneSetup(myController).build();
    }

    @Test
    public void test_400HttpCode_missingParameter() throws Exception {
        restMockMvc.perform(get("/language")).andExpect(status().is(400));
    }

    @Test
    public void test_404HttpCode_wronUrl() throws Exception {
        restMockMvc.perform(get("/noPageFound")).andExpect(status().is(404));
    }

    @Test
    public void test_200HttpCode() throws Exception {

        GithubRepositoryController myController = new GithubRepositoryController();
        GithubRepositoryService myService = Mockito.mock(GithubRepositoryServiceImpl.class);
        ReflectionTestUtils.setField(myController, "service", myService);
        this.restMockMvc = MockMvcBuilders.standaloneSetup(myController).build();

        Mockito.when(myService.searchRepositoryByUser("helloTest")).thenReturn(null);
        Mockito.when(myService.getProgrammingLanguage(null)).thenReturn("Java");
        restMockMvc.perform(get("/language?user=helloTest")).andExpect(status().is(200));
    }

    // This test it is only to check the app
    @Test
    public void testAlltheApplication() throws Exception {
        String user = "rose";
        this.restMockMvc = MockMvcBuilders.webAppContextSetup(this.wac).dispatchOptions(true).build();
        restMockMvc.perform(get("/language?user=" + user)).andExpect(status().is(200));
    }
}
