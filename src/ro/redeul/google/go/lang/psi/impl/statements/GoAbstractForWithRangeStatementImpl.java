package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.typing.*;

public abstract class GoAbstractForWithRangeStatementImpl<Self extends GoAbstractForWithRangeStatementImpl<Self>> extends GoForStatementImpl {

    public GoAbstractForWithRangeStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public abstract GoExpr getRangeExpression();

    public GoType[] getKeyType() {
        GoExpr rangeExpression = getRangeExpression();
        if (rangeExpression == null) {
            return GoType.EMPTY_ARRAY;
        }
        GoType goType;
        GoType[] rangeType = rangeExpression.getType();
        if (rangeType.length == 0) {
            return GoType.EMPTY_ARRAY;
        }
        goType = rangeType[0];

        if (goType instanceof GoTypeArray || goType instanceof GoTypeSlice) {
            return new GoType[]{GoTypeBuiltin.Int};
        }

        if (goType instanceof GoTypeMap) {
            return new GoType[]{((GoTypeMap) goType).getKeyType()};
        }

        if (goType instanceof GoTypeChannel) {
            return new GoType[]{((GoTypeChannel) goType).getElementType()};
        }

        return GoType.EMPTY_ARRAY;
    }

    public GoType[] getValueType() {
        GoExpr rangeExpression = getRangeExpression();
        if (rangeExpression == null) {
            return GoType.EMPTY_ARRAY;
        }
        GoType goType;
        GoType[] rangeType = rangeExpression.getType();
        if (rangeType.length == 0) {
            return GoType.EMPTY_ARRAY;
        }
        goType = rangeType[0];

        if (goType instanceof GoTypeArray) {
            return new GoType[]{((GoTypeArray) goType).getElementType()};
        }

        if (goType instanceof GoTypeSlice) {
            return new GoType[]{((GoTypeSlice) goType).getElementType()};
        }

        if (goType instanceof GoTypeMap) {
            return new GoType[]{((GoTypeMap) goType).getValueType()};
        }

        return GoType.EMPTY_ARRAY;
    }
}
