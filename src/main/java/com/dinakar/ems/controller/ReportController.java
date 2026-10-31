package com.dinakar.ems.controller;

import com.dinakar.ems.report.DepartmentSummary;
import com.dinakar.ems.report.EmployeeReportDao;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final EmployeeReportDao employeeReportDao;

    public ReportController(EmployeeReportDao employeeReportDao) {
        this.employeeReportDao = employeeReportDao;
    }

    @GetMapping("/department-summary")
    public List<DepartmentSummary> departmentSummary() {
        return employeeReportDao.getDepartmentSummary();
    }
}
