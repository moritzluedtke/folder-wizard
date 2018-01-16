package de.moritzluedtke.service.exception;

public class FMLSyntaxException extends Exception {
	
	@Override
	public String getMessage() {
		return "Incorrect FML Syntax!";
	}
}
