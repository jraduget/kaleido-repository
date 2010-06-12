package org.kaleidofoundry.core.plugin;

import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;
import org.kaleidofoundry.core.util.Registry;

/**
 * Registry of all "enable" plugin interface<br/>
 * An enable plugin is an interface annotation by {@link DeclarePlugin} with enable attribute set to true
 * 
 * @author Jerome RADUGET
 */
public class PluginRegistry extends Registry<Plugin<?>> {

   private static final long serialVersionUID = -3817010482113243944L;

   /**
    * Constructor with package access in order to avoid direct instanciation. <br/>
    * Please use {@link PluginFactory} to get current instance.
    */
   PluginRegistry() {
   }

}