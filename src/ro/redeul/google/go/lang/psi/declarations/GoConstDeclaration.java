package ro.redeul.google.go.lang.psi.declarations;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoConstDeclaration extends GoPsiElement {

    boolean hasInitializers();

    GoIdentifier[] getIdentifiers();

    public GoPsiType getIdentifiersType();

    GoExpr[] getExpressions();

    GoExpr getExpression(GoIdentifier identifier);

    Integer getConstSpecIndex();

}
