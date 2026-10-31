package com.dinakar.ems.report;

import java.math.BigDecimal;

public record DepartmentSummary(String departmentName, long employeeCount, BigDecimal averageSalary) {
}
