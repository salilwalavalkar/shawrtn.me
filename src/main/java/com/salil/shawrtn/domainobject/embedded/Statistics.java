package com.salil.shawrtn.domainobject.embedded;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Statistics implements Serializable {

    private List<DateStatistics> dateStats = new ArrayList<>();

    public List<DateStatistics> getDateStats() {
        return dateStats;
    }

    public void setDateStats(List<DateStatistics> dateStats) {
        this.dateStats = dateStats;
    }
}
