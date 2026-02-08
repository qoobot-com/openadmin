package com.qoobot.openadmin.admin.service;

import com.qoobot.openadmin.admin.mapper.RolePermissionMapper;
import com.qoobot.openadmin.admin.repository.RoleRepository;
import com.qoobot.openadmin.admin.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
public class RoleServiceSecurityTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private RoleRepository roleRepository;

    @TestConfiguration
    static class Config {
        @Bean
        public RolePermissionMapper rolePermissionMapper() {
            return Mockito.mock(RolePermissionMapper.class);
        }

        @Bean
        public RoleRepository roleRepository() {
            return Mockito.mock(RoleRepository.class);
        }
    }

    @BeforeEach
    public void setupAuth() {
        var auth = new UsernamePasswordAuthenticationToken("admin", "N/A", List.of(new SimpleGrantedAuthority("perm.admin")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testAssignPermissionsAuthorized() {
        roleService.assignPermissions("r1", List.of("perm.read"));
        verify(rolePermissionMapper, times(1)).deleteByRoleId("r1");
    }
}
