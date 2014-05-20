package ro.redeul.google.go.lang.psi.declarations;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoStatement;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoVarDeclaration extends GoPsiElement, GoStatement {

    GoIdentifier[] getIdentifiers();

    @Nullable
    GoPsiType getIdentifiersType();

    @Nullable
    GoType getIdentifierType(GoIdentifier identifier);

    GoExpr[] getExpressions();
}
