package com.dinakar.ems.service;

import com.dinakar.ems.dto.EmployeeRequest;
import com.dinakar.ems.dto.EmployeeResponse;
import com.dinakar.ems.entity.Department;
import com.dinakar.ems.entity.Employee;
import com.dinakar.ems.exception.ConflictException;
import com.dinakar.ems.exception.ResourceNotFoundException;
import com.dinakar.ems.repository.DepartmentRepository;
import com.dinakar.ems.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.email())) {
            throw new ConflictException("Employee already exists with email: " + request.email());
        }
        Department department = findDepartment(request.departmentId());
        Employee employee = new Employee();
        applyRequest(employee, request, department);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getAll(Long departmentId, Pageable pageable) {
        Page<Employee> page = (departmentId == null)
                ? employeeRepository.findAll(pageable)
                : employeeRepository.findByDepartmentId(departmentId, pageable);
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return toResponse(findEmployee(id));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployee(id);
        if (employeeRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ConflictException("Employee already exists with email: " + request.email());
        }
        Department department = findDepartment(request.departmentId());
        applyRequest(employee, request, department);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void delete(Long id) {
        Employee employee = findEmployee(id);
        employeeRepository.delete(employee);
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    private void applyRequest(Employee employee, EmployeeRequest request, Department department) {
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setSalary(request.salary());
        employee.setDepartment(department);
    }

    private EmployeeResponse toResponse(Employee employee) {
        Department department = employee.getDepartment();
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getSalary(),
                department.getId(),
                department.getName());
    }
}
