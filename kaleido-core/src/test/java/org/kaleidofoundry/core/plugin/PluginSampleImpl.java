/*
 * $License$
 */
package org.kaleidofoundry.core.plugin;

import org.kaleidofoundry.core.plugin.annotation.DeclarePlugin;

/**
 * @author Jerome RADUGET
 */
@DeclarePlugin(value = "newPlugin.impl1", description = "description plugin impl1", version = "1.0.0", enable = true)
class PluginSampleImpl implements PluginSample {
}
