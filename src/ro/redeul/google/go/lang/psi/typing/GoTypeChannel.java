package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;

public class GoTypeChannel extends GoTypeBase<GoPsiTypeChannel> {

    public static enum Direction {
        Bidirectional,
        Sending,
        Receiving;

        public static String getText(Direction direction) {
            switch (direction) {
                case Bidirectional:
                    return "chan";
                case Sending:
                    return "chan<-";
                case Receiving:
                    return "<-chan";
            }

            return "";
        }
    }

    @NotNull
    private final Direction direction;

    @NotNull
    private final GoType elementType;

    public GoTypeChannel(@Nullable GoPsiTypeChannel psiType, @NotNull Direction direction, @NotNull GoType elementType) {
        super(psiType);
        this.direction = direction;
        this.elementType = elementType;
    }

    @NotNull
    public GoType getElementType() {
        return elementType;
    }

    @Override
    public boolean isIdentical(GoType other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof GoTypeChannel))
            return false;

        GoTypeChannel otherChannel = (GoTypeChannel) other;
        return direction == otherChannel.direction
                && elementType.isIdentical(otherChannel.elementType);
    }

    @NotNull
    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return String.format("%s%s", direction.toString(), elementType);
    }
}
