package com.knockk.api.util.model;

import jakarta.validation.constraints.NotNull;

/**
 * Model used in the request when a resident logs in
 */
public class UserModel {

	@NotNull
	private String email;
	@NotNull
	private String password;

	public UserModel(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
}
