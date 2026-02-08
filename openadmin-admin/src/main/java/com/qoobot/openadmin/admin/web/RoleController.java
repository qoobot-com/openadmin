package com.qoobot.openadmin.admin.web;

import com.qoobot.openadmin.admin.dto.RoleDTO;
import com.qoobot.openadmin.admin.service.RoleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) { this.roleService = roleService; }

    @GetMapping
    public String list(Model model, @RequestParam(required = false) String orgId) {
        List<RoleDTO> roles = roleService.findRolesByOrg(orgId);
        model.addAttribute("roles", roles);
        return "admin/roles/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("role", new RoleDTO());
        return "admin/roles/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("role") RoleDTO dto) {
        roleService.createRole(dto);
        return "redirect:/admin/roles";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        model.addAttribute("role", roleService.findRoleById(id));
        return "admin/roles/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("role") RoleDTO dto) {
        roleService.updateRole(dto);
        return "redirect:/admin/roles";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        roleService.deleteRole(id);
        return "redirect:/admin/roles";
    }

    @GetMapping("/assign/{id}")
    public String assignPermissionsForm(@PathVariable String id, Model model) {
        model.addAttribute("roleId", id);
        model.addAttribute("assigned", roleService.getPermissionsAggregated(id));
        return "admin/roles/assign";
    }

    @PostMapping("/assign/{id}")
    public String assignPermissions(@PathVariable String id, @RequestParam List<String> permissionIds) {
        roleService.assignPermissions(id, permissionIds);
        return "redirect:/admin/roles";
    }
}

