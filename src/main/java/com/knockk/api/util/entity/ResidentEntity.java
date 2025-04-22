package com.knockk.api.util.entity;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.knockk.api.util.Gender;

/**
 * Entity class for the Resident table
 */
@Table("Resident")
public class ResidentEntity {

	@Id
	@Column("resident_id")
	private UUID id;

	@Column("first_name")
	private String firstName;

	@Column("last_name")
	private String lastName;

	@Column("gender")
	private Gender gender;

	@Column("age")
	private int age;

	@Column("hometown")
	private String hometown;

	@Column("biography")
	private String biography;

	@Column("profile_photo")
	private String profilePhoto;

	@Column("background_photo")
	private String backgroundPhoto;

	@Column("instagram")
	private String instagram;

	@Column("snapchat")
	private String snapchat;

	@Column("x")
	private String x;

	@Column("facebook")
	private String facebook;

	@Column("fk_lease_id")
	private UUID leaseId;

	@Column("verified")
	private boolean verified;

	public ResidentEntity(UUID id, String firstName, String lastName,
			Gender gender,
			int age, String hometown,
			String biography, String profilePhoto, String backgroundPhoto, String instagram, String snapchat, String x,
			String facebook, UUID leaseId, boolean verified) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.age = age;
		this.hometown = hometown;
		this.biography = biography;
		this.profilePhoto = profilePhoto;
		this.backgroundPhoto = backgroundPhoto;
		this.instagram = instagram;
		this.snapchat = snapchat;
		this.x = x;
		this.facebook = facebook;
		this.leaseId = leaseId;
		this.verified = verified;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
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

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
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

	public UUID getLeaseId() {
		return leaseId;
	}

	public void setLeaseId(UUID leaseId) {
		this.leaseId = leaseId;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

}
