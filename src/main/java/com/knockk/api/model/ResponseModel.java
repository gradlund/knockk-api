package com.knockk.api.model;

/**
 * Model used in the response entity
 * 
 * @param <T> generic used for the value of the HashMap
 */
public class ResponseModel<T> {

	private T data;
	private String message;
	private int status;

	public ResponseModel(T data, String message, int status) {
		this.data = data;
		this.message = message;
		this.status = status;
	}

	// Constructor used for error handling - doesn't include status
	public ResponseModel(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
