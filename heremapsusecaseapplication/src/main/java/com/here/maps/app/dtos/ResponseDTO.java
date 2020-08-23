package com.here.maps.app.dtos;

import org.springframework.http.HttpStatus;

/**
 * @author Vidyashri
 *
 */
public class ResponseDTO {
	private int statusCode;
	private String message;

	/**
	 * Default constructor.
	 *
	 */
	public ResponseDTO() {
		this.setStatusCode(HttpStatus.ACCEPTED.value());
		this.setMessage("Successfully accepted the request");
	}

	/**
	 * Parameterized constructor.
	 *
	 */
	public ResponseDTO(int statusCode, String message) {
		this.setStatusCode(statusCode);
		this.setMessage(message);
	}

	/**
	 * Returns the status code of the request.
	 * 
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * Sets the status code of the request.
	 * 
	 * @param badRequest the statusCode to set
	 */
	public void setStatusCode(int badRequest) {
		this.statusCode = badRequest;
	}

	/**
	 * Returns the message of the request.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the message of the request.
	 * 
	 * @param message the message to set
	 */
	/**
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "{" + "\"statusCode\":" + statusCode + "," + "\"message\":\"" + message + "\"" + "}";
	}
}
