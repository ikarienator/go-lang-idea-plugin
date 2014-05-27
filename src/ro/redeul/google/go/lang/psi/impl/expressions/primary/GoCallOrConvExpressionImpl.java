package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoExpressionList;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoTypePsiBacked;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import java.util.Arrays;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoCallOrConvExpressionImpl extends GoExpressionBase
        implements GoCallOrConvExpression {
    private boolean variadic;

    public GoCallOrConvExpressionImpl(@NotNull ASTNode node) {
        super(node);
        variadic = false;
    }

    public GoCallOrConvExpressionImpl(@NotNull ASTNode node, boolean variadic) {
        super(node);
        this.variadic = variadic;
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoPsiElement base = this.getBase();
        if (base instanceof GoPrimaryExpression) {
            GoPrimaryExpression baseExpression = (GoPrimaryExpression) base;

            PsiElement reference = resolveSafely(baseExpression, PsiElement.class);
            // Conversion
            if (reference instanceof GoTypeSpec) {
                return new GoType[]{GoTypes.fromPsiType(((GoTypeSpec) reference).getType())};
            }

            if (reference != null) {
                PsiElement parent = reference.getParent();

                if (parent instanceof GoMethodDeclaration) {
                    GoMethodDeclaration declaration = (GoMethodDeclaration) parent;
                    return GoTypes.fromPsiType(declaration.getReturnType());
                }

                if (parent instanceof GoFunctionDeclaration) {
                    GoFunctionDeclaration declaration = (GoFunctionDeclaration) parent;
                    return GoTypes.fromPsiType(declaration.getReturnType());
                }

                if (parent instanceof GoVarDeclaration) {
                    GoLiteralIdentifier[] identifiers = ((GoVarDeclaration) parent).getIdentifiers();
                    int i;
                    for (i = 0; i < identifiers.length; i++) {
                        if (identifiers[i].getText().equals(getBase().getText())) {
                            break;
                        }
                    }
                    if (i < identifiers.length)
                        return resolveVarTypes((GoVarDeclaration) parent, identifiers[i], i);
                }

            }

            if (baseExpression instanceof GoParenthesisedExpression) {
                GoType[] types = baseExpression.getType();
                if (types.length != 0) {
                    GoType type = types[0];
                    if (type != null) {
                        type = GoTypeUtils.resolveToFinalType(type);
                        if (type instanceof GoTypeFunction) {
                            return GoUtil.getFuncCallTypes(((GoTypeFunction) type).getPsiType());
                        }
                    }
                }
            }

            if (baseExpression instanceof GoLiteralExpression
                    && ((GoLiteralExpression) baseExpression).getLiteral() instanceof GoLiteralFunction) {
                return GoUtil.getFuncCallTypes((GoPsiTypeFunction) ((GoLiteralExpression) baseExpression).getLiteral());
            }

        } else if (base instanceof GoPsiType) {
            // Conversion
            return new GoType[]{GoTypes.fromPsiType((GoPsiType) base)};
        }

        return GoType.EMPTY_ARRAY;
    }

    private GoType[] resolveVarTypes(GoVarDeclaration parent, GoLiteralIdentifier identifier, int i) {

        GoType identifierType = parent.getIdentifierType(identifier);
        if (identifierType != null && identifierType instanceof GoTypePsiBacked) {
            identifierType = GoTypeUtils.resolveToFinalType(identifierType);
            if (identifierType instanceof GoTypeFunction) {
                return GoUtil.getFuncCallTypes(((GoTypeFunction) identifierType).getPsiType());
            }
        }

        GoExpr[] expressions = parent.getExpressions();
        if (expressions.length == 1 && expressions[0] instanceof GoCallOrConvExpression) {
            GoType[] types = expressions[0].getType();
            if (i < types.length) {
                GoType type = types[i];
                if (type instanceof GoTypePsiBacked) {
                    type = GoTypeUtils.resolveToFinalType(type);
                    if (type instanceof GoTypeFunction) {
                        return GoUtil.getFuncCallTypes(((GoTypeFunction) type).getPsiType());
                    }
                }
            }
        }

        if (i < expressions.length) {
            return expressions[i].getType();
        }

        return GoType.EMPTY_ARRAY;
    }

    @Override
    public GoPsiElement getBase() {
        return (GoPsiElement) this.getFirstChild();
    }

    @Override
    public GoPsiType getTypeArgument() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public GoExpr[] getArguments() {

        GoExpressionList list = findChildByClass(GoExpressionList.class);
        if (list != null) {
            return list.getExpressions();
        }

        GoExpr[] expressions = findChildrenByClass(GoExpr.class);

        if (expressions.length <= 1) {
            return GoExpr.EMPTY_ARRAY;
        }

        return Arrays.copyOfRange(expressions, 1, expressions.length);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitCallOrConvExpression(this);
    }

    @Override
    public boolean isConstantExpression() {
        PsiElement reference = resolveSafely(getBase(), PsiElement.class);

        if (reference instanceof GoTypeSpec) {
            GoExpr[] arguments = getArguments();
            return arguments.length == 1 && arguments[0].isConstantExpression();
        }

        return false;
    }

    @Override
    public boolean isVariadic() {
        return variadic;
    }
}
