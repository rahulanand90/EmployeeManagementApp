package com.coderahul.employeemanagement.config;

import com.coderahul.employeemanagement.entity.Department;
import com.coderahul.employeemanagement.entity.Employee;
import com.coderahul.employeemanagement.entity.Employee.EmploymentStatus;
import com.coderahul.employeemanagement.repository.DepartmentRepository;
import com.coderahul.employeemanagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public DataLoader(DepartmentRepository departmentRepository, 
                     EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadSampleData();
    }

    private void loadSampleData() {
        // Create Departments
        Department itDept = new Department("Information Technology", "Handles all IT operations and software development");
        Department hrDept = new Department("Human Resources", "Manages employee relations and recruitment");
        Department financeDept = new Department("Finance", "Manages company finances and accounting");
        Department marketingDept = new Department("Marketing", "Handles marketing campaigns and customer relations");

        departmentRepository.save(itDept);
        departmentRepository.save(hrDept);
        departmentRepository.save(financeDept);
        departmentRepository.save(marketingDept);

        // Create Employees for IT Department
        Employee emp1 = new Employee("John", "Doe", "john.doe@company.com", "+1234567890", 
                                   LocalDate.of(2022, 1, 15), new BigDecimal("75000.00"));
        emp1.setDepartment(itDept);
        emp1.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Employee emp2 = new Employee("Jane", "Smith", "jane.smith@company.com", "+1234567891", 
                                   LocalDate.of(2021, 3, 20), new BigDecimal("85000.00"));
        emp2.setDepartment(itDept);
        emp2.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Employee emp3 = new Employee("Mike", "Johnson", "mike.johnson@company.com", "+1234567892", 
                                   LocalDate.of(2020, 6, 10), new BigDecimal("95000.00"));
        emp3.setDepartment(itDept);
        emp3.setEmploymentStatus(EmploymentStatus.ACTIVE);

        // Create Employees for HR Department
        Employee emp4 = new Employee("Sarah", "Wilson", "sarah.wilson@company.com", "+1234567893", 
                                   LocalDate.of(2019, 8, 25), new BigDecimal("65000.00"));
        emp4.setDepartment(hrDept);
        emp4.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Employee emp5 = new Employee("David", "Brown", "david.brown@company.com", "+1234567894", 
                                   LocalDate.of(2021, 11, 5), new BigDecimal("60000.00"));
        emp5.setDepartment(hrDept);
        emp5.setEmploymentStatus(EmploymentStatus.ACTIVE);

        // Create Employees for Finance Department
        Employee emp6 = new Employee("Lisa", "Garcia", "lisa.garcia@company.com", "+1234567895", 
                                   LocalDate.of(2020, 2, 14), new BigDecimal("70000.00"));
        emp6.setDepartment(financeDept);
        emp6.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Employee emp7 = new Employee("Robert", "Miller", "robert.miller@company.com", "+1234567896", 
                                   LocalDate.of(2018, 9, 30), new BigDecimal("80000.00"));
        emp7.setDepartment(financeDept);
        emp7.setEmploymentStatus(EmploymentStatus.ACTIVE);

        // Create Employees for Marketing Department
        Employee emp8 = new Employee("Emily", "Davis", "emily.davis@company.com", "+1234567897", 
                                   LocalDate.of(2022, 4, 12), new BigDecimal("55000.00"));
        emp8.setDepartment(marketingDept);
        emp8.setEmploymentStatus(EmploymentStatus.ACTIVE);

        Employee emp9 = new Employee("James", "Anderson", "james.anderson@company.com", "+1234567898", 
                                   LocalDate.of(2021, 7, 8), new BigDecimal("62000.00"));
        emp9.setDepartment(marketingDept);
        emp9.setEmploymentStatus(EmploymentStatus.ACTIVE);

        // One terminated employee
        Employee emp10 = new Employee("Alex", "Taylor", "alex.taylor@company.com", "+1234567899", 
                                    LocalDate.of(2019, 12, 1), new BigDecimal("58000.00"));
        emp10.setDepartment(marketingDept);
        emp10.setEmploymentStatus(EmploymentStatus.TERMINATED);

        // Save all employees
        employeeRepository.save(emp1);
        employeeRepository.save(emp2);
        employeeRepository.save(emp3);
        employeeRepository.save(emp4);
        employeeRepository.save(emp5);
        employeeRepository.save(emp6);
        employeeRepository.save(emp7);
        employeeRepository.save(emp8);
        employeeRepository.save(emp9);
        employeeRepository.save(emp10);

        System.out.println("Sample data loaded successfully!");
    }
}