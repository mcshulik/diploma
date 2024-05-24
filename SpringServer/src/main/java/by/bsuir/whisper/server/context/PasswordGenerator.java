package by.bsuir.whisper.server.context;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
public interface PasswordGenerator {
   String getSalt();
   String getHash();
}
