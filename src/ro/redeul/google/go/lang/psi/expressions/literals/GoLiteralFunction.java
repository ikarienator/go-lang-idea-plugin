package ro.redeul.google.go.lang.psi.expressions.literals;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;

public interface GoLiteralFunction extends GoLiteral<GoFunctionDeclaration>, GoPsiTypeFunction, GoFunctionDeclaration {
    @NotNull
    GoFunctionParameter[] getParameters();

    @NotNull
    GoFunctionParameter[] getResults();

    GoBlockStatement getBlock();
}
