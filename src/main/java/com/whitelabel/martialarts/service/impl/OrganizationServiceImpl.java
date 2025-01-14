package com.whitelabel.martialarts.service.impl;

import com.whitelabel.martialarts.model.Organization;
import com.whitelabel.martialarts.repository.OrganizationRepository;
import com.whitelabel.martialarts.service.service.OrganizationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public Organization getOrganization(Long id) {
        Optional<Organization> organization = organizationRepository.findById(id);
        return organization.orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Override
    public Organization updateOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public Organization createOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }
}
