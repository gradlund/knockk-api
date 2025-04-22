package com.knockk.api.util.model;

import java.util.UUID;

import com.knockk.api.util.Gender;

/**
 * Model that has all details of a resident
 */
public class ResidentModel extends OptionalResidentModel {

	private UUID id;
	private String firstName;
	private String lastName;
	private Gender gender;

	public UUID getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Gender getGender() {
		return gender;
	}

	// Constructor for the response
	public ResidentModel(int age, String hometown, String biography, String profilePhoto, String backgroundPhoto,
			String instagram,
			String snapchat, String x, String facebook, UUID id, String firstName, String lastName, Gender gender) {
		// TODO Auto-generated constructor stub
		super(age, hometown, biography, profilePhoto, backgroundPhoto, instagram, snapchat, x, facebook);
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
	}

}
