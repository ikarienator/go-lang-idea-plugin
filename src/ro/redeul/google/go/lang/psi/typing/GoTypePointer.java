package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;

public class GoTypePointer extends GoTypeBase<GoPsiTypePointer> {

    @NotNull
    private final GoType baseType;

    public GoTypePointer(GoPsiTypePointer psiType, @NotNull GoType baseType) {
        super(psiType);
        this.baseType = baseType;
    }

    @NotNull
    public GoType getTargetType() {
        return baseType;
    }

    @Override
    public boolean isIdentical(GoType other) {
        if (other instanceof GoTypePointer) {
            GoTypePointer otherPointer =
                    (GoTypePointer) other;

            return baseType.isIdentical(otherPointer.baseType);
        }

        return false;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoExpr x) {
        return false;
    }
}
