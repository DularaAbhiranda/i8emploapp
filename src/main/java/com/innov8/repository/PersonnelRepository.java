package com.innov8.repository;

import com.innov8.model.Personnel;
import com.innov8.model.PersonnelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {
    List<Personnel> findByStatus(PersonnelStatus status);
    List<Personnel> findByDepartmentIgnoreCase(String department);
}
