package blooddonation.service;

import blooddonation.bean.Donneur;
import blooddonation.bean.Hopital;
import blooddonation.bean.User;
import blooddonation.dao.interfaces.UserDao;
import blooddonation.enums.GroupeSang;
import blooddonation.enums.Role;
import blooddonation.security.PasswordHasher;
import blooddonation.utils.Validation;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

/** Inscription et authentification. */
public class AuthService {

    /** Données du formulaire d'inscription (certains champs dépendent du rôle). */
    public record RegistrationRequest(
            String role, String prenom, String nom, String email, String telephone,
            String ville, String adresse, String groupeSanguin, String nomHopital,
            String password, String passwordConfirm) { }

    private final UserDao userDao;
    private final PasswordHasher hasher;
    private final Clock clock;
    /** Hash factice : vérifié quand l'e-mail n'existe pas, pour que la réponse prenne le même temps. */
    private final String dummyHash;

    public AuthService(UserDao userDao, PasswordHasher hasher, Clock clock) {
        this.userDao = userDao;
        this.hasher = hasher;
        this.clock = clock;
        this.dummyHash = hasher.hash("dummy-password-for-timing");
    }

    public User register(RegistrationRequest r) {
        Role role = Validation.enumValue(Role.class, r.role(), "Type de compte");
        String email = Validation.email(r.email());
        Validation.motDePasse(r.password(), r.passwordConfirm());

        if (userDao.findByEmail(email).isPresent()) {
            throw new ServiceException("Un compte existe déjà avec cette adresse e-mail.");
        }

        User user;
        if (role == Role.DONNEUR) {
            Donneur d = new Donneur();
            d.setGroupeSanguin(Validation.enumValue(GroupeSang.class, r.groupeSanguin(), "Groupe sanguin"));
            user = d;
        } else {
            Hopital h = new Hopital();
            h.setNomHopital(Validation.required(r.nomHopital(), "Nom de l'hôpital", 120));
            h.setAdresse(Validation.optional(r.adresse(), "Adresse", 200));
            user = h;
        }
        user.setPrenom(Validation.required(r.prenom(), "Prénom", 60));
        user.setNom(Validation.required(r.nom(), "Nom", 60));
        user.setEmail(email);
        user.setTelephone(Validation.telephone(r.telephone()));
        user.setVille(Validation.required(r.ville(), "Ville", 80));
        user.setPasswordHash(hasher.hash(r.password()));
        user.setDateInscription(LocalDateTime.now(clock));

        userDao.save(user);
        return user;
    }

    /** Retourne l'utilisateur si l'e-mail et le mot de passe sont corrects. */
    public Optional<User> authenticate(String email, String password) {
        String normalized = email == null ? "" : email.trim().toLowerCase();
        Optional<User> user = normalized.isEmpty() ? Optional.empty() : userDao.findByEmail(normalized);
        if (user.isEmpty()) {
            hasher.verify(password == null ? "" : password, dummyHash);
            return Optional.empty();
        }
        return hasher.verify(password, user.get().getPasswordHash()) ? user : Optional.empty();
    }
}
