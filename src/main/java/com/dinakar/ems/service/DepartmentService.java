package com.dinakar.ems.service;

import com.dinakar.ems.dto.DepartmentRequest;
import com.dinakar.ems.dto.DepartmentResponse;
import com.dinakar.ems.entity.Department;
import com.dinakar.ems.exception.ConflictException;
import com.dinakar.ems.exception.ResourceNotFoundException;
import com.dinakar.ems.repository.DepartmentRepository;
import com.dinakar.ems.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.name())) {
            throw new ConflictException("Department already exists with name: " + request.name());
        }
        Department department = new Department();
        department.setName(request.name());
        return toResponse(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return toResponse(findDepartment(id));
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findDepartment(id);
        if (departmentRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new ConflictException("Department already exists with name: " + request.name());
        }
        department.setName(request.name());
        return toResponse(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        Department department = findDepartment(id);
        if (employeeRepository.countByDepartmentId(id) > 0) {
            throw new ConflictException("Cannot delete department with id " + id + " because it still has employees");
        }
        departmentRepository.delete(department);
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    private DepartmentResponse toResponse(Department department) {
        return new DepartmentResponse(department.getId(), department.getName());
    }
}
