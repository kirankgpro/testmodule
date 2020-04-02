package com.kiran.service.person;

public class CustomPersonException {

	public static class NotFound extends RuntimeException {

		NotFound(String msg) {
			super(msg);
		}
	}
}
