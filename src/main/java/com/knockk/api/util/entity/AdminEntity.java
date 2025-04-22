/**
 * Contains entity classes
 */
package com.knockk.api.util.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Entity class for the admin table
 */
@Table("Admin")
public class AdminEntity {
	// Make sure the column names match
	@Id
	@Column("admin_id")
	private UUID adminId;

	@Column("username")
	private String username;

	@Column("password")
	private String password;

	public AdminEntity(UUID adminId, String username, String password) {
		this.adminId = adminId;
		this.username = username;
		this.password = password;
	}

	public UUID getAdminId() {
		return adminId;
	}

	public void setAdminId(UUID adminId) {
		this.adminId = adminId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
