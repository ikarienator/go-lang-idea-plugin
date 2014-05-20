package ro.redeul.google.go.lang.psi.statements;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoForWithRangeAndVarsStatement extends GoForStatement {

    @Nullable
    GoIdentifier getKey();

    GoType[] getKeyType();

    @Nullable
    GoIdentifier getValue();

    GoType[] getValueType();

    GoExpr getRangeExpression();
}
