package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.UserDAO;
import fr.miage.sgpa.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userDAO);
    }

    @Test
    void testLoginSuccess() throws Exception {
        String username = "testuser";
        String password = "password123";

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(password);
        user.setRole(User.Role.VENDEUR);

        when(userDAO.findByUsername(username)).thenReturn(Optional.of(user));

        boolean result = authService.login(username, password);

        assertTrue(result);
        assertEquals(user, AuthService.getCurrentUser());
    }

    @Test
    void testLoginFailure() throws Exception {
        String username = "testuser";
        when(userDAO.findByUsername(username)).thenReturn(Optional.empty());

        boolean result = authService.login(username, "wrong");

        assertFalse(result);
    }
}
