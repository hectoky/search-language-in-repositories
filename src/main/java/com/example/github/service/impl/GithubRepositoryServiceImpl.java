package com.example.github.service.impl;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.github.data.RepositoryData;
import com.example.github.exception.BadResponseException;
import com.example.github.service.GithubRepositoryService;

@Service
public class GithubRepositoryServiceImpl implements GithubRepositoryService {

    private static final Logger log = Logger.getLogger(GithubRepositoryServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.githud.search.repository.url}")
    private String url;

    @Override
    public List<RepositoryData> searchRepositoryByUser(String user) throws BadResponseException {
        // Build the url
        String completeUrl = String.format(url, user.trim().toLowerCase());

        // Send the request
        log.info("Sending request to " + completeUrl);
        ParameterizedTypeReference<List<RepositoryData>> type = new ParameterizedTypeReference<List<RepositoryData>>() {
        };

        ResponseEntity<List<RepositoryData>> response = restTemplate.exchange(completeUrl, HttpMethod.GET, null, type);

        // Check the response
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            String message =
                String.format("There was an error requesting data from Github. HttpCode: %s, Url: %s",
                              response.getStatusCode(), completeUrl);
            log.error(message);
            throw new BadResponseException(message);
        }

        return response.getBody();

    }

    @Override
    public String getProgrammingLanguage(List<RepositoryData> respositories) {
        // Build a map key=language value=number of ocurrencies
        Map<String, Long> languageProgrammingMap =
            respositories.stream().filter(a -> a.getLanguage() != null)
                .collect(Collectors.groupingBy(RepositoryData::getLanguage, Collectors.counting()));

        // Debug
        languageProgrammingMap.entrySet().forEach(a -> log.info(String.format("Language: %s has %d repositories",
                                                                              a.getKey(), a.getValue())));
        // Get the language with more repo and store it in a optional
        // If there were not any language in the repo (null value), returns
        // "There is not favourite language for that user, there is not any repository"
        Entry<String, Long> result =
            languageProgrammingMap
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue(Long::compareTo))
                .orElse(new AbstractMap.SimpleEntry<String, Long>(
                                                                  "There is not favourite language for that user, there is not any repository",
                                                                  Long.valueOf(0)));

        // Check if there were more language with the same nombre of repo, if so the result is the concatenation of all
        // these languages
        if (result.getValue().longValue() != 0) {
            String languages =
                languageProgrammingMap.entrySet().stream().filter(entry -> entry.getValue().equals(result.getValue()))
                    .map(entry -> entry.getKey()).reduce((x, y) -> (x + " " + y)).get();
            return languages;
        }
        return result.getKey();

    }
}
