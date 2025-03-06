package com.whitelabel.martialarts.repository;

import com.whitelabel.martialarts.model.School;
import com.whitelabel.martialarts.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    Optional<School> findByStripeConnectAccountId(String stripeConnectAccountId);
    
    List<School> findByOrganization(Organization organization);
}
