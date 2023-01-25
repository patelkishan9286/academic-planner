package com.group13.academicplannerbackend.repository;

import com.group13.academicplannerbackend.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    VerificationCode findByCodeAndEmail(String code, String email);

    VerificationCode findByEmail(String email);
}
