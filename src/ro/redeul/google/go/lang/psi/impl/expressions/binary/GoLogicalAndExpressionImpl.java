package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.binary.GoLogicalAndExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;

public class GoLogicalAndExpressionImpl extends GoBinaryExpressionImpl
        implements GoLogicalAndExpression {
    public GoLogicalAndExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    protected GoType[] resolveTypes() {
        return new GoType[]{
                GoTypeBuiltin.Bool
        };
    }
}

