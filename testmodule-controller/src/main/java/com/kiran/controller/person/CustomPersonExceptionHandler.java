package com.kiran.controller.person;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.kiran.service.person.CustomPersonException;

@ControllerAdvice
public class CustomPersonExceptionHandler {

	@ExceptionHandler(CustomPersonException.NotFound.class)

	ResponseEntity personNotFound(CustomPersonException.NotFound ex) {
		return ResponseEntity.notFound().build();
	}
}
