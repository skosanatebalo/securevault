package za.co.securevault;

import org.junit.jupiter.api.Test;
import za.co.securevault.model.Action;
import za.co.securevault.model.Role;
import za.co.securevault.service.AccessControlService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccessControlServiceTest {

    private final AccessControlService accessControlService = new AccessControlService();

    @Test
    void adminCanDoEverything() {
        assertTrue(accessControlService.isAllowed(Role.ADMIN, Action.VIEW_ASSETS));
        assertTrue(accessControlService.isAllowed(Role.ADMIN, Action.ADD_ASSET));
        assertTrue(accessControlService.isAllowed(Role.ADMIN, Action.DELETE_ASSET));
        assertTrue(accessControlService.isAllowed(Role.ADMIN, Action.MANAGE_USERS));
    }

    @Test
    void analystCanViewAndAddButNotDeleteOrManageUsers() {
        assertTrue(accessControlService.isAllowed(Role.ANALYST, Action.VIEW_ASSETS));
        assertTrue(accessControlService.isAllowed(Role.ANALYST, Action.ADD_ASSET));
        assertFalse(accessControlService.isAllowed(Role.ANALYST, Action.DELETE_ASSET));
        assertFalse(accessControlService.isAllowed(Role.ANALYST, Action.MANAGE_USERS));
    }

    @Test
    void viewerCanOnlyView() {
        assertTrue(accessControlService.isAllowed(Role.VIEWER, Action.VIEW_ASSETS));
        assertFalse(accessControlService.isAllowed(Role.VIEWER, Action.ADD_ASSET));
        assertFalse(accessControlService.isAllowed(Role.VIEWER, Action.DELETE_ASSET));
        assertFalse(accessControlService.isAllowed(Role.VIEWER, Action.MANAGE_USERS));
    }
}
