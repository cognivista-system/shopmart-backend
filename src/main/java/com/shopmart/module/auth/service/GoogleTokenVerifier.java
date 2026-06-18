package com.shopmart.module.auth.service;

/**
 * Verifies a Google ID token and returns the authenticated user's profile.
 * The default implementation validates the token signature and audience locally
 * via Google's published keys; an alternative implementation can be swapped in.
 */
public interface GoogleTokenVerifier {
    GoogleUserInfo verify(String idToken);
}
