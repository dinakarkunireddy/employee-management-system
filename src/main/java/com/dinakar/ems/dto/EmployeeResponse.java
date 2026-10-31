package com.dinakar.ems.dto;

import java.math.BigDecimal;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        BigDecimal salary,
        Long departmentId,
        String departmentName) {
}
