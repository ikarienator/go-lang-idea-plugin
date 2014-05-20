package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;

public class GoTypeSlice extends GoTypeBase<GoPsiTypeSlice> {
    @NotNull
    private final GoType elementType;

    public GoTypeSlice(@Nullable GoPsiTypeSlice psiType, @NotNull GoType elementType) {
        super(psiType);
        this.elementType = elementType;
    }

    @Override
    public boolean isIdentical(GoType other) {
        if (this == other) return true;
        if (other instanceof GoTypeSlice) {
            GoTypeSlice otherSlice =
                    (GoTypeSlice) other;
            return elementType.isIdentical(otherSlice.elementType);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[]%s", elementType.toString());
    }


    @NotNull
    public GoType getElementType() {
        return elementType;
    }
}
