package za.co.securevault;

import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Action;
import za.co.securevault.model.Role;
import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;
import za.co.securevault.service.AccessControlService;
import za.co.securevault.service.AuthService;

import java.io.Console;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AuthService authService = new AuthService(userRepository);
        AccessControlService accessControlService = new AccessControlService();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        String username = scanner.nextLine();

        String password = readPassword(scanner);

        try {
            Optional<User> result = authService.login(username, password);

            if (result.isPresent()) {
                User user = result.get();
                System.out.println("Login successful. Welcome, " + user.getUsername()
                        + " (role: " + user.getRole() + ")");
                showMenu(user, accessControlService);

                if (accessControlService.isAllowed(user.getRole(), Action.MANAGE_USERS)) {
                    offerCreateUser(scanner, userRepository);
                }

            } else {
                System.out.println("Login failed: invalid username or password.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred while logging in.");
            e.printStackTrace();
        }
    }

    private static void showMenu(User user, AccessControlService accessControlService) {

        System.out.println("\nAvailable actions:");

        for (Action action : Action.values()) {
            String status = accessControlService.isAllowed(user.getRole(), action)
                    ? "ALLOWED"
                    : "denied";
            System.out.println("  " + action + " — " + status);
        }
    }

    private static void offerCreateUser(Scanner scanner, UserRepository userRepository) {

        System.out.print("\nCreate a new user? (y/n): ");
        String answer = scanner.nextLine().trim().toLowerCase();

        if (!answer.equals("y")) {
            return;
        }

        System.out.print("New username: ");
        String newUsername = scanner.nextLine().trim();

        System.out.print("New password: ");
        String newPassword = scanner.nextLine();

        System.out.print("Role (ADMIN, ANALYST, VIEWER): ");
        String roleInput = scanner.nextLine().trim().toUpperCase();

        Role role;

        try {
            role = Role.valueOf(roleInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role. User not created.");
            return;
        }

        try {
            User created = userRepository.createUser(newUsername, newPassword, role);
            System.out.println("Created user '" + created.getUsername()
                    + "' with role " + created.getRole() + ".");
        } catch (SQLException e) {
            System.out.println("Failed to create user — username may already be taken.");
        }
    }

    private static String readPassword(Scanner scanner) {

        Console console = System.console();

        if (console != null) {
            char[] passwordChars = console.readPassword("Password: ");
            return new String(passwordChars);
        }

        System.out.print("Password: ");
        return scanner.nextLine();
    }
}