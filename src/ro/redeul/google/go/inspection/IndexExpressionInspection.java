package ro.redeul.google.go.inspection;

import com.intellij.codeInspection.ProblemHighlightType;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFloat;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIndexExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.psi.visitors.GoRecursiveElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import java.math.BigDecimal;

import static ro.redeul.google.go.util.GoTypeInspectUtil.checkValidLiteralIntExpr;

public class IndexExpressionInspection extends AbstractWholeGoFileInspection {
    @Override
    protected void doCheckFile(@NotNull GoFile file, @NotNull final InspectionResult result) {

        new GoRecursiveElementVisitor() {
            @Override
            public void visitIndexExpression(GoIndexExpression expression) {
                checkIndexExpression(expression, result);
            }


        }.visitFile(file);
    }

    private void checkIndexExpression(GoIndexExpression expression, InspectionResult result) {
        GoExpr indexExpr = expression.getIndex();
        if (indexExpr == null)
            return;
        for (GoType goType : expression.getBaseExpression().getType()) {
            if (goType != null) {
                goType = GoTypeUtils.resolveToFinalType(goType);

                if (goType instanceof GoPsiTypeArray || goType instanceof GoPsiTypeSlice) {
                    checkIndexSliceArray(indexExpr, result);
                }
                if (goType instanceof GoPsiTypeMap) {
                    checkIndexMap(((GoPsiTypeMap) goType).getKeyType(), indexExpr, result);
                }
            }
        }
    }

    private void checkIndexMap(GoPsiType keyType, GoExpr indexExpr, InspectionResult result) {
        if (!keyType.getType().isAssignableFrom(indexExpr)) {
            result.addProblem(
                    indexExpr,
                    GoBundle.message("warning.functioncall.type.mismatch", GoUtil.getNameLocalOrGlobal(keyType, (GoFile) indexExpr.getContainingFile())),
                    ProblemHighlightType.GENERIC_ERROR_OR_WARNING, new CastTypeFix(indexExpr, keyType));
        }
    }

    private void checkIndexSliceArray(GoExpr index, InspectionResult result) {


        if (index instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) index).getLiteral();
            if (literal instanceof GoLiteralInteger)
                return;
            if (literal instanceof GoLiteralFloat) {
                BigDecimal value = ((GoLiteralFloat) literal).getValue();
                if (!value.toString().matches("^[0-9]+\\.0+$")) {
                    result.addProblem(
                            index,
                            GoBundle.message("warning.functioncall.type.mismatch", "int"));
                    return;
                }
                return;
            }
            if (literal instanceof GoLiteralString) {
                result.addProblem(
                        index,
                        GoBundle.message("warning.functioncall.type.mismatch", "int"));
                return;
            }

        }
        if (index.isConstantExpression()) {
            Number numValue = FunctionCallInspection.getNumberValueFromLiteralExpr(index);
            if (numValue == null) {
                if (!checkValidLiteralIntExpr(index)) {
                    result.addProblem(
                            index,
                            GoBundle.message("warning.functioncall.type.mismatch", "int"));
                }
            } else {
                if (numValue instanceof Integer || numValue.intValue() == numValue.floatValue()) {
                    Integer value = numValue.intValue();
                    if (value < 0) {
                        result.addProblem(
                                index,
                                GoBundle.message("warning.index.invalid", value, "(index must be non-negative)"));
                    }

                } else {
                    result.addProblem(
                            index,
                            GoBundle.message("warning.functioncall.type.mismatch", "int"));
                }
            }
            return;
        }

        for (GoType goType : index.getType()) {
            if (goType != null) {
                GoType resolvedGoType = GoTypeUtils.resolveToFinalType(goType);
                if (resolvedGoType == null || !resolvedGoType.isIdentical(GoTypeBuiltin.Int))
                    result.addProblem(
                            index,
                            GoBundle.message("warning.functioncall.type.mismatch", "int"));
                return;
            }
        }
    }
}
