package com.knockk.api.util.model;

import java.util.UUID;

/**
 * Model used in the response when a user logs in
 */
public class LoginModel {

	private UUID id;
	private boolean isVerified;

	public LoginModel(UUID id, boolean isVerified) {
		this.id = id;
		this.isVerified = isVerified;
	}

	// Getters for testing
	public UUID getId() {
		return id;
	}

	public boolean isVerified() {
		return isVerified;
	}

}
