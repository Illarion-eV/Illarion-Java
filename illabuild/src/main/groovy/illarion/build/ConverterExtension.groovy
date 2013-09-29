package illarion.build

import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.ExtensionContainer

/**
 * This is the extension class that allows to set some parameters for the resource converter.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
class ConverterExtension {
    def String atlasNameExtension
    def File privateKey
}
