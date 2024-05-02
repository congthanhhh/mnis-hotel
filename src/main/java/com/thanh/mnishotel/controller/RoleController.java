package com.thanh.mnishotel.controller;

import com.thanh.mnishotel.exception.RoleAlreadyExistException;
import com.thanh.mnishotel.model.Role;
import com.thanh.mnishotel.model.User;
import com.thanh.mnishotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getRoles(), HttpStatus.FOUND);
    }

    @PostMapping("/create-new-role")
    public ResponseEntity<String> createRole(@RequestBody Role thRole) {
        try{
            roleService.createRole(thRole);
            return ResponseEntity.ok("New role created successfully!");
        }catch (RoleAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId) {
        roleService.deleteRole(roleId);

    }

    @PostMapping("/remove-all-user-from-role/{roleId}")
    public Role removeAllUserFromRole(@PathVariable("roleId") Long roleId) {
        return roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUserFromRole(
            @RequestParam("userId") Long userId,
            @RequestParam("roleId") Long roleId) {
        return roleService.removeUserFromRole(userId, roleId);

    }

    @PostMapping("/assign-user-to-role")
    public User assignUserToRole(
            @RequestParam("userId") Long userId,
            @RequestParam("roleId") Long roleId) {
        return roleService.assignRoleToUser(userId, roleId);

    }

}
