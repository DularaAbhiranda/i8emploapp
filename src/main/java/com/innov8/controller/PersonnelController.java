package com.innov8.controller;

import com.innov8.dto.PersonnelDTO;
import com.innov8.model.Personnel;
import com.innov8.service.PersonnelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.MDC;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/personnel")
@RequiredArgsConstructor
@Slf4j
public class PersonnelController {

    private final PersonnelService personnelService;

    @GetMapping
    public ResponseEntity<List<PersonnelDTO>> getAllPersonnel() {
        MDC.put("endpoint", "GET /personnel");
        try {
            List<PersonnelDTO> personnel = personnelService.getAllPersonnel()
                    .stream()
                    .map(PersonnelDTO::from)
                    .collect(Collectors.toList());
            
            MDC.put("returnCount", String.valueOf(personnel.size()));
            return ResponseEntity.ok(personnel);
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonnelDTO> getPersonnelById(@PathVariable Long id) {
        MDC.put("endpoint", "GET /personnel/{id}");
        MDC.put("personnelId", String.valueOf(id));
        
        try {
            return personnelService.getPersonnelById(id)
                    .map(p -> ResponseEntity.ok(PersonnelDTO.from(p)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } finally {
            MDC.clear();
        }
    }

    @PostMapping
    public ResponseEntity<PersonnelDTO> createPersonnel(@RequestBody PersonnelDTO dto) {
        MDC.put("endpoint", "POST /personnel");
        MDC.put("action", "CREATE");
        
        try {
            Personnel personnel = new Personnel();
            personnel.setName(dto.getName());
            personnel.setEmail(dto.getEmail());
            personnel.setRole(dto.getRole());
            personnel.setDepartment(dto.getDepartment());

            Personnel created = personnelService.createPersonnel(personnel);
            return ResponseEntity.status(HttpStatus.CREATED).body(PersonnelDTO.from(created));
        } finally {
            MDC.clear();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonnelDTO> updatePersonnel(@PathVariable Long id, @RequestBody PersonnelDTO dto) {
        MDC.put("endpoint", "PUT /personnel/{id}");
        MDC.put("action", "UPDATE");
        MDC.put("personnelId", String.valueOf(id));
        
        try {
            Personnel personnel = new Personnel();
            personnel.setName(dto.getName());
            personnel.setEmail(dto.getEmail());
            personnel.setRole(dto.getRole());
            personnel.setDepartment(dto.getDepartment());

            Personnel updated = personnelService.updatePersonnel(id, personnel);
            return ResponseEntity.ok(PersonnelDTO.from(updated));
        } finally {
            MDC.clear();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonnel(@PathVariable Long id) {
        MDC.put("endpoint", "DELETE /personnel/{id}");
        MDC.put("action", "DELETE");
        MDC.put("personnelId", String.valueOf(id));
        
        try {
            personnelService.deletePersonnel(id);
            return ResponseEntity.noContent().build();
        } finally {
            MDC.clear();
        }
    }

}
