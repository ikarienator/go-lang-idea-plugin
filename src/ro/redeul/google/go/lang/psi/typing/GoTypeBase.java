package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public abstract class GoTypeBase<PsiType extends GoPsiType> implements GoType {
    @Nullable
    protected final PsiType psiType;

    protected GoTypeBase(@Nullable PsiType psiType) {
        this.psiType = psiType;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoExpr x) {
        GoType[] types = x.getType();
        return types.length != 0 && isIdentical(types[0]);
    }

    @Nullable
    @Override
    public final PsiType getPsiType() {
        return psiType;
    }
}
