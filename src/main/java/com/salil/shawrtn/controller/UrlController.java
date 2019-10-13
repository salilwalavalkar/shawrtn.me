package com.salil.shawrtn.controller;

import com.salil.shawrtn.dto.UrlCustomResponse;
import com.salil.shawrtn.dto.UrlExpandRequestDTO;
import com.salil.shawrtn.dto.UrlShortenRequestDTO;
import com.salil.shawrtn.dto.VisitStateDTO;
import com.salil.shawrtn.exception.UrlInvalidException;
import com.salil.shawrtn.exception.UrlNotFoundException;
import com.salil.shawrtn.service.IUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Main REST enabled controller for url management.
 *
 * @author Salil Walavalkar
 */
@RestController
public class UrlController {

    final static String PREFIX = "/api/v1";
    private static final Logger LOGGER = LoggerFactory.getLogger(UrlController.class);
    private IUrlService urlService;

    public UrlController(IUrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping(PREFIX + "/shawrtn")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UrlCustomResponse> shorten(@Valid @RequestBody UrlShortenRequestDTO dto) throws URISyntaxException, MalformedURLException {
        LOGGER.info("Requested url to shorten: {}", dto.getLongUrl());
        String shortUrl = urlService.shorten(dto);
        LOGGER.info("Responded with short url: {}", shortUrl);
        return ResponseEntity.created(new URI(PREFIX + "/" + shortUrl)).body(new UrlCustomResponse(true, shortUrl, UrlCustomResponse.SUCCESSFUL));
    }

    @GetMapping(PREFIX + "/{key}")
    public void expand(@NotNull @PathVariable String key, HttpServletResponse response) throws UrlNotFoundException, UrlInvalidException {
        LOGGER.info("Requested url key: {}", key);

        UrlExpandRequestDTO urlExpandRequestDTO = new UrlExpandRequestDTO();
        urlExpandRequestDTO.setShortUrl(key);

        String longUrl = urlService.expand(urlExpandRequestDTO);
        LOGGER.info("Redirecting to actual url: {}", longUrl);
        response.setHeader("Location", longUrl);
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    }

    @GetMapping(PREFIX + "/statistics/{key}")
    public ResponseEntity<UrlCustomResponse> getStatistics(@PathVariable String key) throws UrlNotFoundException {
        VisitStateDTO dto = urlService.getVisitStateByKey(key);
        LOGGER.info("Requested statistics for key: {}", key);
        return ResponseEntity.ok().body(dto);
    }
}
