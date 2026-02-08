package com.qoobot.openadmin.admin.web;

import com.qoobot.openadmin.admin.dto.UserDTO;
import com.qoobot.openadmin.admin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.qoobot.openadmin.core.paging.Pageable;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public String list(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        var pageable = Pageable.of(page, size, "");
        var users = userService.findUsersByPage(pageable);
        model.addAttribute("page", users);
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "admin/users/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("user") UserDTO userDTO) {
        userService.createUser(userDTO);
        return "redirect:/admin/users";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable String id, Model model) {
        model.addAttribute("user", userService.findUserById(id));
        return "admin/users/edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute("user") UserDTO userDTO) {
        userService.updateUser(userDTO);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
