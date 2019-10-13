package com.salil.shawrtn.domainobject.embedded;

import java.io.Serializable;

public class DateStatistics implements Serializable {

    private Integer dayOfYear;
    private Integer visits;

    public DateStatistics() {
    }

    public DateStatistics(Integer dayOfYear, Integer visits) {
        this.dayOfYear = dayOfYear;
        this.visits = visits;
    }

    public Integer getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(Integer dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public void incrementVisit() {
        this.visits++;
    }

    public Integer getVisits() {
        return visits;
    }

    public void setVisits(Integer visits) {
        this.visits = visits;
    }
}
