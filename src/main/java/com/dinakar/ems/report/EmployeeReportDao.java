package com.dinakar.ems.report;

import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain JDBC data access (Connection, PreparedStatement, ResultSet) used for
 * the reporting query. The rest of the application uses Spring Data JPA.
 */
@Repository
public class EmployeeReportDao {

    private static final String DEPARTMENT_SUMMARY_SQL =
            "SELECT d.name AS department_name, "
                    + "COUNT(e.id) AS employee_count, "
                    + "COALESCE(AVG(e.salary), 0) AS average_salary "
                    + "FROM departments d "
                    + "LEFT JOIN employees e ON e.department_id = d.id "
                    + "GROUP BY d.id, d.name "
                    + "ORDER BY d.name";

    private final DataSource dataSource;

    public EmployeeReportDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<DepartmentSummary> getDepartmentSummary() {
        List<DepartmentSummary> summaries = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DEPARTMENT_SUMMARY_SQL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                BigDecimal average = resultSet.getBigDecimal("average_salary").setScale(2, RoundingMode.HALF_UP);
                summaries.add(new DepartmentSummary(
                        resultSet.getString("department_name"),
                        resultSet.getLong("employee_count"),
                        average));
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to generate department summary report", ex);
        }
        return summaries;
    }
}
