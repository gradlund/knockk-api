package com.knockk.api.model;

import java.util.Optional;
import java.util.UUID;

import com.knockk.api.data.Gender;

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

	// Constructor for the request because some fields may not be present
	// public ResidentModel(Optional<Integer> age, Optional<String> hometown,
	// Optional<String> profilePhoto, Optional<String> backgroundPhoto,
	// Optional<String> instagram, Optional<String> snapchat, Optional<String> x,
	// Optional<String> facebook,
	// UUID id, String firstName, String lastName, Gender gender) {
	// super(age, hometown, profilePhoto, backgroundPhoto, instagram, snapchat, x,
	// facebook);
	// this.id = id;
	// this.firstName = firstName;
	// this.lastName = lastName;
	// this.gender = gender; //may cause an issue

	// }

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

	// public ResidentModel(UUID id, String firstName, String lastName, String
	// gender, int age, String hometown, String profilePhoto, String
	// backgroundPhoto, String instagram,
	// String snapchat, String x, String facebook) {
	// //super(age, hometown, profilePhoto, backgroundPhoto, instagram, snapchat, x,
	// facebook);
	// //super(getAge(), getHometown(), getBackgroundPhoto(), getInstagram(),
	// getSnapchat(), getX(), getFacebook());
	// this.id = id;
	// this.firstName = firstName;
	// this.lastName = lastName;
	// this.gender = gender;
	// }

}
