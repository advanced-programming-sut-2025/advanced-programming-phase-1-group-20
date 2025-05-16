import org.example.controllers.LoginRegisterMenuController;
import org.example.models.App;
import org.example.models.common.Result;
import org.example.models.entities.User;
import org.example.views.AppView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class LoginRegisterTest {
    private LoginRegisterMenuController controller;
    private AppView mockAppView;

    @Before
    public void setUp() {
        App.initialize();

        mockAppView = Mockito.mock(AppView.class);

        controller = new LoginRegisterMenuController(mockAppView, null);

        User existingUser = App.getUser("testuser");
        if (existingUser != null) {
            App.removeUser(existingUser);
        }
    }

    @After
    public void tearDown() {
        // Clean up test users
        User testUser = App.getUser("testuser");
        if (testUser != null) {
            App.removeUser(testUser);
        }
    }

    // ==================== REGISTRATION TESTS ====================

    @Test
    public void testRegisterUser_ValidInput() {
        // Test with valid registration data
        String[] args = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        Result result = controller.registerUser(args);

        assertTrue("Registration with valid data should succeed", result.success());
        assertEquals("user registered successfully", result.message());

        User user = App.getUser("testuser");
        assertNotNull("User should be created in App", user);
        assertEquals("test@email.com", user.getEmail());
        assertEquals("TestUser", user.getNickname());
    }

    @Test
    public void testRegisterUser_UsernameAlreadyTaken() {
        // First register a user
        String[] validArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(validArgs);

        // Try to register with the same username
        String[] duplicateArgs = {"testuser", "DifferentPass1!", "DifferentPass1!", "OtherUser", "other@email.com", "female"};
        Result result = controller.registerUser(duplicateArgs);

        assertFalse("Registration with duplicate username should fail", result.success());
        assertEquals("username already taken", result.message());
    }

    @Test
    public void testRegisterUser_InvalidUsername() {
        // Test with invalid username (must start with a letter and contain only letters, numbers, and underscore)
        String[] args = {"1user", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        Result result = controller.registerUser(args);

        assertFalse("Registration with invalid username should fail", result.success());
        assertEquals("invalid username format", result.message());

        // Test with valid username that follows the pattern
        String[] validArgs = {"valid_user1", "Password123!", "Password123!", "TestUser", "testi@email.com", "male"};
        Result validResult = controller.registerUser(validArgs);

        assertTrue("Registration with valid username format should succeed", validResult.success());

        // Clean up
        User validUser = App.getUser("valid_user1");
        if (validUser != null) {
            App.removeUser(validUser);
        }
    }

    @Test
    public void testRegisterUser_PasswordMismatch() {
        // Test with mismatched password and confirmation
        String[] args = {"testuser", "Password123!", "DifferentPass123!", "TestUser", "test@email.com", "male"};
        Result result = controller.registerUser(args);

        assertFalse("Registration with mismatched passwords should fail", result.success());
        assertEquals("password confirm does not match the password", result.message());
    }

    @Test
    public void testRegisterUser_WeakPassword() {
        // Test with password that's too short
        String[] shortPasswordArgs = {"testuser", "Pass1!", "Pass1!", "TestUser", "test@email.com", "male"};
        Result shortResult = controller.registerUser(shortPasswordArgs);

        assertFalse("Registration with short password should fail", shortResult.success());
        assertTrue("Error message should indicate password is too short",
                shortResult.message().contains("password too short"));

        // Test with password missing uppercase
        String[] noUpperArgs = {"testuser", "password123!", "password123!", "TestUser", "test@email.com", "male"};
        Result noUpperResult = controller.registerUser(noUpperArgs);

        assertFalse("Registration with no uppercase should fail", noUpperResult.success());
        assertTrue("Error message should indicate missing uppercase",
                noUpperResult.message().contains("upper case"));

        // Test with password missing lowercase
        String[] noLowerArgs = {"testuser", "PASSWORD123!", "PASSWORD123!", "TestUser", "test@email.com", "male"};
        Result noLowerResult = controller.registerUser(noLowerArgs);

        assertFalse("Registration with no lowercase should fail", noLowerResult.success());
        assertTrue("Error message should indicate missing lowercase",
                noLowerResult.message().contains("lower case"));

        // Test with password missing digit
        String[] noDigitArgs = {"testuser", "PasswordABC!", "PasswordABC!", "TestUser", "test@email.com", "male"};
        Result noDigitResult = controller.registerUser(noDigitArgs);

        assertFalse("Registration with no digit should fail", noDigitResult.success());

        // Test with password missing special character
        String[] noSpecialArgs = {"testuser", "Password123", "Password123", "TestUser", "test@email.com", "male"};
        Result noSpecialResult = controller.registerUser(noSpecialArgs);

        assertFalse("Registration with no special character should fail", noSpecialResult.success());
        assertTrue("Error message should indicate missing special character",
                noSpecialResult.message().contains("special character"));
    }

    @Test
    public void testRegisterUser_InvalidEmail() {
        // Test with email missing @ symbol
        String[] missingAtArgs = {"testuser", "Password123!", "Password123!", "TestUser", "testemail.com", "male"};
        Result missingAtResult = controller.registerUser(missingAtArgs);

        assertFalse("Registration with invalid email should fail", missingAtResult.success());
        assertEquals("invalid email format", missingAtResult.message());

        // Test with email containing consecutive dots
        String[] consecutiveDotsArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test..email@domain.com", "male"};
        Result consecutiveDotsResult = controller.registerUser(consecutiveDotsArgs);

        assertFalse("Registration with consecutive dots in email should fail", consecutiveDotsResult.success());
        assertEquals("invalid email format", consecutiveDotsResult.message());

        // Test with email containing multiple @ symbols
        String[] multipleAtArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email@domain.com", "male"};
        Result multipleAtResult = controller.registerUser(multipleAtArgs);

        assertFalse("Registration with multiple @ in email should fail", multipleAtResult.success());
        assertEquals("invalid email format", multipleAtResult.message());
    }

    @Test
    public void testRegisterUser_EmailAlreadyUsed() {
        // First register a user
        String[] firstArgs = {"user1", "Password123!", "Password123!", "TestUser", "same@email.com", "male"};
        controller.registerUser(firstArgs);

        // Try to register with the same email
        String[] secondArgs = {"user2", "Password123!", "Password123!", "OtherUser", "same@email.com", "female"};
        Result result = controller.registerUser(secondArgs);

        assertFalse("Registration with duplicate email should fail", result.success());
        assertEquals("email already used", result.message());

        // Clean up
        User user1 = App.getUser("user1");
        if (user1 != null) {
            App.removeUser(user1);
        }
    }

    // ==================== LOGIN TESTS ====================

    @Test
    public void testLogin_ValidCredentials() {
        // First register a user
        String[] registerArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(registerArgs);

        // Try to login with correct credentials
        String[] loginArgs = {"testuser", "Password123!", ""};
        Result result = controller.login(loginArgs);

        assertTrue("Login with valid credentials should succeed", result.success());
        assertEquals("logged in successfully", result.message());

        // Verify user is logged in
        assertEquals("testuser", App.getLoggedInUser().getUsername());
    }

    @Test
    public void testLogin_UserNotFound() {
        // Try to login with non-existent user
        String[] loginArgs = {"nonexistentuser", "Password123!", ""};
        Result result = controller.login(loginArgs);

        assertFalse("Login with non-existent user should fail", result.success());
        assertEquals("user not found", result.message());
    }

    @Test
    public void testLogin_WrongPassword() {
        // First register a user
        String[] registerArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(registerArgs);

        // Try to login with wrong password
        String[] loginArgs = {"testuser", "WrongPassword123!", ""};
        Result result = controller.login(loginArgs);

        assertFalse("Login with wrong password should fail", result.success());
        assertEquals("wrong password", result.message());
    }

    @Test
    public void testLogin_StayLoggedIn() {
        // First register a user
        String[] registerArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(registerArgs);

        // Login with stay-logged-in option
        String[] loginArgs = {"testuser", "Password123!", "-stay-logged-in"};
        Result result = controller.login(loginArgs);

        assertTrue("Login with stay-logged-in should succeed", result.success());

        // Verify user is marked as staying logged in
        assertTrue("User should be marked as staying logged in",
                App.getLoggedInUser().isStayLoggedIn());
    }

    // ==================== PASSWORD RECOVERY TESTS ====================

    @Test
    public void testForgotPassword_UserNotFound() {
        // Try forgot password for non-existent user
        String[] forgotArgs = {"nonexistentuser"};
        Result result = controller.forgotPassword(forgotArgs);

        assertFalse("Forgot password for non-existent user should fail", result.success());
        assertEquals("user not found", result.message());
    }

    @Test
    public void testAnswerSecurityQuestion_CorrectAnswer() {
        // First register a user
        String[] registerArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(registerArgs);

        // Set security question
        User user = App.getUser("testuser");
        user.setSecurityQuestionIndex(0);
        user.setSecurityAnswer("correct_answer");

        // Answer security question correctly
        String[] answerArgs = {"correct_answer"};
        Result result = controller.answerSecurityQuestion(answerArgs, "testuser");

        assertTrue("Answering security question correctly should succeed", result.success());
        assertTrue("Response should contain new password",
                result.message().startsWith("your new password is"));
    }

    @Test
    public void testAnswerSecurityQuestion_WrongAnswer() {
        // First register a user
        String[] registerArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(registerArgs);

        // Set security question
        User user = App.getUser("testuser");
        user.setSecurityQuestionIndex(0);
        user.setSecurityAnswer("correct_answer");

        // Answer security question incorrectly
        String[] answerArgs = {"wrong_answer"};
        Result result = controller.answerSecurityQuestion(answerArgs, "testuser");

        assertFalse("Answering security question incorrectly should fail", result.success());
        assertEquals("the answer is not correct", result.message());
    }

    @Test
    public void testPickSecurityQuestion_AnswerMismatch() {
        // First register a user
        String[] registerArgs = {"testuser", "Password123!", "Password123!", "TestUser", "test@email.com", "male"};
        controller.registerUser(registerArgs);

        User user = App.getUser("testuser");

        // Pick security question with mismatched answers
        String[] pickArgs = {"1", "my answer", "different answer"};
        Result result = controller.pickSecurityQuestion(pickArgs, user);

        assertFalse("Picking security question with mismatched answers should fail", result.success());
        assertEquals("the answer confirmation is not correct", result.message());
    }
}