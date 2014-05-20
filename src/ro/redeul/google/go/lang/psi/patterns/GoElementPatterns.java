package ro.redeul.google.go.lang.psi.patterns;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclarations;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.GoLabeledStatement;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeDeclaration;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;

public class GoElementPatterns {

    public static final ElementPattern<GoIdentifier> GLOBAL_CONST_DECL =
        psiElement(GoIdentifier.class)
            .withParent(
                psiElement(GoConstDeclaration.class)
                    .withParent(
                        psiElement(GoConstDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoIdentifier> CONST_DECLARATION =
        psiElement(GoIdentifier.class)
            .withParent(GoConstDeclaration.class);

    public static final ElementPattern<GoIdentifier> GLOBAL_VAR_DECL =
        psiElement(GoIdentifier.class)
            .withParent(
                psiElement(GoVarDeclaration.class)
                    .withParent(
                        psiElement(GoVarDeclarations.class)
                            .withParent(psiElement(GoFile.class))));

    public static final ElementPattern<GoIdentifier> METHOD_DECLARATION =
        psiElement(GoIdentifier.class)
            .withParent(GoMethodDeclaration.class);

    public static final ElementPattern<GoIdentifier> FUNCTION_DECLARATION =
            psiElement(GoIdentifier.class)
                    .withParent(GoFunctionDeclaration.class);

    @SuppressWarnings("unchecked")
    public static final ElementPattern<GoIdentifier> VAR_DECLARATION =
        psiElement(GoIdentifier.class)
            .withParent(
                or(
                    psiElement(GoShortVarDeclaration.class),
                    psiElement(GoVarDeclaration.class),
                    psiElement(GoTypeStructField.class),
                    psiElement(GoFunctionParameter.class)
                )
            );

    public static final ElementPattern<GoIdentifier> VAR_IN_FOR_RANGE =
            psiElement(GoIdentifier.class)
                    .withParent(
                            psiElement(GoForWithRangeAndVarsStatement.class)
                    );

    public static final ElementPattern<GoIdentifier> PARAMETER_DECLARATION =
        psiElement(GoIdentifier.class)
            .withParent(GoFunctionParameter.class);

    @SuppressWarnings("unchecked")
    public static final ElementPattern<? extends PsiElement> BLOCK_DECLARATIONS =
        or(
            psiElement(GoShortVarDeclaration.class),
            psiElement(GoVarDeclarations.class),
            psiElement(GoTypeDeclaration.class),
            psiElement(GoConstDeclarations.class),
            psiElement(GoLabeledStatement.class)
        );

}
