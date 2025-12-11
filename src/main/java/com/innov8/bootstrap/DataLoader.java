package com.innov8.bootstrap;

import com.innov8.model.Personnel;
import com.innov8.model.PersonnelStatus;
import com.innov8.repository.PersonnelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final PersonnelRepository personnelRepository;

    @Override
    public void run(String... args) throws Exception {
        // Load initial data
        personnelRepository.save(Personnel.builder()
                .name("Amal Perera")
                .email("amal.perera@innov8.lk")
                .role("Security Analyst")
                .department("SOC")
                .status(PersonnelStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        personnelRepository.save(Personnel.builder()
                .name("Nimal Fernando")
                .email("nimal.fernando@innov8.lk")
                .role("Penetration Tester")
                .department("Red Team")
                .status(PersonnelStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        personnelRepository.save(Personnel.builder()
                .name("Kumari Silva")
                .email("kumari.silva@innov8.lk")
                .role("Incident Responder")
                .department("SOC")
                .status(PersonnelStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        personnelRepository.save(Personnel.builder()
                .name("Rohan Jayawardena")
                .email("rohan.j@innov8.lk")
                .role("Security Engineer")
                .department("Engineering")
                .status(PersonnelStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        personnelRepository.save(Personnel.builder()
                .name("Priya Wickramasinghe")
                .email("priya.w@innov8.lk")
                .role("Compliance Officer")
                .department("GRC")
                .status(PersonnelStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        System.out.println("Sample data loaded successfully!");
    }

}
