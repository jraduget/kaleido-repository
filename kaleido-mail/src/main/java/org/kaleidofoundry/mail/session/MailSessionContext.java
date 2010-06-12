package org.kaleidofoundry.mail.session;

import static org.kaleidofoundry.mail.session.MailSessionConstants.LocalRootProperty;
import static org.kaleidofoundry.mail.session.MailSessionConstants.TypeProperty;

import java.util.Properties;

import org.kaleidofoundry.core.config.Configuration;
import org.kaleidofoundry.core.context.RuntimeContext;

/**
 * Ancêtre MailSessionContext utilisé pour charger une Session Mail
 * 
 * @author Jerome RADUGET
 */
public class MailSessionContext extends RuntimeContext {

   private static final long serialVersionUID = -8772474592608596423L;

   /**
	 * 
	 */
   public MailSessionContext() {
	super((String) null, LocalRootProperty);
   }

   /**
    * @param config
    */
   public MailSessionContext(final Configuration config) {
	super(null, config, LocalRootProperty);
   }

   /**
    * @param props
    */
   public MailSessionContext(final Properties props) {
	super(null, props, LocalRootProperty);
   }

   /**
    * @param name
    * @param defaults
    * @param prefixProperty
    */
   public MailSessionContext(final String name, final Configuration defaults, final String prefixProperty) {
	super(name, defaults, prefixProperty);
   }

   /**
    * @param name
    * @param defaults
    */
   public MailSessionContext(final String name, final Configuration defaults) {
	super(name, defaults, LocalRootProperty);
   }

   /**
    * @param name
    * @param defaults
    * @param prefixProperty
    */
   public MailSessionContext(final String name, final Properties defaults, final String prefixProperty) {
	super(name, defaults, prefixProperty);
   }

   /**
    * @param name
    * @param defaults
    */
   public MailSessionContext(final String name, final Properties defaults) {
	super(name, defaults, LocalRootProperty);
   }

   /**
    * @param name
    */
   public MailSessionContext(final String name) {
	super(name, LocalRootProperty);
   }

   /**
    * @return Type d'implementation (clé)
    */
   public String getType() {
	return getProperty(TypeProperty);
   }

}
