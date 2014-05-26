package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;

/**
 * // TODO: mtoader ! Implement this.
 */
public class GoTypePointer extends GoAbstractType implements GoType {

    private final GoType targetType;

    public GoTypePointer(GoType targetType) {
        this.targetType = targetType;
    }

    public GoTypePointer(GoPsiTypePointer type) {
        this(GoTypes.fromPsiType(type.getTargetType()));
    }

    public GoType getTargetType() {
        return targetType;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypePointer(this);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        return "*" + targetType.getNameLocalOrGlobal(currentFile);
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return type == this || type instanceof GoTypePointer && this.targetType.isIdentical(((GoTypePointer) type).targetType);
    }
}
