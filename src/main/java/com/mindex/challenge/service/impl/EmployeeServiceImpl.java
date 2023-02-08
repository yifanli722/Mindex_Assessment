package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.apache.tomcat.jni.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Creating employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(String id) {
        int numberOfReports = 0;
        Set<String> visited = new HashSet<>();
        Stack<String> dfs = new Stack<>();
        Employee rootEmployee = null;
        dfs.push(id);
        while(!dfs.isEmpty()) {
            String current = dfs.pop();
            if(visited.contains(current))
                continue;

            visited.add(current);
            Employee employee = employeeRepository.findByEmployeeId(current);
            if(rootEmployee == null) {
                rootEmployee = employee;
            } else {
                numberOfReports++;
            }

            List<Employee> reportees = employee.getDirectReports();
            if(reportees != null && !reportees.isEmpty())
                reportees
                    .stream()
                    .map(Employee::getEmployeeId)
                    .forEach(dfs::push);

        }
        return new ReportingStructure(rootEmployee, numberOfReports);
    }

    @Override
    public Compensation createCompensationRecord(Compensation compensation) {
        String id = compensation.getEmployeeId();
        if(employeeRepository.findByEmployeeId(id) == null) {
            throw new RuntimeException(String.format("Unable to add compensation for missing employee %s. Add the employee first", id));
        }
        compensation.setEffectiveDate(LocalDateTime.now());
        compensationRepository.insert(compensation);
        return compensation;
    }

    @Override
    public Compensation readCompensationRecord(String id) {
        return compensationRepository.findByEmployeeId(id);
    }


}
