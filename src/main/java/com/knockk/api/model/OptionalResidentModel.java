package com.knockk.api.model;

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

	// private Optional<Integer> age;
	// private Optional<String> hometown;
	// private Optional<String> profilePhoto;
	// private Optional<String> backgroundPhoto;
	// private Optional<String> instagram;
	// private Optional<String> snapchat;
	// private Optional<String> x;
	// private Optional<String> facebook;

	// public Optional<Integer> getAge() {
	// return age;
	// }

	// public Optional<String> getHometown() {
	// return hometown;
	// }

	// public Optional<String> getProfilePhoto() {
	// return profilePhoto;
	// }

	// public Optional<String> getBackgroundPhoto() {
	// return backgroundPhoto;
	// }

	// public Optional<String> getInstagram() {
	// return instagram;
	// }

	// public Optional<String> getSnapchat() {
	// return snapchat;
	// }

	// public Optional<String> getX() {
	// return x;
	// }

	// public Optional<String> getFacebook() {
	// return facebook;
	// }

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

	// public OptionalResidentModel(Optional<Integer> age, Optional<String>
	// hometown, Optional<String> profilePhoto, Optional<String> backgroundPhoto,
	// Optional<String> instagram, Optional<String> snapchat, Optional<String> x,
	// Optional<String> facebook) {
	// this.age = age;
	// this.hometown = hometown;
	// this.profilePhoto = profilePhoto;
	// this.backgroundPhoto = backgroundPhoto;
	// this.instagram = instagram;
	// this.snapchat = snapchat;
	// this.x = x;
	// this.facebook = facebook;
	// }

	// public OptionalResidentModel(int age, Optional<String> hometown,
	// Optional<String> profilePhoto, Optional<String> backgroundPhoto,
	// Optional<String> instagram, Optional<String> snapchat, Optional<String> x,
	// Optional<String> facebook) {
	// this.age = Optional.of(age);
	// this.hometown = hometown;
	// this.profilePhoto = profilePhoto;
	// this.backgroundPhoto = backgroundPhoto;
	// this.instagram = instagram;
	// this.snapchat = snapchat;
	// this.x = x;
	// this.facebook = facebook;
	// }

	// public OptionalResidentModel(int age, String hometown, String profilePhoto,
	// String backgroundPhoto,
	// String instagram, String snapchat, String x, String facebook) {
	// this.age = Optional.of(age);
	// this.hometown = Optional.ofNullable(hometown);
	// this.profilePhoto = Optional.ofNullable(profilePhoto);
	// this.backgroundPhoto = Optional.ofNullable(backgroundPhoto);
	// this.instagram = Optional.ofNullable(instagram);
	// this.snapchat = Optional.ofNullable(snapchat);
	// this.x = Optional.ofNullable(x);
	// this.facebook = Optional.ofNullable(facebook);
	// }

}
