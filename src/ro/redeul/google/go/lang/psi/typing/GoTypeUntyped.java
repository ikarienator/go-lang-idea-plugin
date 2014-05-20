package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public abstract class GoTypeUntyped<Value> implements GoType {
    @NotNull
    protected final Value value;

    protected GoTypeUntyped(@NotNull Value value) {
        this.value = value;
    }

    @NotNull
    public Value getValue() {
        return value;
    }

    @Override
    public boolean isIdentical(GoType other) {
        return other == this || other instanceof GoTypeUntyped && getValue().equals(((GoTypeUntyped) other).getValue());
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoExpr x) {
        return false;
    }

    @Nullable
    @Override
    public GoPsiType getPsiType() {
        return null;
    }
}
