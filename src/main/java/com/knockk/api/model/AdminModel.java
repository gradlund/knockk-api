/**
 * Contains model classes
 */
package com.knockk.api.model;

import jakarta.validation.constraints.NotNull;

/**
 * Model used in the request body when an admin logs in
 */
public class AdminModel {

	@NotNull
	private String username;
	@NotNull
	private String password;
	
	public AdminModel(String email, String password) {
		this.username = email;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}
