package com.salil.shawrtn.service;

import com.salil.shawrtn.dto.UrlExpandRequestDTO;
import com.salil.shawrtn.dto.UrlShortenRequestDTO;
import com.salil.shawrtn.dto.VisitStateDTO;
import com.salil.shawrtn.exception.UrlInvalidException;
import com.salil.shawrtn.exception.UrlNotFoundException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public interface IUrlService {

    String shorten(final UrlShortenRequestDTO dto) throws URISyntaxException, MalformedURLException;

    String expand(final UrlExpandRequestDTO dto) throws UrlNotFoundException, UrlInvalidException;

    VisitStateDTO getVisitStateByKey(String key) throws UrlNotFoundException;
}
