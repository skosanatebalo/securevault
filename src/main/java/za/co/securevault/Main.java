package za.co.securevault;

import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.Action;
import za.co.securevault.model.Asset;
import za.co.securevault.model.Role;
import za.co.securevault.model.Severity;
import za.co.securevault.model.User;
import za.co.securevault.model.Vulnerability;
import za.co.securevault.model.VulnerabilityStatus;
import za.co.securevault.repository.AssetRepository;
import za.co.securevault.repository.UserRepository;
import za.co.securevault.repository.VulnerabilityRepository;
import za.co.securevault.service.AccessControlService;
import za.co.securevault.service.AuthService;
import za.co.securevault.validation.InputValidator;

import java.io.Console;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AssetRepository assetRepository = new AssetRepository(databaseManager);
        VulnerabilityRepository vulnerabilityRepository = new VulnerabilityRepository(databaseManager);
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

                runMenu(scanner, user, accessControlService,
                        assetRepository, vulnerabilityRepository, userRepository);

            } else {
                System.out.println("Login failed: invalid username or password.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static void runMenu(Scanner scanner, User user, AccessControlService accessControlService,
                                AssetRepository assetRepository,
                                VulnerabilityRepository vulnerabilityRepository,
                                UserRepository userRepository) throws SQLException {

        while (true) {

            System.out.println("\n--- Menu ---");
            System.out.println("1. View assets");
            System.out.println("2. Add asset");
            System.out.println("3. Delete asset");
            System.out.println("4. View vulnerabilities");
            System.out.println("5. Add vulnerability");
            System.out.println("6. Update vulnerability status");
            System.out.println("7. Create user");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1" -> handleViewAssets(user, accessControlService, assetRepository);
                case "2" -> handleAddAsset(scanner, user, accessControlService, assetRepository);
                case "3" -> handleDeleteAsset(scanner, user, accessControlService, assetRepository);
                case "4" -> handleViewVulnerabilities(user, accessControlService, vulnerabilityRepository);
                case "5" -> handleAddVulnerability(scanner, user, accessControlService, vulnerabilityRepository);
                case "6" -> handleUpdateVulnerability(scanner, user, accessControlService, vulnerabilityRepository);
                case "7" -> handleCreateUser(scanner, user, accessControlService, userRepository);
                case "8" -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void handleViewAssets(User user, AccessControlService accessControlService,
                                         AssetRepository assetRepository) throws SQLException {

        if (!accessControlService.isAllowed(user.getRole(), Action.VIEW_ASSETS)) {
            System.out.println("Access denied: your role cannot view assets.");
            return;
        }

        List<Asset> assets = assetRepository.findAll();

        if (assets.isEmpty()) {
            System.out.println("No assets found.");
            return;
        }

        for (Asset asset : assets) {
            System.out.println(asset.getId() + " | " + asset.getHostname() + " | "
                    + asset.getIpAddress() + " | " + asset.getOperatingSystem()
                    + " | " + asset.getOwner());
        }
    }

    private static void handleAddAsset(Scanner scanner, User user, AccessControlService accessControlService,
                                       AssetRepository assetRepository) throws SQLException {

        if (!accessControlService.isAllowed(user.getRole(), Action.ADD_ASSET)) {
            System.out.println("Access denied: your role cannot add assets.");
            return;
        }

        System.out.print("Hostname: ");
        String hostname = scanner.nextLine().trim();

        System.out.print("IP address: ");
        String ipAddress = scanner.nextLine().trim();

        System.out.print("Operating system: ");
        String os = scanner.nextLine().trim();

        System.out.print("Owner: ");
        String owner = scanner.nextLine().trim();
        try {
            InputValidator.validateHostname(hostname);
            InputValidator.validateIpv4(ipAddress);
            InputValidator.validateText("Operating system", os, 100);
            InputValidator.validateText("Owner", owner, 100);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return;
        }
        Asset newAsset = new Asset(0, hostname, ipAddress, os, owner);

        try {
            Asset saved = assetRepository.save(newAsset);
            System.out.println("Added asset '" + saved.getHostname() + "' with ID " + saved.getId());
        } catch (SQLException e) {
            System.out.println("Failed to add asset: hostname may already exist, or the IP address is invalid.");
        }
    }

    private static void handleDeleteAsset(Scanner scanner, User user, AccessControlService accessControlService,
                                          AssetRepository assetRepository) throws SQLException {

        if (!accessControlService.isAllowed(user.getRole(), Action.DELETE_ASSET)) {
            System.out.println("Access denied: your role cannot delete assets.");
            return;
        }

        System.out.print("Asset ID to delete: ");
        String idInput = scanner.nextLine().trim();

        try {
            long id = Long.parseLong(idInput);
            boolean deleted = assetRepository.deleteById(id);

            if (deleted) {
                System.out.println("Deleted asset " + id + ".");
            } else {
                System.out.println("No asset found with ID " + id + ".");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
    }

    private static void handleViewVulnerabilities(User user, AccessControlService accessControlService,
                                                  VulnerabilityRepository vulnerabilityRepository) throws SQLException {

        if (!accessControlService.isAllowed(user.getRole(), Action.VIEW_VULNERABILITIES)) {
            System.out.println("Access denied: your role cannot view vulnerabilities.");
            return;
        }

        List<Vulnerability> vulnerabilities = vulnerabilityRepository.findAll();

        if (vulnerabilities.isEmpty()) {
            System.out.println("No vulnerabilities found.");
            return;
        }

        for (Vulnerability v : vulnerabilities) {
            System.out.println(v.getId() + " | asset " + v.getAssetId() + " | "
                    + v.getSeverity() + " | " + v.getStatus() + " | " + v.getTitle());
        }
    }

    private static void handleAddVulnerability(Scanner scanner, User user,
                                               AccessControlService accessControlService,
                                               VulnerabilityRepository vulnerabilityRepository) {

        if (!accessControlService.isAllowed(user.getRole(), Action.ADD_VULNERABILITY)) {
            System.out.println("Access denied: your role cannot add vulnerabilities.");
            return;
        }

        System.out.print("Asset ID: ");
        String assetIdInput = scanner.nextLine().trim();

        long assetId;

        try {
            assetId = Long.parseLong(assetIdInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid asset ID.");
            return;
        }

        System.out.print("Title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        try {
            InputValidator.validateText("Title", title, 255);
            InputValidator.validateText("Description", description, 2000);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return;
        }
        System.out.print("Severity (LOW, MEDIUM, HIGH, CRITICAL): ");
        String severityInput = scanner.nextLine().trim().toUpperCase();

        Severity severity;

        try {
            severity = Severity.valueOf(severityInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid severity. Vulnerability not added.");
            return;
        }

        Vulnerability newVulnerability = new Vulnerability(
                0, assetId, title, description, severity, VulnerabilityStatus.OPEN);

        try {
            Vulnerability saved = vulnerabilityRepository.save(newVulnerability);
            System.out.println("Added vulnerability " + saved.getId() + " (status: OPEN).");
        } catch (SQLException e) {
            System.out.println("Failed to add vulnerability: check that the asset ID exists.");
        }
    }

    private static void handleUpdateVulnerability(Scanner scanner, User user,
                                                  AccessControlService accessControlService,
                                                  VulnerabilityRepository vulnerabilityRepository) {

        if (!accessControlService.isAllowed(user.getRole(), Action.UPDATE_VULNERABILITY)) {
            System.out.println("Access denied: your role cannot update vulnerabilities.");
            return;
        }

        System.out.print("Vulnerability ID: ");
        String idInput = scanner.nextLine().trim();

        long id;

        try {
            id = Long.parseLong(idInput);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
            return;
        }

        System.out.print("New status (OPEN, IN_PROGRESS, FIXED): ");
        String statusInput = scanner.nextLine().trim().toUpperCase();

        VulnerabilityStatus status;

        try {
            status = VulnerabilityStatus.valueOf(statusInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Nothing changed.");
            return;
        }

        try {
            boolean updated = vulnerabilityRepository.updateStatus(id, status);

            if (updated) {
                System.out.println("Vulnerability " + id + " is now " + status + ".");
            } else {
                System.out.println("No vulnerability found with ID " + id + ".");
            }

        } catch (SQLException e) {
            System.out.println("Failed to update vulnerability.");
        }
    }

    private static void handleCreateUser(Scanner scanner, User user, AccessControlService accessControlService,
                                         UserRepository userRepository) throws SQLException {

        if (!accessControlService.isAllowed(user.getRole(), Action.MANAGE_USERS)) {
            System.out.println("Access denied: your role cannot manage users.");
            return;
        }

        System.out.print("New username: ");
        String newUsername = scanner.nextLine().trim();

        System.out.print("New password: ");
        String newPassword = scanner.nextLine();
        try {
            InputValidator.validateUsername(newUsername);
            InputValidator.validatePassword(newPassword);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
            return;
        }
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
            System.out.println("Created user '" + created.getUsername() + "' with role " + created.getRole() + ".");
        } catch (SQLException e) {
            System.out.println("Failed to create user: username may already be taken.");
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