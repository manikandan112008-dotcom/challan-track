package com.example.challantrack.repository;

import com.example.challantrack.entity.Challan;
import com.example.challantrack.entity.PaymentRecord;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByChallan(Challan challan);

    boolean existsByPaymentReference(String paymentReference);
}
