package com.knockk.api.model;

/**
 * Model that contains all the optional fields of a resident
 */
public class OptionalResidentModel {

	private int age;
	private String hometown;
	private String profilePhoto;
	private String backgroundPhoto;
	private String instagram;
	private String snapchat;
	private String x;
	private String facebook;
	
	public OptionalResidentModel(int age, String hometown, String profilePhoto, String backgroundPhoto,
			String instagram, String snapchat, String x, String facebook) {
		super();
		this.age = age;
		this.hometown = hometown;
		this.profilePhoto = profilePhoto;
		this.backgroundPhoto = backgroundPhoto;
		this.instagram = instagram;
		this.snapchat = snapchat;
		this.x = x;
		this.facebook = facebook;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public String getProfilePhoto() {
		return profilePhoto;
	}

	public void setProfilePhoto(String profilePhoto) {
		this.profilePhoto = profilePhoto;
	}

	public String getBackgroundPhoto() {
		return backgroundPhoto;
	}

	public void setBackgroundPhoto(String backgroundPhoto) {
		this.backgroundPhoto = backgroundPhoto;
	}

	public String getInstagram() {
		return instagram;
	}

	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}

	public String getSnapchat() {
		return snapchat;
	}

	public void setSnapchat(String snapchat) {
		this.snapchat = snapchat;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
}
