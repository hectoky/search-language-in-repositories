package com.example.github.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.example.github.Application;
import com.example.github.data.RepositoryData;
import com.example.github.exception.BadResponseException;
import com.example.github.service.GithubRepositoryService;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@SpringApplicationConfiguration(classes = Application.class)
@ActiveProfiles("test")
@IntegrationTest
@WebAppConfiguration
public class GithubRepositoryServiceImplTest {

    private RestTemplate restTemplate;

    @Autowired
    GithubRepositoryService myService;

    @Before
    public void init() {
        restTemplate = Mockito.mock(RestTemplate.class);
        ReflectionTestUtils.setField(myService, "restTemplate", restTemplate);
    }

    @Test
    public void testsearchRepositoryByUser_ResponseOK() {

        List<RepositoryData> mockedList = Mockito.mock(List.class);
        ResponseEntity<List<RepositoryData>> response =
            new ResponseEntity<List<RepositoryData>>(mockedList, HttpStatus.OK);
        ParameterizedTypeReference<List<RepositoryData>> type = new ParameterizedTypeReference<List<RepositoryData>>() {
        };
        Mockito.when(restTemplate.exchange("https://api.github.com/users/test/repos", HttpMethod.GET, null, type))
            .thenReturn(response);
        try {
            myService.searchRepositoryByUser("test");
        } catch (BadResponseException e) {
            fail();
        }
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(response.getBody(), mockedList);

    }

    @Test
    public void testsearchRepositoryByUser_ResponseKO() {
        List<RepositoryData> mockedList = Mockito.mock(List.class);
        ResponseEntity<List<RepositoryData>> response =
            new ResponseEntity<List<RepositoryData>>(mockedList, HttpStatus.BAD_REQUEST);
        ParameterizedTypeReference<List<RepositoryData>> type = new ParameterizedTypeReference<List<RepositoryData>>() {
        };
        Mockito.when(restTemplate.exchange("https://api.github.com/users/test/repos", HttpMethod.GET, null, type))
            .thenReturn(response);
        try {
            myService.searchRepositoryByUser("test");
            fail();
        } catch (BadResponseException e) {
            assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        }

    }

    @Test
    public void testgetProgrammingLanguage() {
        // Generate a list of 10 repo in Java, 4 in python and 2 in C
        List<RepositoryData> testList = Stream.generate(RepositoryData::new).limit(10).map(a -> {
            a.setLanguage("Java");
            return a;
        }).collect(Collectors.toList());
        List<RepositoryData> testList_Python = Stream.generate(RepositoryData::new).limit(4).map(a -> {
            a.setLanguage("Python");
            return a;
        }).collect(Collectors.toList());
        List<RepositoryData> testList_C = Stream.generate(RepositoryData::new).limit(2).map(a -> {
            a.setLanguage("C");
            return a;
        }).collect(Collectors.toList());
        testList.addAll(testList_Python);
        testList.addAll(testList_C);

        String result = myService.getProgrammingLanguage(testList);
        assertEquals(result, "Java");

    }

    @Test
    public void testgetProgrammingLanguage_emptyRepoList() {
        List<RepositoryData> testList = new ArrayList<RepositoryData>();
        String result = myService.getProgrammingLanguage(testList);
        assertEquals(result, "There is not favourite language for that user, there is not any repository");
    }

    @Test
    public void testgetProgrammingLanguage_repoList_nullLanguage() {
        List<RepositoryData> testList = Stream.generate(RepositoryData::new).limit(10).collect(Collectors.toList());
        String result = myService.getProgrammingLanguage(testList);
        assertEquals(result, "There is not favourite language for that user, there is not any repository");
    }

    @Test
    public void testgetProgrammingLanguage_sameNumberOfRepo() {
        // Generate a list of 10 repo in Java, 4 in python and 2 in C
        List<RepositoryData> testList = Stream.generate(RepositoryData::new).limit(4).map(a -> {
            a.setLanguage("Java");
            return a;
        }).collect(Collectors.toList());
        List<RepositoryData> testList_Python = Stream.generate(RepositoryData::new).limit(4).map(a -> {
            a.setLanguage("Python");
            return a;
        }).collect(Collectors.toList());
        List<RepositoryData> testList_C = Stream.generate(RepositoryData::new).limit(4).map(a -> {
            a.setLanguage("C");
            return a;
        }).collect(Collectors.toList());
        List<RepositoryData> testList_ObjectiveC = Stream.generate(RepositoryData::new).limit(2).map(a -> {
            a.setLanguage("Objective-C");
            return a;
        }).collect(Collectors.toList());
        List<RepositoryData> testList_Scala = Stream.generate(RepositoryData::new).limit(2).map(a -> {
            a.setLanguage("Scala");
            return a;
        }).collect(Collectors.toList());
        testList.addAll(testList_Python);
        testList.addAll(testList_C);
        testList.addAll(testList_ObjectiveC);
        testList.addAll(testList_Scala);

        String result = myService.getProgrammingLanguage(testList);
        assertThat(result, CoreMatchers.containsString("Java"));
        assertThat(result, CoreMatchers.containsString("Python"));
        assertThat(result, CoreMatchers.containsString("C"));

    }

}
