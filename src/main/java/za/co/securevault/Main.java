package za.co.securevault;

import za.co.securevault.database.DatabaseManager;
import za.co.securevault.model.User;
import za.co.securevault.repository.UserRepository;
import za.co.securevault.service.AuthService;

import java.io.Console;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManager();
        UserRepository userRepository = new UserRepository(databaseManager);
        AuthService authService = new AuthService(userRepository);

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
            } else {
                System.out.println("Login failed: invalid username or password.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred while logging in.");
            e.printStackTrace();
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
