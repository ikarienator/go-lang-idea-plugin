package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.lang.stubs.GoNamesCache;
import ro.redeul.google.go.services.GoPsiManager;

public class GoLiteralExpressionImpl extends GoExpressionBase
        implements GoLiteralExpression {

    private static final LiteralTypeCalculator TYPE_CALCULATOR = new LiteralTypeCalculator();

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
    public GoType[] getType() {
        return GoPsiManager.getInstance(getProject())
                .getType(this, TYPE_CALCULATOR);
    }

    @Override
    public boolean isConstantExpression() {
        GoLiteral literal = getLiteral();
        if (literal == null)
            return true;

        switch (literal.getConstantType()) {
            case Bool:
            case Char:
            case Float:
            case ImaginaryFloat:
            case ImaginaryInt:
            case Int:
            case InterpretedString:
            case RawString:
                return true;
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

    private static class LiteralTypeCalculator
            implements Function<GoLiteralExpressionImpl, GoType[]> {
        @Override
        public GoType[] fun(GoLiteralExpressionImpl expression) {
            GoLiteral literal = expression.getLiteral();
            if (literal == null)
                return GoType.EMPTY_ARRAY;

            GoNamesCache namesCache =
                    GoNamesCache.getInstance(expression.getProject());

            switch (literal.getConstantType()) {
                case Bool:
                    return new GoType[]{
                            GoTypeBuiltin.Bool
                    };

                case Int:
                    return new GoType[]{
                            GoTypeBuiltin.Int
                    };

                case Float:
                    return new GoType[]{
                            GoTypeBuiltin.Float64
                    };

                case Char:
                    return new GoType[]{
                            GoTypeBuiltin.Rune
                    };

                case ImaginaryInt:
                case ImaginaryFloat:
                    return new GoType[]{
                            GoTypeBuiltin.Complex128
                    };

                case RawString:
                case InterpretedString:
                    if (literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR) {
                        return new GoType[]{
                                GoTypeBuiltin.Rune
                        };
                    } else {
                        return new GoType[]{
                                GoTypeBuiltin.String
                        };
                    }

                case Function:
                    return new GoType[]{
                            GoTypes.fromPsiType((GoLiteralFunction) literal)
                    };

                case Composite:
                    GoLiteralComposite composite = (GoLiteralComposite) literal;
                    GoPsiType literalType = composite.getLiteralType();
                    if (literalType == null) {
                        return GoType.EMPTY_ARRAY;
                    }
                    return new GoType[]{
                            GoTypes.fromPsiType(literalType)
                    };

                default:
                    return GoType.EMPTY_ARRAY;
            }
        }
    }
}
