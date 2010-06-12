package org.kaleidofoundry.core.i18n.entity;

/**
 * Constants for {@link I18nMessage}
 * 
 * @author Jerome RADUGET
 */
public interface I18nMessageConstants {

   /** {@link I18nMessage} entity table name */
   // ML for multiLanguism, I would prefer to not specify table name, but i have an eclipse link issue (ddl generation for constraints,)
   public static final String Table_I18nMessage = "MLI18N_ENTRY";
   /** {@link I18nMessageLanguage} entity table name */
   public static final String Table_I18nMessageLanguage = "MLI18N_LANGUAGE";
   /** {@link I18nMessageGroup} entity table name */
   public static final String Table_I18nMessageGroupe = "MLI18N_GROUP";

   /**
    * Query static final informations
    */
   public static interface Query_MessagesByLocale {
	public static final String Name = "findMessagesByLanguageIso";
	public static final String Parameter_ResourceName = "resourceName";
	public static final String Parameter_Locale = "localeId";
	public static final String Jql = "select ml from I18nMessageLanguage ml, I18nMessage m where ml.message.id=m.id and m.group.code = :"
		+ Parameter_ResourceName + " and ml.localeId = :" + Parameter_Locale;
   }

   public static final I18nMessageGroup DefaultMessageGroup = new I18nMessageGroup("DEFAULT");

}
