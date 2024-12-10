package com.knockk.api.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class for the user table
 */
@Table("User")
public class UserEntity {
	//Make sure the column names match
	@Id
	@Column("user_id")
	private UUID residentId;
	
	@Column("email")
	private String email;
	
	@Column("password")
	private String password;

	public UserEntity(UUID residentId, String email, String password) {
		this.residentId = residentId;
		this.email = email;
		this.password = password;
	}

	public UUID getResidentId() {
		return residentId;
	}

	public void setResidentId(UUID residentId) {
		this.residentId = residentId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
