package seedu.address.account.account;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a list of Accounts.
 * Each AccountList is associated with a map that stores accounts with their usernames as keys.
 */
public class AccountList {
    private Map<Username, Account> accounts;

    /**
     * Constructs an AccountList instance with an empty map of accounts.
     */
    public AccountList() {
        this.accounts = new HashMap<>();
    }

    /**
     * Adds a new account to the list. Returns true if the account was successfully added,
     * or false if an account with the same username already exists.
     */
    public boolean addAccount(Account account) {
        if (accounts.containsKey(account.getUsername())) {
            return false;
        }
        accounts.put(account.getUsername(), account);
        return true;
    }

    /**
     * Attempts to authenticate a user with the provided username and password.
     * Returns the Account object if authentication is successful, or null otherwise.
     */
    public Account authenticate(String username, String passwordHash) {
        Account account = accounts.get(username);
        if (account != null && account.getPasswordHash().equals(passwordHash)) {
            return account;
        }
        return null;
    }

    /**
     * Hashes a password using SHA-256.
     *
     * @param password The password to be hashed.
     * @return The hashed password.
     * @throws RuntimeException if the SHA-256 algorithm is not found.
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param hash The byte array to be converted.
     * @return The hexadecimal string.
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
