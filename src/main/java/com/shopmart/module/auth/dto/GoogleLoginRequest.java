package com.shopmart.module.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** The Google ID-token (JWT credential) returned by Google Identity Services on the client. */
public record GoogleLoginRequest(@NotBlank String idToken) {}
