package com.example.github.service;

import java.util.List;

import com.example.github.data.RepositoryData;
import com.example.github.exception.BadResponseException;

public interface GithubRepositoryService {

    List<RepositoryData> searchRepositoryByUser(String user) throws BadResponseException;

    String getProgrammingLanguage(List<RepositoryData> respositories);

}
