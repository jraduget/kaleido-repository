package org.kaleidofoundry.core.naming;

/**
 * Jndi ClientException
 * 
 * @author Jerome RADUGET
 */
public class JndiResourceNotFoundException extends JndiResourceException {

   private static final long serialVersionUID = 1189942552569947656L;

   /**
    * @param resourceName
    */
   public JndiResourceNotFoundException(final String resourceName) {
	super("jndi.error.initialContext.lookup.notfound", resourceName);
   }

}
