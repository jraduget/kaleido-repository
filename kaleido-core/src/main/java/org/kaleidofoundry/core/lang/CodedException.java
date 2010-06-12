package org.kaleidofoundry.core.lang;

/**
 * This class is used to represent a technical or domain service exception (coded exception). <br/>
 * Code parameter have to be unique, and it represent more precisely the type of exception.
 * 
 * @author Jerome RADUGET
 */
public class CodedException extends Exception {

   private static final long serialVersionUID = 4684568762315563117L;

   private final String code;

   /**
    * @param code error code
    */
   public CodedException(final String code) {
	super(code);
	this.code = code;
   }

   /**
    * @param code error code
    * @param message Détail
    */
   public CodedException(final String code, final String message) {
	super(message != null ? message : code);
	this.code = code;
   }

   /**
    * @param code error code
    * @param message Détail
    * @param cause
    */
   public CodedException(final String code, final String message, final Throwable cause) {
	super(message != null ? message : code, cause);
	this.code = code;
   }

   /**
    * @return Error code of the exception
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
