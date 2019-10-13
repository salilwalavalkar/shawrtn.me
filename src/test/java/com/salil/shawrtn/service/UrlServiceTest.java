package com.salil.shawrtn.service;

import com.salil.shawrtn.domainobject.DatabaseSequence;
import com.salil.shawrtn.domainobject.UrlDO;
import com.salil.shawrtn.domainobject.embedded.DateStatistics;
import com.salil.shawrtn.domainobject.embedded.Statistics;
import com.salil.shawrtn.dto.UrlCustomResponse;
import com.salil.shawrtn.dto.UrlExpandRequestDTO;
import com.salil.shawrtn.dto.UrlShortenRequestDTO;
import com.salil.shawrtn.dto.VisitStateDTO;
import com.salil.shawrtn.exception.UrlInvalidException;
import com.salil.shawrtn.exception.UrlNotFoundException;
import com.salil.shawrtn.repository.UrlRepository;
import com.salil.shawrtn.service.impl.SequenceGeneratorService;
import com.salil.shawrtn.service.impl.UrlService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Arrays;

import static com.salil.shawrtn.util.Base62.fromBase10;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UrlServiceTest {

    @InjectMocks
    private UrlService service;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private UrlRepository repository;

    @Before
    public void contextLoads() {
        MockitoAnnotations.initMocks(this);
    }

    private UrlDO dummyUrl() {
        UrlDO shortUrl = new UrlDO();
        shortUrl.setLongUrl("https://salilwalavalkar.github.io/");
        shortUrl.setCreatedDate(LocalDate.now());
        Statistics stats = new Statistics();

        DateStatistics dateStat1 = new DateStatistics(25, 50);
        DateStatistics dateStat2 = new DateStatistics(20, 100);
        DateStatistics dateStat3 = new DateStatistics(120, 200);
        stats.getDateStats().addAll(Arrays.asList(dateStat1, dateStat2, dateStat3));

        shortUrl.setStatistics(stats);
        return shortUrl;
    }

    @Test
    public void should_resolveShortenedUrl_when_alreadyExists() throws UrlNotFoundException, UrlInvalidException {

        UrlDO shortUrl = this.dummyUrl();
        UrlExpandRequestDTO urlExpandRequestDTO = new UrlExpandRequestDTO();
        urlExpandRequestDTO.setShortUrl("c");

        Long key = 2L;
        when(repository.findByKey(key)).thenReturn(shortUrl);

        String existingUrl = service.expand(urlExpandRequestDTO);

        assertThat(existingUrl).isNotNull();
        assertThat(existingUrl).isEqualTo("https://salilwalavalkar.github.io/");
    }

    @Test(expected = UrlNotFoundException.class)
    public void should_throwException_when_shortUrlDoesNotExist() throws UrlNotFoundException, UrlInvalidException {
        UrlExpandRequestDTO urlExpandRequestDTO = new UrlExpandRequestDTO();
        urlExpandRequestDTO.setShortUrl("c");
        service.expand(urlExpandRequestDTO);
    }

    @Test(expected = UrlInvalidException.class)
    public void should_throwException_when_shortUrlIsEmpty() throws UrlNotFoundException, UrlInvalidException {
        UrlExpandRequestDTO urlExpandRequestDTO = new UrlExpandRequestDTO();
        service.expand(urlExpandRequestDTO);
    }

    @Test
    public void should_generateShortenedUrl_when_urlIsValidAndDoesNotExist() throws MalformedURLException, URISyntaxException {

        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl("https://salilwalavalkar.github.io/");

        long key = 5L;

        DatabaseSequence counter = new DatabaseSequence();
        counter.setId("1");
        counter.setSeq(100);

        when(sequenceGeneratorService.generateSequence(anyString())).thenReturn(key);

        String shortedUrl = service.shorten(requestDTO);

        assertThat(shortedUrl).isNotNull();
        assertThat(shortedUrl).isEqualTo(fromBase10(key));
    }

    @Test(expected = MalformedURLException.class)
    public void should_throwException_when_urlIsNotValid() throws URISyntaxException, MalformedURLException {
        UrlShortenRequestDTO requestDTO = new UrlShortenRequestDTO();
        requestDTO.setLongUrl("malformed-url");
        service.shorten(requestDTO);
    }

    @Test
    public void should_calculateStatistics_when_ShortUrlExist() throws UrlNotFoundException {
        Long key = 3L;
        when(repository.findByKey(key)).thenReturn(this.dummyUrl());

        VisitStateDTO dto = service.getVisitStateByKey(fromBase10(key));

        assertThat(dto.getDailyAverage()).isNotNull();
        assertThat(dto.getMax()).isNotNull();
        assertThat(dto.getMin()).isNotNull();
        assertThat(dto.getTotalPerYear()).isNotNull();
        assertThat(dto.getPerMonth()).isNotNull();
        assertThat(dto.getCode()).isEqualTo(UrlCustomResponse.SUCCESSFUL);
        assertThat(dto.getMessage()).isEqualTo("statistics");
    }

    @Test(expected = UrlNotFoundException.class)
    public void should_throwExceptionOnStatCalculation_when_shortUrlDoesNotExist() throws UrlNotFoundException {
        service.getVisitStateByKey(fromBase10(123L));
    }
}
