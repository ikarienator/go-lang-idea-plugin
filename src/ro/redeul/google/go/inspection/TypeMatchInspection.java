package ro.redeul.google.go.inspection;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;

public class TypeMatchInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {
        new GoRecursiveElementVisitor() {
            @Override
            public void visitBinaryExpression(GoBinaryExpression expression) {
                checkBinaryExpression(result, expression);
            }
        }.visitFile(file);
    }

    public static void checkBinaryExpression(InspectionResult result, GoBinaryExpression expression) {
        GoExpr left = expression.getLeftOperand();
        GoExpr right = expression.getRightOperand();
        if (left == null || right == null) {
            return;
        }
        if (left.isConstantExpression() || right.isConstantExpression()) {
            return;
        }
        GoType[] leftTypes = left.getType();
        GoType[] rightTypes = right.getType();
        if (leftTypes.length == 0 || rightTypes.length == 0) {
            return;
        }
        String operator = expression.getOperator().toString();
        boolean shift = operator.equals("<<") || operator.equals(">>");
        if (operator.equals("!=") || operator.equals("==")) {
            for (GoType leftType : leftTypes) {
                for (GoType rightType : rightTypes) {
                    if (leftType == null || rightType == null) {
                        return;
                    }
                    if (leftType instanceof GoTypeInterface || rightType instanceof GoTypeInterface) {
                        return;
                    }
                    if (leftType instanceof GoTypePointer && rightType instanceof GoTypePointer) {
                        GoTypePointer lptr = (GoTypePointer) leftType;
                        GoTypePointer rptr = (GoTypePointer) rightType;
                        leftType = lptr.getTargetType();
                        rightType = rptr.getTargetType();
                    }
                }
            }
        } else {
            for (GoType leftType : leftTypes) {
                for (GoType rightType : rightTypes) {
                    if (leftType == null || rightType == null) {
                        return;
                    }
                    if (leftType instanceof GoTypePointer || rightType instanceof GoTypePointer) {
                        result.addProblem(expression, "operator " + operator + " not defined on pointer");
                        return;
                    }
                    if (leftType instanceof GoTypeInterface || rightType instanceof GoTypeInterface) {
                        result.addProblem(expression, "operator " + operator + " not defined on interface");
                        return;
                    }
                    if (shift) {
                        String rightUnderStr = rightType.toString();
                        if (rightUnderStr.startsWith("uint") || rightUnderStr.equals("byte")) {
                            return;
                        } else {
                            result.addProblem(expression, "shift count type " + rightType + ", must be unsigned integer");
                            return;
                        }
                    }
                }
            }
        }
        // TODO
        result.addProblem(expression, "mismatched types");
    }
}
