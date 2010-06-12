package org.kaleidofoundry.core.store;

/**
 * @author Jerome RADUGET
 */
public interface ResourceStoreType {

   /**
    * @return <code>false</code> if it is a default kaleido foundry implementation otherwise <code>false</code>
    */
   boolean isCustom();
}
