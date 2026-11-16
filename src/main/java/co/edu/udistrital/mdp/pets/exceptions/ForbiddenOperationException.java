package co.edu.udistrital.mdp.pets.exceptions;

/*
 * Excepción que se lanza cuando el usuario no tiene permiso sobre el recurso consultado
 * (por ejemplo, una adopción de otro adoptante o una mascota que no está a cargo del veterinario).
 */
public class ForbiddenOperationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ForbiddenOperationException(String message) {
		super(message);
	}
}
