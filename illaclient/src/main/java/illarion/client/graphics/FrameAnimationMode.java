package illarion.client.world.movement;

import illarion.common.types.ServerCoordinate;

import javax.annotation.Nonnull;

public interface TargetTurnHandler extends MovementHandler {
    /**
     * Turn towards a location.
     *
     * @param target the target location
     */
    void turnTo(@Nonnull ServerCoordinate target);

}
