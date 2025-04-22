package com.knockk.api.util.model;

/**
 * Model used in the request - which includes the resident's id
 */
public class RequestWithId {
	private String residentId;

	public void setResidentId(String residentId) {
		this.residentId = residentId;
	}

	public String getResidentId() {
		return residentId;
	}

}
