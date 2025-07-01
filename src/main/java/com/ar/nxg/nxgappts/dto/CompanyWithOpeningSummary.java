package com.ar.nxg.nxgappts.dto;

import com.ar.nxg.nxgappts.domain.Company;
import com.ar.nxg.nxgappts.domain.OpeningHour;
import lombok.Getter;

import java.util.List;

@Getter
public class CompanyWithOpeningSummary {
    private Company company;
    private List<OpeningSummary> summaries;

    public CompanyWithOpeningSummary(Company company, List<OpeningSummary> summaries) {
        this.company = company;
        this.summaries = summaries;
    }

    // Getters
}

