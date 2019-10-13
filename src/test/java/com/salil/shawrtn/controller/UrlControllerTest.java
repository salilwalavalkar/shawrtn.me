package com.salil.shawrtn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salil.shawrtn.domainobject.UrlDO;
import com.salil.shawrtn.domainobject.embedded.Statistics;
import com.salil.shawrtn.dto.UrlCustomResponse;
import com.salil.shawrtn.dto.UrlExpandRequestDTO;
import com.salil.shawrtn.dto.UrlShortenRequestDTO;
import com.salil.shawrtn.dto.VisitStateDTO;
import com.salil.shawrtn.service.impl.UrlService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService shortUrlService;

    private JacksonTester<UrlShortenRequestDTO> shorteningRequest;
    private JacksonTester<VisitStateDTO> getStatRequest;
    private JacksonTester<UrlDO> getShortUrl;

    @Before
    public void setup() {
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    public void should_returnShortedUrl_whenUrlIsValid() throws Exception {

        UrlShortenRequestDTO dto = new UrlShortenRequestDTO();
        dto.setLongUrl("https://salilwalavalkar.github.io/");
        final String linkDTOJson = shorteningRequest.write(dto).getJson();
        given(shortUrlService.shorten(ArgumentMatchers.any(UrlShortenRequestDTO.class))).willReturn("b");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                "/api/v1/shawrtn").accept(MediaType.APPLICATION_JSON).content(linkDTOJson)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("b"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(UrlCustomResponse.SUCCESSFUL))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andReturn();
    }

    @Test
    public void should_redirectUrl_whenUrlIsReturned() throws Exception {

        UrlDO urlDO = new UrlDO();
        urlDO.setLongUrl("http://google.com");
        urlDO.setCreatedDate(LocalDate.now());
        urlDO.setModifiedDate(LocalDate.now());
        urlDO.setKey(2L);
        urlDO.setStatistics(new Statistics());

        given(shortUrlService.expand(any(UrlExpandRequestDTO.class))).willReturn(urlDO.getLongUrl());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/api/v1/aBC");

        mockMvc.perform(requestBuilder).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlDO.getLongUrl()))
                .andReturn();
    }
}
