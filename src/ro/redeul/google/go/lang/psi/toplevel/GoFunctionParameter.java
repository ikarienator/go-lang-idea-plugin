package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoFunctionParameter extends GoPsiElement {

    public static final GoFunctionParameter[] EMPTY_ARRAY =
        new GoFunctionParameter[0];

    boolean isVariadic();

    GoIdentifier[] getIdentifiers();

    GoPsiType getType();
}
