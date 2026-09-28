package com.example.challantrack.repository;

import com.example.challantrack.entity.ViolationType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationTypeRepository extends JpaRepository<ViolationType, Long> {

    Optional<ViolationType> findByViolationCode(String violationCode);

    boolean existsByViolationCode(String violationCode);
}
