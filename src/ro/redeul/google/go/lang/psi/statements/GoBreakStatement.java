package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;

public interface GoBreakStatement extends GoPsiElement {

    GoIdentifier getLabel();

}
