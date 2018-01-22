package de.moritzluedtke.service.exception;

/**
 * Custom exception in case an FML syntax rule gets broken.
 */
public class FMLSyntaxException extends Exception {
	
	/**
	 * Custom error message.
	 * @return the error message
	 */
	@Override
	public String getMessage() {
		return "Incorrect FML Syntax!";
	}
}
