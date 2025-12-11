package com.innov8.dto;

import com.innov8.model.Personnel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonnelDTO {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String department;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PersonnelDTO from(Personnel personnel) {
        return PersonnelDTO.builder()
                .id(personnel.getId())
                .name(personnel.getName())
                .email(personnel.getEmail())
                .role(personnel.getRole())
                .department(personnel.getDepartment())
                .status(personnel.getStatus() != null ? personnel.getStatus().toString() : null)
                .createdAt(personnel.getCreatedAt())
                .updatedAt(personnel.getUpdatedAt())
                .build();
    }

}
