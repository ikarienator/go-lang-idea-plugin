package ro.redeul.google.go.lang.psi.statements.switches;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public interface GoSwitchTypeGuard extends GoPsiElement {

    @Nullable
    GoIdentifier getIdentifier();

    @NotNull
    GoExpr getExpression();
}
