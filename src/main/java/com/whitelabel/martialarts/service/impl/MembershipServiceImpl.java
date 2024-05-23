package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Membership;
import com.whitelabel.martialarts.repository.MembershipRepository;
import com.whitelabel.martialarts.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    @Override
    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    @Override
    public Membership getMembershipById(Long id) {
        return membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership not found with id: " + id));
    }

    @Override
    public Membership createMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    @Override
    public Membership updateMembership(Long id, Membership membership) {
        Membership existingMembership = membershipRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membership not found with id: " + id));
        existingMembership.setMembershipType(membership.getMembershipType());
        existingMembership.setStartDate(membership.getStartDate());
        existingMembership.setEndDate(membership.getEndDate());
        existingMembership.setStatus(membership.getStatus());
        existingMembership.setPaymentStatus(membership.getPaymentStatus());
        existingMembership.setStudent(membership.getStudent());
        return membershipRepository.save(existingMembership);
    }

    @Override
    public void deleteMembership(Long id) {
        membershipRepository.deleteById(id);
    }
}
