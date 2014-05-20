package ro.redeul.google.go.lang.psi.toplevel;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public interface GoMethodReceiver extends GoPsiElement {

    GoIdentifier getIdentifier();

    boolean isReference();

    GoPsiType getType();

}
