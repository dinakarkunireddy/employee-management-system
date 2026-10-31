package com.dinakar.ems.service;

import com.dinakar.ems.dto.EmployeeRequest;
import com.dinakar.ems.dto.EmployeeResponse;
import com.dinakar.ems.entity.Department;
import com.dinakar.ems.entity.Employee;
import com.dinakar.ems.exception.ConflictException;
import com.dinakar.ems.exception.ResourceNotFoundException;
import com.dinakar.ems.repository.DepartmentRepository;
import com.dinakar.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void create_savesEmployeeAndReturnsResponse() {
        Department department = new Department();
        department.setId(1L);
        department.setName("Engineering");
        EmployeeRequest request = new EmployeeRequest("Ravi Kumar", "ravi@example.com", new BigDecimal("55000.00"), 1L);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> {
            Employee saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        EmployeeResponse response = employeeService.create(request);

        assertEquals(10L, response.id());
        assertEquals("Ravi Kumar", response.name());
        assertEquals("ravi@example.com", response.email());
        assertEquals("Engineering", response.departmentName());
    }

    @Test
    void create_throwsConflictWhenEmailAlreadyExists() {
        EmployeeRequest request = new EmployeeRequest("Ravi Kumar", "ravi@example.com", new BigDecimal("55000.00"), 1L);
        when(employeeRepository.existsByEmail("ravi@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> employeeService.create(request));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getById_throwsNotFoundWhenEmployeeMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getById(99L));
    }
}
