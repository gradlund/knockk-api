package com.knockk.api.model;

import java.util.UUID;

/**
 * Model that has all details of a resident
 */
public class ResidentModel extends OptionalResidentModel {
	
	private UUID id;
	private String firstName;
	private String lastName;
	private String gender;

	public ResidentModel(UUID id, String firstName, String lastName, String gender, int age, String hometown, String profilePhoto, String backgroundPhoto, String instagram,
			String snapchat, String x, String facebook) {
		super(age, hometown, profilePhoto, backgroundPhoto, instagram, snapchat, x, facebook);
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
	}
	
}
