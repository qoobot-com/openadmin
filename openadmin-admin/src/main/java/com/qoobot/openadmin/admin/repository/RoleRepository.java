package com.qoobot.openadmin.admin.repository;

import com.qoobot.openadmin.admin.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    List<Role> findByOrganizationId(String organizationId);
}
