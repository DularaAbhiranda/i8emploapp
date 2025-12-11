package com.innov8.service;

import com.innov8.model.Personnel;
import com.innov8.model.PersonnelStatus;
import com.innov8.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.MDC;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonnelService {

    private final PersonnelRepository personnelRepository;

    public List<Personnel> getAllPersonnel() {
        long startTime = System.currentTimeMillis();
        
        // Simulate jitter: 10% of requests take an extra 1000ms
        if (Math.random() < 0.10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        List<Personnel> personnel = personnelRepository.findAll();
        long executionTime = System.currentTimeMillis() - startTime;

        MDC.put("status", "SUCCESS");
        MDC.put("executionTime", String.valueOf(executionTime));
        log.info("Retrieved {} personnel records in {}ms", personnel.size(), executionTime);

        return personnel;
    }

    public Optional<Personnel> getPersonnelById(Long id) {
        long startTime = System.currentTimeMillis();
        
        Optional<Personnel> personnel = personnelRepository.findById(id);
        long executionTime = System.currentTimeMillis() - startTime;

        MDC.put("status", personnel.isPresent() ? "SUCCESS" : "NOT_FOUND");
        MDC.put("executionTime", String.valueOf(executionTime));
        log.info("Retrieved personnel with id: {}", id);

        return personnel;
    }

    public Personnel createPersonnel(Personnel personnel) {
        long startTime = System.currentTimeMillis();

        Personnel saved = personnelRepository.save(personnel);
        long executionTime = System.currentTimeMillis() - startTime;

        MDC.put("status", "CREATED");
        MDC.put("executionTime", String.valueOf(executionTime));
        log.info("Created new personnel: {}", saved.getId());

        return saved;
    }

    public Personnel updatePersonnel(Long id, Personnel personnelUpdate) {
        long startTime = System.currentTimeMillis();

        Optional<Personnel> existing = personnelRepository.findById(id);
        
        if (existing.isEmpty()) {
            MDC.put("status", "NOT_FOUND");
            log.warn("Personnel not found for update: {}", id);
            throw new RuntimeException("Personnel not found");
        }

        Personnel personnel = existing.get();
        personnel.setName(personnelUpdate.getName());
        personnel.setEmail(personnelUpdate.getEmail());
        personnel.setRole(personnelUpdate.getRole());
        personnel.setDepartment(personnelUpdate.getDepartment());
        personnel.setStatus(personnelUpdate.getStatus());

        Personnel updated = personnelRepository.save(personnel);
        long executionTime = System.currentTimeMillis() - startTime;

        MDC.put("status", "UPDATED");
        MDC.put("executionTime", String.valueOf(executionTime));
        log.info("Updated personnel: {}", id);

        return updated;
    }

    public void deletePersonnel(Long id) {
        long startTime = System.currentTimeMillis();

        if (!personnelRepository.existsById(id)) {
            MDC.put("status", "NOT_FOUND");
            log.warn("Personnel not found for deletion: {}", id);
            throw new RuntimeException("Personnel not found");
        }

        personnelRepository.deleteById(id);
        long executionTime = System.currentTimeMillis() - startTime;

        MDC.put("status", "DELETED");
        MDC.put("executionTime", String.valueOf(executionTime));
        log.info("Deleted personnel: {}", id);
    }

    public List<Personnel> getInactivePersonnel() {
        return personnelRepository.findByStatus(PersonnelStatus.INACTIVE);
    }

}
