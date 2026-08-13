package org.example.sentinelcorebackend.Repository;

import org.example.sentinelcorebackend.Entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatus(Alert.AlertStatus status);
}