package com.whitelabel.martialarts.service.service;

import com.whitelabel.martialarts.model.Membership;
import java.util.List;

public interface MembershipService {
    List<Membership> getAllMemberships();
    Membership getMembershipById(Long id);
    Membership createMembership(Membership membership);
    Membership updateMembership(Long id, Membership membership);
    void deleteMembership(Long id);
}
