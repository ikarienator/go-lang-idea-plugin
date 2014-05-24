package ro.redeul.google.go.lang.psi.expressions.literals.composite;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.typing.GoType;

public interface GoLiteralCompositeElement extends GoPsiElement {

    @Nullable
    GoIdentifier getKey();

    @Nullable
    GoExpr getIndex();

    @Nullable
    GoExpr getExpressionValue();

    @Nullable
    GoLiteralCompositeValue getLiteralValue();

    @Nullable
    GoType getElementType();
}
