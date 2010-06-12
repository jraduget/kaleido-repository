package org.kaleidofoundry.core.i18n;

import java.util.ResourceBundle;

/**
 * @author Jerome RADUGET
 * @see ResourceBundle.Control
 */
public enum MessageBundleControlFormat {

   /** Standard JDK Properties file loader */
   STANDARD_PROPERTIES("properties"),
   /** Properties xml file loader control */
   XML_PROPERTIES("properties.xml"),
   /** Properties jpa database control -> properties store exploded in table line */
   JPA_ENTITY_PROPERTIES("entity.jpa"),
   /** Properties jpa database control -> properties store in a clob */
   // JPA_CLOB_PROPERTIES("clob.jpa")
   ;

   private String extention;

   MessageBundleControlFormat(final String extention) {
	this.extention = extention;
   }

   /**
    * @return resource properties extentions
    */
   public String getExtention() {
	return extention;
   }

}
