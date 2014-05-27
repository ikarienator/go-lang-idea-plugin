package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinCallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralExpressionImpl extends GoExpressionBase
        implements GoLiteralExpression {

    public GoLiteralExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralExpression(this);
    }

    @Override
    public GoLiteral getLiteral() {
        return findChildByClass(GoLiteral.class);
    }

    @NotNull
    @Override
    public GoType[] resolveTypes() {
        GoLiteral literal = this.getLiteral();
        if (literal == null)
            return GoType.EMPTY_ARRAY;
        return literal.getType();
    }

    @Override
    public boolean isConstantExpression() {
        GoLiteral literal = getLiteral();
        if (literal == null)
            return true;

        switch (literal.getLiteralType()) {
            case Bool:
            case Char:
            case Float:
            case ImaginaryFloat:
            case ImaginaryInt:
            case Int:
            case InterpretedString:
            case RawString:
                return true;
            case Identifier:
                GoLiteralIdentifier identifier = (GoLiteralIdentifier) literal;

                if (identifier.isIota()) {
                    return true;
                }

                PsiElement resolved = GoPsiUtils.resolveSafely(identifier, PsiElement.class);

                if (resolved == null)
                    return false;

                PsiElement constDecl = resolved.getParent();
                if (constDecl instanceof GoConstDeclaration) {
                    GoPsiType identifiersType = ((GoConstDeclaration) constDecl).getIdentifiersType();
                    //Type was specified
                    if (identifiersType != null) {
                        return ((GoPsiTypeName) identifiersType).isPrimitive();
                    }
                    //If not check the expressions
                    for (GoExpr goExpr : ((GoConstDeclaration) constDecl).getExpressions()) {
                        if (goExpr instanceof GoBinaryExpression || goExpr instanceof GoUnaryExpression) {
                            if (!goExpr.isConstantExpression())
                                return false;
                        }
                    }
                    return true;
                }

        }

        return false;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return GoPsiScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {

        if (BuiltinCallOrConversionReference.MATCHER.accepts(this)) {
            if (getLiteral().getText().matches("print|println"))
                return refs(PsiReference.EMPTY_ARRAY);

            return refs(new BuiltinCallOrConversionReference(this));
        }

        if (CallOrConversionReference.MATCHER.accepts(this))
            return refs(
                    new CallOrConversionReference(this),
                    new VarOrConstReference((GoLiteralIdentifier) this.getLiteral()));

        return super.getReferences();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
