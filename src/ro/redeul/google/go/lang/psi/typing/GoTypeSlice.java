package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeSlice;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;
import ro.redeul.google.go.util.GoTypeInspectUtil;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoTypeSlice extends GoAbstractType<GoUnderlyingTypeSlice> implements GoType {

    @NotNull
    private final GoType elementType;


    public GoTypeSlice(@NotNull GoType elementType) {
        this.elementType = elementType;
        setUnderlyingType(GoUnderlyingTypes.getSlice(elementType.getUnderlyingType()));
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        if (!(type instanceof GoTypeSlice))
            return false;

        GoTypeSlice otherSlice = (GoTypeSlice) type;
        return elementType.isIdentical(otherSlice.getElementType());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeSlice(this);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        return String.format("[]%s", elementType.getNameLocalOrGlobal(currentFile));
    }

    @NotNull
    public GoType getElementType() {
        return elementType;
    }
}