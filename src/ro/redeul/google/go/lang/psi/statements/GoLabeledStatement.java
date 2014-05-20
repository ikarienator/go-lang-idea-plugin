package ro.redeul.google.go.lang.psi.statements;

import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;

public interface GoLabeledStatement extends GoStatement {
    GoIdentifier getLabel();

    GoStatement getStatement();
}
