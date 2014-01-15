package illarion.mapedit.data.formats;

import javax.annotation.Nonnull;
import java.nio.file.Path;

/**
 * The decoder factory is able to provide the decoder implementations according to a version value.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DecoderFactory {
    @Nonnull
    public Decoder getDecoder(final int version, @Nonnull final String mapName, @Nonnull final Path mapPath) {
        switch (version) {
            case 2:
                return new Version2Decoder(mapName, mapPath);
            default:
                throw new IllegalArgumentException("Illegal version: " + version);
        }
    }
}
