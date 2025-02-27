package com.whitelabel.martialarts.repository;

import com.whitelabel.martialarts.model.Student;
import com.whitelabel.martialarts.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByStudent(Student student);
    Optional<Subscription> findByStripeSubscriptionId(String stripeSubscriptionId);
}
