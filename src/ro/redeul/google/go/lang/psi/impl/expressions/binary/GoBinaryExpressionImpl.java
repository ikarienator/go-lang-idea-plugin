package ro.redeul.google.go.lang.psi.impl.expressions.binary;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeUntyped;
import ro.redeul.google.go.lang.psi.typing.untyped.Calculator;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public abstract class GoBinaryExpressionImpl extends GoExpressionBase
        implements GoBinaryExpression, GoTokenTypes {

    GoBinaryExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitBinaryExpression(this);
    }

    @Override
    public IElementType getOperator() {
        PsiElement child = findChildByFilter(GoElementTypes.BINARY_OPS);
        return child != null ? child.getNode().getElementType() : null;
    }

    @Override
    @Nullable
    public GoExpr getLeftOperand() {
        GoExpr[] children = findChildrenByClass(GoExpr.class);
        return children.length == 0 ? null : children[0];
    }

    @Override
    public GoExpr getRightOperand() {
        GoExpr[] children = findChildrenByClass(GoExpr.class);
        return children.length <= 1 ? null : children[1];
    }

    @Override
    public boolean isConstantExpression() {
        GoExpr leftOperand = getLeftOperand();
        GoExpr rightOperand = getRightOperand();

        return leftOperand != null && leftOperand.isConstantExpression() && rightOperand != null && rightOperand.isConstantExpression();
    }

    @NotNull
    @Override
    protected GoType[] resolveTypes() {
        GoExpr leftOperand = getLeftOperand();
        GoExpr rightOperand = getRightOperand();

        if (leftOperand == null && rightOperand == null)
            return GoType.EMPTY_ARRAY;

        if (leftOperand == null)
            return rightOperand.getType();

        if (rightOperand == null)
            return leftOperand.getType();

        GoType[] leftTypes = leftOperand.getType();
        GoType[] rightTypes = rightOperand.getType();

        if (leftTypes.length == 1 && rightTypes.length == 1) {
            if (leftTypes[0] instanceof GoTypeUntyped && rightTypes[0] instanceof GoTypeUntyped) {
                GoTypeUntyped leftType = (GoTypeUntyped) leftTypes[0];
                GoTypeUntyped rightType = (GoTypeUntyped) rightTypes[0];
                // Constant expression
                GoTypeUntyped result = Calculator.operate(getOperator(), leftType, rightType);
                if (result == null) {
                    return GoType.EMPTY_ARRAY;
                }
                return new GoType[]{result};
            }
            if (leftTypes[0].isIdentical(rightTypes[0])) {
                return leftTypes;
            }
        }
        return GoType.EMPTY_ARRAY;
    }
}
