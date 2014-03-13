package edu.tuberlin.dima.taverna_to_pig.exception;

/**
 * 
 * @author Umar Maqsud
 *
 */
public class UnsupportedWorkflowException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an <code>UnsupportedWorkflowException</code>.
	 */
	public UnsupportedWorkflowException() {
		super();
	}
	
	/**
	 * Constructs an <code>UnsupportedWorkflowException</code> with the specified detail message.
	 * 
	 * @param message the detail message
	 */
	public UnsupportedWorkflowException(String message) {
		super(message);
	}
}
