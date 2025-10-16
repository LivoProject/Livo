package com.livo.project.report.service;

import com.livo.project.report.domain.Report;

public interface ReportService {

    //신고 등록 (민영은 등록만 하면 될 것 같음!)
    Report saveReport(Report report);

}
