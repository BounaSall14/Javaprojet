package fr.miage.sgpa.service;

import fr.miage.sgpa.dao.UserDAO;
import fr.miage.sgpa.model.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final UserDAO userDAO;
    private static User currentUser;

    public AuthService() {
        this(new UserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean login(String username, String password) throws Exception {
        logger.info("Tentative de connexion pour : {}", username);
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("Utilisateur trouvé en base. Vérification du mot de passe...");
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                currentUser = user;
                logger.info("Connexion réussie !");
                return true;
            } else {
                logger.warn("Mot de passe incorrect pour l'utilisateur : {}", username);
            }
        } else {
            logger.warn("Aucun utilisateur trouvé avec le nom : {}", username);
        }
        return false;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == User.Role.ADMIN;
    }
}
