package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public class GoTypeSlice extends GoAbstractType implements GoType {

    @NotNull
    private final GoType elementType;


    public GoTypeSlice(@NotNull GoType elementType) {
        this.elementType = elementType;
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