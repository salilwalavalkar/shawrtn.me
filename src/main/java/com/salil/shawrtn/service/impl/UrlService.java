package com.salil.shawrtn.service.impl;

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
import com.salil.shawrtn.service.ISequenceGeneratorService;
import com.salil.shawrtn.service.IUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

import static com.salil.shawrtn.util.Base62.fromBase10;
import static com.salil.shawrtn.util.Base62.toBase10;
import static com.salil.shawrtn.util.UrlValidator.isUrlValid;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class UrlService implements IUrlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlService.class);

    private UrlRepository urlRepository;
    private ISequenceGeneratorService sequenceGeneratorService;

    public UrlService(UrlRepository urlRepository, ISequenceGeneratorService sequenceGeneratorService) {
        this.urlRepository = urlRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Override
    public String shorten(final UrlShortenRequestDTO dto) throws URISyntaxException, MalformedURLException {
        String longUrl = normalizeURL(dto.getLongUrl());

        if (!isUrlValid(longUrl)) {
            throw new MalformedURLException();
        }

        Optional<UrlDO> existingShortUrl = Optional.ofNullable(urlRepository.findByLongUrl(longUrl));
        if (existingShortUrl.isPresent()) {
            return fromBase10(existingShortUrl.get().getKey());
        }

        long newKey = sequenceGeneratorService.generateSequence("key_sequence");

        UrlDO newShortUrl = new UrlDO();
        newShortUrl.setKey(newKey);
        newShortUrl.setLongUrl(longUrl);
        newShortUrl.setCreatedDate(LocalDate.now());
        newShortUrl.setModifiedDate(LocalDate.now());

        newShortUrl.setStatistics(new Statistics());

        urlRepository.save(newShortUrl);
        String urlString = fromBase10(newKey);

        LOGGER.debug("New url {} generated with key {} ", urlString, newKey);
        return urlString;
    }

    @Override
    public String expand(final UrlExpandRequestDTO dto) throws UrlNotFoundException, UrlInvalidException {

        if (isEmpty(dto.getShortUrl())) {
            throw new UrlInvalidException(String.format("Url is empty", dto.getShortUrl()));
        }

        UrlDO existingShortUrl = urlRepository.findByKey(toBase10(dto.getShortUrl()));
        if (null == existingShortUrl) {
            throw new UrlNotFoundException(String.format("Url not found for key %s", dto.getShortUrl()));
        }
        updateStats(existingShortUrl);
        existingShortUrl.setModifiedDate(LocalDate.now());
        urlRepository.save(existingShortUrl);
        return existingShortUrl.getLongUrl();
    }

    private void updateStats(UrlDO existingShortUrl) {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        existingShortUrl.getStatistics().getDateStats().stream().filter((d) -> d.getDayOfYear() == dayOfYear).findFirst().map(d -> {
            d.incrementVisit();
            return d;
        }).orElseGet(() -> {
            DateStatistics newDateStat = new DateStatistics(dayOfYear, 1);
            existingShortUrl.getStatistics().getDateStats().add(newDateStat);
            return newDateStat;
        });
    }

    private String normalizeURL(final String urlValue) {
        String normalizedUrl = urlValue.trim();
        if (normalizedUrl.endsWith("/")) {
            // Force the string end without "/"
            normalizedUrl = normalizedUrl.substring(0, normalizedUrl.length() - 1);
        }
        LOGGER.debug("URL post normalize: " + normalizedUrl);
        return normalizedUrl;
    }

    public VisitStateDTO getVisitStateByKey(String key) throws UrlNotFoundException {
        VisitStateDTO dto = new VisitStateDTO();

        UrlDO existingShortUrl = urlRepository.findByKey(toBase10(key));
        if (null == existingShortUrl) {
            throw new UrlNotFoundException(String.format("Url not found for key %s", key));
        }

        LongSummaryStatistics longSummaryStatistics = existingShortUrl.getStatistics().getDateStats().stream().mapToLong(d -> d.getVisits()).summaryStatistics();
        dto.setDailyAverage(longSummaryStatistics.getAverage());
        dto.setMax(longSummaryStatistics.getMax());
        dto.setMin(longSummaryStatistics.getMin());
        dto.setTotalPerYear(longSummaryStatistics.getSum());
        dto.setPerMonth(getMonthlyVisitReport(existingShortUrl));
        dto.setModifiedDate(existingShortUrl.getModifiedDate());
        dto.setCode(UrlCustomResponse.SUCCESSFUL);
        dto.setSuccess(true);
        dto.setMessage("statistics");

        return dto;
    }

    private Map<String, Long> getMonthlyVisitReport(UrlDO shortUrl) {
        int year = LocalDate.now().getYear();
        Map<String, Long> monthlyVisitsReport = new HashMap<>();
        for (LocalDate date = LocalDate.of(year, 1, 1); date.isBefore(LocalDate.of(year + 1, 1, 1)); date = date.plusMonths(1)) {
            String month = date.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
            Long totalVisits = this.getTotalVisitForAMonth(shortUrl, date.getDayOfYear(), date.plusMonths(1).getDayOfYear());
            monthlyVisitsReport.put(month, totalVisits);
        }

        return monthlyVisitsReport;
    }

    private Long getTotalVisitForAMonth(UrlDO shortUrl, Integer startDay, Integer lastDay) {
        return shortUrl.getStatistics().getDateStats().stream().filter(d -> d.getDayOfYear() >= startDay && d.getDayOfYear() < lastDay).mapToLong(d -> d.getVisits()).sum();
    }
}
