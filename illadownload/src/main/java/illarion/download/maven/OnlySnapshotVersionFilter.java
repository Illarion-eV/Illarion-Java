package illarion.download.maven;

import java.util.Iterator;

import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.version.Version;

/**
 * A version filter that (unconditionally) blocks non "*-SNAPSHOT" versions.
 */
public final class OnlySnapshotVersionFilter implements VersionFilter {
    public void filterVersions( VersionFilterContext context ) {
        for ( Iterator<Version> it = context.iterator(); it.hasNext(); )
        {
            String version = it.next().toString();
            if ( !version.endsWith( "SNAPSHOT" ) )
            {
                it.remove();
            }
        }
    }

    public VersionFilter deriveChildFilter( DependencyCollectionContext context ) {
        return this;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
        {
            return true;
        }

        return null != obj && getClass().equals(obj.getClass());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
