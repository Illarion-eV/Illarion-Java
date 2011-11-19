package illarion.client.loading;

import org.newdawn.slick.loading.LoadingList;

import illarion.common.graphics.TextureLoader;

/**
 * This class is used to create the list of things that need to be loaded before
 * the game is able to start.
 * 
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class Loading {
    /**
     * This variable is set to true in case the elements got enlisted already
     * and this class must not do anything anymore.
     */
    private static boolean loadingDone = false;
    
    public static void enlistMissingComponents() {
        if (!loadingDone) {
            while (!TextureLoader.getInstance().preloadAtlasTextures()) {};
            LoadingList.get().add(new ResourceTableLoading());
            loadingDone = true;
        }
        
        LoadingList.get().add(new GameEnvironmentLoading());
        LoadingList.get().add(new FinishLoading());
    }
}
