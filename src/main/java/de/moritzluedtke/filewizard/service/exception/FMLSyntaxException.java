package de.moritzluedtke.filewizard.service.exception;

/**
 * Custom exception in case an FML syntax rule gets broken.
 */
public class FMLSyntaxException extends Exception {
	
	/**
	 * Returns the custom error message.
	 *
	 * @return the custom error message
	 */
	@Override
	public String getMessage() {
		return "Incorrect FML Syntax!";
	}
}
