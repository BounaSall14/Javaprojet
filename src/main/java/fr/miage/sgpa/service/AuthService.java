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
            boolean passwordMatches = checkPassword(password, user.getPasswordHash());
            if (passwordMatches) {
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

    /**
     * Vérifie un mot de passe en clair contre un hash stocké en base.
     * Supporte les deux formats : hash BCrypt (commence par $2a$) et texte clair (legacy).
     */
    private boolean checkPassword(String plainPassword, String storedHash) {
        if (plainPassword == null || storedHash == null) return false;
        // Si le hash est un hash BCrypt valide, on utilise BCrypt.checkpw
        if (storedHash.startsWith("$2a$") || storedHash.startsWith("$2b$") || storedHash.startsWith("$2y$")) {
            try {
                return BCrypt.checkpw(plainPassword, storedHash);
            } catch (Exception e) {
                logger.error("Erreur lors de la vérification BCrypt : {}", e.getMessage());
                return false;
            }
        }
        // Sinon comparaison en texte clair (legacy / mots de passe non hashés)
        return plainPassword.equals(storedHash);
    }

    /**
     * Hash un mot de passe en clair avec BCrypt.
     * À utiliser lors de la création ou modification d'un utilisateur.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
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
