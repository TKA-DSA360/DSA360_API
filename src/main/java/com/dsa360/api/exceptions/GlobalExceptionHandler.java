package com.dsa360.api.exceptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;

import com.dsa360.api.constants.General;
import com.dsa360.api.dto.ExceptionRespone;

/**
 * @author RAM
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public LinkedHashMap<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());

		map.put("Time", formattedTime);
		map.put("path", request.getRequestURI());
		ex.getFieldErrors().forEach(error -> {
			map.put(error.getField(), error.getDefaultMessage());
		});
		return map;
	}

	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ConstraintViolationException.class)
	public HashMap<String, Object> constraintViolationException(ConstraintViolationException ex) {
		HashMap<String, Object> map = new HashMap<>();

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		map.put("Time", formattedTime);
		Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

		for (ConstraintViolation<?> constraintViolation : constraintViolations) {
			map.put("Message", constraintViolation.getConstraintDescriptor() + " " + constraintViolation.getMessage());

		}

		return map;
	}

	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ExceptionRespone> badCredentialsException(BadCredentialsException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone("Invalid User Password", request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.UNAUTHORIZED);

	}

	@ExceptionHandler(InvalidCredentialsException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ExceptionRespone> invalidCredientials(InvalidCredentialsException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ExceptionRespone> invalidUsername(InternalAuthenticationServiceException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone("Invalid Username", request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	@ResponseStatus(code = HttpStatus.CONFLICT)
	public ResponseEntity<ExceptionRespone> resourceAlreadyExists(ResourceAlreadyExistsException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public ResponseEntity<ExceptionRespone> resourceNotFoundException(ResourceNotFoundException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(SomethingWentWrongException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ExceptionRespone> somethingWentWrongException(SomethingWentWrongException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(UserDeactivatedException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public ResponseEntity<ExceptionRespone> userDeactivatedException(UserDeactivatedException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(UserSuspendedException.class)
	@ResponseStatus(code = HttpStatus.LOCKED)
	public ResponseEntity<ExceptionRespone> userSuspendedException(UserSuspendedException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.LOCKED);
	}

	@ExceptionHandler(TokenExpirationException.class)
	@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ExceptionRespone> tokenExpirationException(TokenExpirationException ex,
			HttpServletRequest request) {
		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(InternalServerError.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ExceptionRespone> internalServerError(InternalServerError ex, HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(IllegalStateException.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ExceptionRespone> illegalStateException(IllegalStateException ex, HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone("Something went wrong ", request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public ResponseEntity<ExceptionRespone> accessDeniedException(AccessDeniedException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone(ex.getMessage(), request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	@ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
	public ResponseEntity<ExceptionRespone> unImplemented(UnsupportedOperationException ex,
			HttpServletRequest request) {

		var sdf = new SimpleDateFormat(General.DATE_FORMAT.getValue());
		String formattedTime = sdf.format(new Date());
		var exceptionRespone = new ExceptionRespone("Method not implemented", request.getRequestURI(), formattedTime);
		return new ResponseEntity<>(exceptionRespone, HttpStatus.NOT_IMPLEMENTED);

	}

}
