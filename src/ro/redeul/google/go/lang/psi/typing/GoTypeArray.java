package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.inspection.InspectionUtil;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;

public class GoTypeArray extends GoTypeBase<GoPsiTypeArray> {

    @NotNull
    private final GoType elementType;

    private final int length;

    public GoTypeArray(@Nullable GoPsiTypeArray psiType, @NotNull GoType elementType, int length) {
        super(psiType);
        this.elementType = elementType;
        this.length = length;
    }

    @Override
    public boolean isIdentical(GoType other) {
        if (this == other) return true;
        if (other instanceof GoTypeArray) {
            GoTypeArray otherArray = (GoTypeArray) other;
            return !(this.length == InspectionUtil.UNKNOWN_COUNT || otherArray.length == InspectionUtil.UNKNOWN_COUNT || length == otherArray.length) && otherArray.elementType.isIdentical(elementType);
        }
        return false;
    }

    @NotNull
    public GoType getElementType() {
        return elementType;
    }
}
