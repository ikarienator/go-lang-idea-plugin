package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoTypeAssertionExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.stubs.GoNamesCache;

public class GoTypeAssertionExpressionImpl extends GoExpressionBase
        implements GoTypeAssertionExpression {
    public GoTypeAssertionExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        GoNamesCache namesCache = GoNamesCache.getInstance(getProject());
        return new GoType[]{
                GoTypes.fromPsiType(getAssertedType()),
                GoTypeBuiltin.Bool
        };
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    public GoPsiType getAssertedType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public boolean isConstantExpression() {
        return false;
    }
}
