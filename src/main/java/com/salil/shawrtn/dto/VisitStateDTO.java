package com.salil.shawrtn.dto;

import java.time.LocalDate;
import java.util.Map;

public class VisitStateDTO extends UrlCustomResponse {

    private LocalDate modifiedDate;
    private Double dailyAverage;
    private Long max;
    private Long min;
    private Long totalPerYear;
    private Map<String, Long> perMonth;

    public Map<String, Long> getPerMonth() {
        return perMonth;
    }

    public void setPerMonth(Map<String, Long> perMonth) {
        this.perMonth = perMonth;
    }

    public Long getMax() {
        return max;
    }

    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMin() {
        return min;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Double getDailyAverage() {
        return dailyAverage;
    }

    public void setDailyAverage(Double dailyAverage) {
        this.dailyAverage = dailyAverage;
    }

    public Long getTotalPerYear() {
        return totalPerYear;
    }

    public void setTotalPerYear(Long totalPerYear) {
        this.totalPerYear = totalPerYear;
    }

    public LocalDate getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(LocalDate modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
}
