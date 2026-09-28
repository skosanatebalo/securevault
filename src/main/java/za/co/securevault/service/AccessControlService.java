package za.co.securevault.service;

import za.co.securevault.model.Action;
import za.co.securevault.model.Role;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

public class AccessControlService {

    private static final Map<Role, Set<Action>> PERMISSIONS = Map.of(

            Role.ADMIN, EnumSet.allOf(Action.class),

            Role.ANALYST, EnumSet.of(
                    Action.VIEW_ASSETS,
                    Action.ADD_ASSET,
                    Action.VIEW_VULNERABILITIES,
                    Action.ADD_VULNERABILITY,
                    Action.UPDATE_VULNERABILITY
            ),

            Role.VIEWER, EnumSet.of(
                    Action.VIEW_ASSETS,
                    Action.VIEW_VULNERABILITIES
            )
    );

    public boolean isAllowed(Role role, Action action) {
        return PERMISSIONS.getOrDefault(role, Set.of()).contains(action);
    }
}