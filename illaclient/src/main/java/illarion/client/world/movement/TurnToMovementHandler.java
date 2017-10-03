package illarion.client.world.movement;

import illarion.client.world.CharMovementMode;
import illarion.common.types.Direction;
import illarion.common.types.ServerCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurnToMovementHandler extends AbstractMovementHandler implements TargetTurnHandler {
    @Nonnull
    private static final Logger log = LoggerFactory.getLogger(TurnToMovementHandler.class);
    @Nonnull
    private static final Marker marker = MarkerFactory.getMarker("Movement");

    @Nullable
    private ServerCoordinate targetLocation;

    TurnToMovementHandler(@Nonnull Movement movement) {
        super(movement);
    }

    @Override
    public void turnTo(@Nonnull ServerCoordinate target) {
        targetLocation = target;
    }

    @Override
    public void disengage(boolean transferAllowed) {
        super.disengage(transferAllowed);
        targetLocation = null;
    }

    @Nullable
    @Override
    public StepData getNextStep(@Nonnull ServerCoordinate currentLocation) {
        if (targetLocation == null) {
            return null;
        }
        log.debug(marker, "Performing turn to {}", targetLocation);
        Direction direction = currentLocation.getDirection(targetLocation);
        targetLocation = null;
        return new DefaultStepData(CharMovementMode.None, direction);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Turn to target movement handler";
    }

}
