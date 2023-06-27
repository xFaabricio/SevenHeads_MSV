package br.com.sevenheads.userService.exception;

public class BusinessException extends RuntimeException {
	
	private static final long serialVersionUID = -4740736650902677864L;

	public BusinessException (String message) {
		super(message);
	}
	
}
