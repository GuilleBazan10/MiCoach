package com.micoach.admin.presentation;

import com.micoach.admin.application.port.in.AdminUseCase;
import com.micoach.admin.application.port.in.AdminUseCase.AuditLogFilter;
import com.micoach.admin.application.port.in.AdminUseCase.PermissionData;
import com.micoach.admin.application.port.in.AdminUseCase.RoleData;
import com.micoach.admin.presentation.AdminDtos.AuditLogResponse;
import com.micoach.admin.presentation.AdminDtos.PermissionRequest;
import com.micoach.admin.presentation.AdminDtos.PermissionResponse;
import com.micoach.admin.presentation.AdminDtos.RoleRequest;
import com.micoach.admin.presentation.AdminDtos.RoleResponse;
import com.micoach.admin.presentation.AdminDtos.UserRoleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contratos REST del módulo admin (base path /api/v1/admin). Gobernanza interna: roles,
 * permisos, asignaciones y auditoría. Requiere JWT + rol ROLE_ADMIN (los roles del token
 * ahora vienen de {@code admin_user_roles}, ver {@code AdminUserRoleProvider}).
 */
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminUseCase useCase;

    public AdminController(AdminUseCase useCase) {
        this.useCase = useCase;
    }

    // ------------------------- Roles y permisos -------------------------

    @GetMapping("/roles")
    public List<RoleResponse> listRoles() {
        return useCase.listRoles().stream().map(RoleResponse::from).toList();
    }

    @PostMapping("/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleResponse createRole(@Valid @RequestBody RoleRequest request) {
        RoleData data = new RoleData(request.code(), request.name(), request.description());
        return RoleResponse.from(useCase.createRole(data));
    }

    @DeleteMapping("/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRole(@PathVariable Long roleId) {
        useCase.deleteRole(roleId);
    }

    @GetMapping("/permissions")
    public List<PermissionResponse> listPermissions() {
        return useCase.listPermissions().stream().map(PermissionResponse::from).toList();
    }

    @PostMapping("/permissions")
    @ResponseStatus(HttpStatus.CREATED)
    public PermissionResponse createPermission(@Valid @RequestBody PermissionRequest request) {
        PermissionData data = new PermissionData(request.code(), request.name(), request.description());
        return PermissionResponse.from(useCase.createPermission(data));
    }

    @PostMapping("/roles/{roleId}/permissions/{permissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignPermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        useCase.assignPermission(roleId, permissionId);
    }

    @DeleteMapping("/roles/{roleId}/permissions/{permissionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignPermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        useCase.unassignPermission(roleId, permissionId);
    }

    // ------------------------- Roles de usuario -------------------------

    @GetMapping("/users/{userId}/roles")
    public List<UserRoleResponse> listUserRoles(@PathVariable Long userId) {
        return useCase.listUserRoles(userId).stream().map(UserRoleResponse::from).toList();
    }

    @PostMapping("/users/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        useCase.assignRole(userId, roleId);
    }

    @DeleteMapping("/users/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        useCase.unassignRole(userId, roleId);
    }

    // ------------------------- Auditoría -------------------------

    @GetMapping("/audit-logs")
    public List<AuditLogResponse> listAuditLogs(@RequestParam(required = false) Long userId,
                                                @RequestParam(required = false) String entityType) {
        return useCase.listAuditLogs(new AuditLogFilter(userId, entityType)).stream()
                .map(AuditLogResponse::from).toList();
    }
}
