package blooddonation.service;

/**
 * Erreur métier ou de validation. Son message est destiné à l'utilisateur
 * et s'affiche dans l'interface (message flash).
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceException(String message) {
        super(message);
    }
}
