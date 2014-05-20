package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIdentifierExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.statements.GoShortVarDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeNameDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public abstract class AbstractCallOrConversionReference<Reference extends AbstractCallOrConversionReference<Reference>>
        extends GoPsiReference.Single<GoIdentifierExpression, Reference> {

    public static final ElementPattern<GoIdentifierExpression> MATCHER =
            psiElement(GoIdentifierExpression.class)
                    .withChild(psiElement(GoIdentifier.class))
                    .withParent(psiElement(GoCallOrConvExpression.class))
                    .atStartOf(psiElement(GoCallOrConvExpression.class));
    private static final ElementPattern IDENT_IN_SHORT_VAR =
            psiElement(GoIdentifierExpression.class)
                    .withParent(psiElement(GoShortVarDeclaration.class));

    AbstractCallOrConversionReference(GoIdentifierExpression identifier,
                                      ResolveCache.AbstractResolver<Reference, GoResolveResult> resolver) {
        super(identifier, resolver);
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        GoIdentifierExpression identifier = getElement();

        String identifierName = identifier.getName();
        if (identifierName != null) {
            return getElement().getText();
        }

        return getElement().getText();
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        String myName = getCanonicalText();

        if (element instanceof GoTypeNameDeclaration) {
            GoTypeNameDeclaration typeNameDeclaration = (GoTypeNameDeclaration) element;
            return matchesVisiblePackageName(typeNameDeclaration, myName);
        }

        if (element instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration funcDeclaration =
                    (GoFunctionDeclaration) element;

            if (funcDeclaration.getNameIdentifier() != null) {
                return matchesVisiblePackageName(
                        funcDeclaration.getUserData(GoResolveStates.VisiblePackageName),
                        funcDeclaration.getNameIdentifier(), myName);
            }
        }

        if (
                psiElement(GoIdentifierExpression.class)
                        .withParent(
                                psiElement(GoFunctionParameter.class)
                                        .withChild(psiElement(GoPsiTypeFunction.class))
                        ).accepts(element)) {
            return matchesVisiblePackageName(element, myName);
        }

        if (IDENT_IN_SHORT_VAR.accepts(element)) {
            GoShortVarDeclaration shortVars = (GoShortVarDeclaration) element.getParent();

            GoType identifierType =
                    shortVars.getIdentifierType(((GoIdentifierExpression) element).getIdentifier());

            if (identifierType != null && identifierType instanceof GoTypeFunction)
                return matchesVisiblePackageName(element, myName);
        }

        return false;
    }

    @Override
    public boolean isSoft() {
        return true;
    }
}
