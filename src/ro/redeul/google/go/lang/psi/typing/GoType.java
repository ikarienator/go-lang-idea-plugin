package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoType {

    public static GoType[] EMPTY_ARRAY = new GoType[0];

    public static GoType Undefined = new GoType() {
        @Override
        public boolean isIdentical(GoType other) {
            return false;
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


        @Override
        public String toString() {
            return "undefined";
        }
    };

    boolean isIdentical(GoType other);

    boolean isAssignableFrom(@NotNull GoExpr x);

    @Nullable
    GoPsiType getPsiType();

    // method sets
}
