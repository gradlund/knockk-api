package com.knockk.api.util.model;

import java.util.Optional;

/**
 * Model that contains all the optional fields of a resident.
 * Making fields optional, means that the property doesn't need to be sent in
 * the request. The value of that field will be Optional.empty if not given.
 */
public class OptionalResidentModel {

	private int age;
	private String hometown;
	private String biography;
	private String profilePhoto;
	private String backgroundPhoto;
	private String instagram;
	private String snapchat;
	private String x;
	private String facebook;

	public int getAge() {
		return age;
	}

	public String getHometown() {
		return hometown;
	}

	public String getBiography() {
		return biography;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public String getBackgroundPhoto() {
		return backgroundPhoto;
	}

	public String getInstagram() {
		return instagram;
	}

	public String getSnapchat() {
		return snapchat;
	}

	public String getX() {
		return x;
	}

	public String getFacebook() {
		return facebook;
	}

	public OptionalResidentModel(int age, String hometown, String biography, String profilePhoto,
			String backgroundPhoto,
			String instagram, String snapchat, String x, String facebook) {
		this.age = age;
		this.hometown = hometown;
		this.biography = biography;
		this.profilePhoto = profilePhoto;
		this.backgroundPhoto = backgroundPhoto;
		this.instagram = instagram;
		this.snapchat = snapchat;
		this.x = x;
		this.facebook = facebook;
	}

}
