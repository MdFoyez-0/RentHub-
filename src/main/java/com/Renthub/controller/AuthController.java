package com.Renthub.controller;

import com.Renthub.entity.User;
import com.Renthub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.lang.reflect.Method;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Storage for active tokens
    private Map<String, Long> tokenStorage = new HashMap<>();

    // ✅ WORKING LOGIN ENDPOINT (POST)
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Map<String, Object> response = new HashMap<>();

        try {
            // Get all users and find by email
            List<User> allUsers = userRepository.findAll();
            User foundUser = null;

            for (User user : allUsers) {
                if (email.equals(getUserEmail(user))) {
                    foundUser = user;
                    break;
                }
            }

            if (foundUser != null) {
                String storedPassword = getUserPassword(foundUser);

                // Check password (simple comparison for now)
                if (password.equals(storedPassword)) {
                    // Generate token
                    String token = "token_" + System.currentTimeMillis();
                    tokenStorage.put(token, foundUser.getId());

                    // Success response
                    response.put("success", true);
                    response.put("message", "Login successful");
                    response.put("token", token);

                    // User data
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", foundUser.getId());
                    userData.put("email", getUserEmail(foundUser));
                    userData.put("role", getUserRole(foundUser));
                    userData.put("name", getUserName(foundUser));

                    response.put("user", userData);

                    System.out.println("✅ Login success: " + email);
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid email or password");
                    System.out.println("❌ Login failed - wrong password: " + email);
                }
            } else {
                response.put("success", false);
                response.put("message", "Invalid email or password");
                System.out.println("❌ Login failed - user not found: " + email);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    // ✅ WORKING REGISTER ENDPOINT (POST) - FIXED!
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");
            String fullName = request.get("fullName");
            String role = request.get("role");

            // Validate input
            if (email == null || email.isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return response;
            }

            if (password == null || password.length() < 8) {
                response.put("success", false);
                response.put("message", "Password must be at least 8 characters");
                return response;
            }

            // Check if email exists
            List<User> allUsers = userRepository.findAll();
            boolean emailExists = false;

            for (User user : allUsers) {
                if (email.equals(getUserEmail(user))) {
                    emailExists = true;
                    break;
                }
            }

            if (emailExists) {
                response.put("success", false);
                response.put("message", "Email already exists");
                return response;
            }

            // Create new user - SIMPLIFIED VERSION
            User newUser = new User();

            // Try to set fields directly first (most reliable)
            try {
                // Method 1: Try direct method calls
                newUser.getClass().getMethod("setEmail", String.class).invoke(newUser, email);
                newUser.getClass().getMethod("setPassword", String.class).invoke(newUser, password);
                newUser.getClass().getMethod("setRole", String.class).invoke(newUser, role != null ? role.toUpperCase() : "TENANT");

                // Try different name setters
                if (fullName != null && !fullName.trim().isEmpty()) {
                    try {
                        newUser.getClass().getMethod("setName", String.class).invoke(newUser, fullName);
                    } catch (NoSuchMethodException e1) {
                        try {
                            newUser.getClass().getMethod("setFullName", String.class).invoke(newUser, fullName);
                        } catch (NoSuchMethodException e2) {
                            System.out.println("⚠️ Could not set name field");
                        }
                    }
                }

            } catch (Exception e) {
                System.out.println("Direct method failed, trying reflection...");
                // Fallback to reflection
                setFieldWithReflection(newUser, email, password, fullName, role);
            }

            // Save user
            User savedUser = userRepository.save(newUser);

            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", savedUser.getId());

            System.out.println("✅ New user registered: " + email);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    // ✅ TEST ENDPOINT (GET)
    @GetMapping("/test")
    public String test() {
        long userCount = userRepository.count();
        return "✅ Auth API Working! Total users in DB: " + userCount;
    }

    // ✅ DEBUG USERS ENDPOINT (GET)
    @GetMapping("/debug/users")
    public List<Map<String, Object>> getAllUsersDebug() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", getUserEmail(user));
            userMap.put("name", getUserName(user));
            userMap.put("role", getUserRole(user));
            // Don't show password in debug output
            result.add(userMap);
        }

        return result;
    }

    // ✅ CREATE TEST USERS ENDPOINT (POST) - NEW!
    @PostMapping("/create-test-users")
    public Map<String, Object> createTestUsers() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> createdUsers = new ArrayList<>();

        try {
            // Test users to create
            String[][] testUsers = {
                    {"tenant@test.com", "test1234", "Test Tenant", "tenant"},
                    {"landlord@test.com", "test1234", "Test Landlord", "landlord"},
                    {"admin@renthub.com", "admin123", "Administrator", "admin"}
            };

            for (String[] userData : testUsers) {
                String email = userData[0];
                String password = userData[1];
                String name = userData[2];
                String role = userData[3];

                // Check if user already exists
                boolean exists = false;
                List<User> allUsers = userRepository.findAll();
                for (User user : allUsers) {
                    if (email.equals(getUserEmail(user))) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // Create registration request
                    Map<String, String> regRequest = new HashMap<>();
                    regRequest.put("email", email);
                    regRequest.put("password", password);
                    regRequest.put("fullName", name);
                    regRequest.put("role", role);

                    // Register the user
                    Map<String, Object> regResult = register(regRequest);

                    if (regResult.get("success").equals(true)) {
                        Map<String, Object> createdUser = new HashMap<>();
                        createdUser.put("email", email);
                        createdUser.put("name", name);
                        createdUser.put("role", role);
                        createdUser.put("id", regResult.get("userId"));
                        createdUsers.add(createdUser);

                        System.out.println("✅ Created test user: " + email);
                    } else {
                        System.out.println("❌ Failed to create " + email + ": " + regResult.get("message"));
                    }
                } else {
                    System.out.println("⚠️ User already exists: " + email);
                }
            }

            response.put("success", true);
            response.put("message", "Test users created");
            response.put("created", createdUsers);
            response.put("total_users_now", userRepository.count());

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    // ✅ CHECK CREDENTIALS ENDPOINT (GET) - NEW!
    @GetMapping("/check-credentials")
    public Map<String, Object> checkCredentials(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<User> allUsers = userRepository.findAll();
            User foundUser = null;

            for (User user : allUsers) {
                if (email.equals(getUserEmail(user))) {
                    foundUser = user;
                    break;
                }
            }

            if (foundUser != null) {
                response.put("exists", true);
                response.put("email", getUserEmail(foundUser));
                response.put("role", getUserRole(foundUser));
                response.put("name", getUserName(foundUser));
                response.put("id", foundUser.getId());
            } else {
                response.put("exists", false);
                response.put("message", "User not found in database");

                // List all available emails
                List<String> allEmails = new ArrayList<>();
                for (User user : allUsers) {
                    allEmails.add(getUserEmail(user));
                }
                response.put("available_users", allEmails);
                response.put("total_users", allUsers.size());
            }

        } catch (Exception e) {
            response.put("error", e.getMessage());
        }

        return response;
    }

    // ========== HELPER METHODS ==========

    // Get user email
    private String getUserEmail(User user) {
        try {
            return (String) user.getClass().getMethod("getEmail").invoke(user);
        } catch (Exception e) {
            try {
                return (String) user.getClass().getMethod("getMail").invoke(user);
            } catch (Exception e2) {
                return "Unknown";
            }
        }
    }

    // Get user password
    private String getUserPassword(User user) {
        try {
            return (String) user.getClass().getMethod("getPassword").invoke(user);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    // Get user role
    private String getUserRole(User user) {
        try {
            return (String) user.getClass().getMethod("getRole").invoke(user);
        } catch (Exception e) {
            return "Unknown";
        }
    }

    // Get user name
    private String getUserName(User user) {
        try {
            return (String) user.getClass().getMethod("getName").invoke(user);
        } catch (Exception e) {
            try {
                return (String) user.getClass().getMethod("getFullName").invoke(user);
            } catch (Exception e2) {
                try {
                    return (String) user.getClass().getMethod("getUsername").invoke(user);
                } catch (Exception e3) {
                    return "Unknown";
                }
            }
        }
    }

    // Simplified field setter
    private void setFieldWithReflection(User user, String email, String password, String name, String role) {
        try {
            // Try to find and call setters
            Method[] methods = user.getClass().getMethods();

            for (Method method : methods) {
                if (method.getName().equals("setEmail") && method.getParameterCount() == 1) {
                    method.invoke(user, email);
                }
                if (method.getName().equals("setPassword") && method.getParameterCount() == 1) {
                    method.invoke(user, password);
                }
                if (method.getName().equals("setRole") && method.getParameterCount() == 1) {
                    method.invoke(user, role != null ? role.toUpperCase() : "TENANT");
                }
                if (name != null) {
                    if (method.getName().equals("setName") && method.getParameterCount() == 1) {
                        method.invoke(user, name);
                    }
                    if (method.getName().equals("setFullName") && method.getParameterCount() == 1) {
                        method.invoke(user, name);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Reflection failed: " + e.getMessage());
        }
    }
}