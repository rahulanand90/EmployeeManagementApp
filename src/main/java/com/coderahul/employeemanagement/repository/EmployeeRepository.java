package com.coderahul.employeemanagement.repository;

import com.coderahul.employeemanagement.entity.Employee;
import com.coderahul.employeemanagement.entity.Employee.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByFirstNameContainingIgnoreCase(String firstName);

    List<Employee> findByLastNameContainingIgnoreCase(String lastName);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByEmploymentStatus(EmploymentStatus status);

    List<Employee> findByHireDateBetween(LocalDate startDate, LocalDate endDate);

    List<Employee> findBySalaryGreaterThan(BigDecimal salary);

    List<Employee> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);

    @Query("SELECT e FROM Employee e WHERE e.firstName LIKE %:name% OR e.lastName LIKE %:name%")
    List<Employee> findByFirstNameOrLastNameContaining(@Param("name") String name);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :departmentId")
    Long countEmployeesByDepartmentId(@Param("departmentId") Long departmentId);

    @Query(value = "SELECT AVG(salary) FROM employees WHERE department_id = :departmentId", nativeQuery = true)
    BigDecimal findAverageSalaryByDepartment(@Param("departmentId") Long departmentId);

    boolean existsByEmail(String email);
}