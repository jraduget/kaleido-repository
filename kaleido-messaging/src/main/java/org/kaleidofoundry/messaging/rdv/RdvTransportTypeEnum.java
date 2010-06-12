package org.kaleidofoundry.messaging.rdv;

/**
 * Enumération des type de transport RDV possible
 * <ul>
 * <li>RELIABLE</li>
 * <li>CERTIFIED</li>
 * <li>DQUEUE</li>
 * </ul>
 * 
 * @author Jerome RADUGET
 */
public enum RdvTransportTypeEnum {

   /** Basic Transport */
   RELIABLE("reliable"),
   /** Transport with message confirmation */
   CERTIFIED("certified"),
   /** Transport with distributed Queue */
   DQUEUE("dqueue");

   RdvTransportTypeEnum(final String code) {
	this.code = code;
   }

   private final String code;

   public String getCode() {
	return code;
   }

   public boolean equals(final String code) {
	return this.getCode().equalsIgnoreCase(code);
   }
}