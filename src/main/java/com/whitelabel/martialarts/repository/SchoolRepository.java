package com.whitelabel.martialarts.repository;

import com.whitelabel.martialarts.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByStripeConnectAccountId(String stripeConnectAccountId);
}
