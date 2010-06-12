package org.kaleidofoundry.core.store;

/**
 * Represents handled uri schemes (default and custom implementations )
 * 
 * @author Jerome RADUGET
 */
public class ResourceStoreTypeBean implements ResourceStoreType {

   private final String name;

   /**
    * @param name
    */
   public ResourceStoreTypeBean(final String name) {
	this.name = name;
   }

   /**
    * @return scheme
    */
   public String name() {
	return name;
   }

   @Override
   public boolean isCustom() {
	return true;
   }

}
