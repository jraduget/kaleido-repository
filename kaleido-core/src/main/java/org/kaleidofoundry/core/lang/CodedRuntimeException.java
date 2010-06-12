package org.kaleidofoundry.core.lang;

/**
 * This class is used to represent a runtime domain service exception (coded exception). <br/>
 * Runtime is normaly not handle, and propagate through the layers.<br/>
 * It has an unique code parameter, represented precisely type of exception.
 * 
 * @author Jerome RADUGET
 */
public class CodedRuntimeException extends RuntimeException {

   private static final long serialVersionUID = 4684568762315563117L;

   private final String code;

   /**
    * @param code error code
    */
   public CodedRuntimeException(final String code) {
	super(code);
	this.code = code;
   }

   /**
    * @param code error code
    * @param message error message
    */
   public CodedRuntimeException(final String code, final String message) {
	super(message != null ? message : code);
	this.code = code;
   }

   /**
    * @param code error code
    * @param message error message
    * @param cause
    */
   public CodedRuntimeException(final String code, final String message, final Throwable cause) {
	super(message != null ? message : code, cause);
	this.code = code;
   }

   /**
    * @return Retourne le code d'erreur de l'exception
    */
   public String getCode() {
	return code;
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Throwable#toString()
    */
   @Override
   public String toString() {
	return super.toString() + " [" + getCode() + "]";
   }

}
