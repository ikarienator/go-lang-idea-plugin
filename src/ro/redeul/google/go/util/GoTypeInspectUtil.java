package ro.redeul.google.go.util;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.GoBundle;
import ro.redeul.google.go.inspection.InspectionResult;
import ro.redeul.google.go.inspection.fix.CastTypeFix;
import ro.redeul.google.go.inspection.fix.ChangeReturnsParametersFix;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.statements.GoReturnStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.utils.GoExpressionUtils;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class GoTypeInspectUtil {


    public static boolean checkIsInterface(GoPsiType psiType) {
        if (psiType instanceof GoPsiTypeInterface)
            return true;
        if (psiType instanceof GoPsiTypeName)
            return psiType.getName().equals("error") && ((GoPsiTypeName) psiType).isPrimitive();
        if (psiType instanceof GoPsiTypeSlice)
            return checkIsInterface(((GoPsiTypeSlice) psiType).getElementType());
        if (psiType instanceof GoPsiTypePointer)
            return checkIsInterface(((GoPsiTypePointer) psiType).getTargetType());
        if (psiType instanceof GoPsiTypeArray)
            return checkIsInterface(((GoPsiTypeArray) psiType).getElementType());
        if (psiType instanceof GoPsiTypeChannel)
            return checkIsInterface(((GoPsiTypeChannel) psiType).getElementType());
        return false;
    }

    public static boolean IsNil(PsiElement psiElement) {
        if (psiElement instanceof GoParenthesisedExpression)
            return IsNil(((GoParenthesisedExpression) psiElement).getInnerExpression());
        if (psiElement instanceof GoLiteralExpression)
            psiElement = ((GoLiteralExpression) psiElement).getLiteral();
        return psiElement instanceof GoIdentifier && ((GoIdentifier) psiElement).getName().equals("nil");
    }

    public static boolean checkValidLiteralFloatExpr(GoExpr expr) {
        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoIdentifier) {
                //Never will be null
                PsiElement goPsiElement = GoUtil.ResolveReferece(literal).getParent();
                if (goPsiElement instanceof GoConstDeclaration) {
                    for (GoExpr goExpr : ((GoConstDeclaration) goPsiElement).getExpressions()) {
                        if (!checkValidLiteralFloatExpr(goExpr))
                            return false;
                    }
                }
                return true;
            }
            if (literal instanceof GoLiteralExpression)
                return checkValidLiteralIntExpr((GoExpr) literal);
            return literal instanceof GoLiteralFloat || literal instanceof GoLiteralInteger || literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR;
        }
        if (expr instanceof GoBinaryExpression) {
            if (!checkValidLiteralFloatExpr(((GoBinaryExpression) expr).getLeftOperand()))
                return false;
            return checkValidLiteralFloatExpr(((GoBinaryExpression) expr).getRightOperand());
        }
        if (expr instanceof GoUnaryExpression)
            return checkValidLiteralFloatExpr(((GoUnaryExpression) expr).getExpression());
        return false;
    }

    public static boolean checkValidLiteralIntExpr(GoExpr expr) {
        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoIdentifier) {
                //Never will be null
                PsiElement goPsiElement = GoUtil.ResolveReferece(literal).getParent();
                if (goPsiElement instanceof GoConstDeclaration) {
                    for (GoExpr goExpr : ((GoConstDeclaration) goPsiElement).getExpressions()) {
                        if (!checkValidLiteralIntExpr(goExpr))
                            return false;
                    }
                }
                return true;
            }
            if (literal instanceof GoLiteralExpression)
                return checkValidLiteralIntExpr((GoExpr) literal);
            if (literal instanceof GoLiteralInteger || literal.getNode().getElementType() == GoElementTypes.LITERAL_CHAR)
                return true;
            return literal instanceof GoLiteralFloat && literal.getText().matches("^[0-9]*\\.0*$");
        }
        if (expr instanceof GoBinaryExpression) {
            if (!checkValidLiteralIntExpr(((GoBinaryExpression) expr).getLeftOperand()))
                return false;
            return checkValidLiteralIntExpr(((GoBinaryExpression) expr).getRightOperand());
        }
        if (expr instanceof GoUnaryExpression)
            return checkValidLiteralIntExpr(((GoUnaryExpression) expr).getExpression());
        if (expr instanceof GoParenthesisedExpression)
            return checkValidLiteralIntExpr(((GoParenthesisedExpression) expr).getInnerExpression());
        return false;
    }

    public static void checkFunctionTypeArguments(GoCallOrConvExpression call, InspectionResult result) {
        GoFunctionDeclaration goFunctionDeclaration = GoExpressionUtils.resolveToFunctionDeclaration(call);
        GoExpr[] goExprs = call.getArguments();
        int index = 0;

        if (goFunctionDeclaration == null)
            return;
    }


    public static boolean checkFunctionTypeReturns(GoReturnStatement statement, InspectionResult result) {
        int index = 0;

        GoFunctionDeclaration goFunctionDeclaration = GoPsiUtils.findParentOfType(statement, GoFunctionDeclaration.class);
        if (goFunctionDeclaration == null)
            return true;
        GoExpr[] goExprs = statement.getExpressions();

        GoFunctionParameter[] results = goFunctionDeclaration.getResults();
        if (goExprs.length == 1 && goExprs[0] instanceof GoCallOrConvExpression) {

            GoFunctionDeclaration goFunctionDeclarationResolved = GoExpressionUtils.resolveToFunctionDeclaration(goExprs[0]);
            for (GoFunctionParameter resolvedParameter : goFunctionDeclarationResolved.getResults()) {
                if (!resolvedParameter.getType().getType().isAssignableFrom(goExprs[index])) {
                    result.addProblem(
                            goExprs[0],
                            GoBundle.message("warning.function.return.call.types.mismatch"),
                            new ChangeReturnsParametersFix(statement));
                    return false;
                }
                index++;
            }
            return true;

        }

        for (GoFunctionParameter functionParameter : results) {
            if (index >= goExprs.length)
                return false;
            GoPsiType type = functionParameter.getType();
            GoIdentifier[] identifiers = functionParameter.getIdentifiers();
            String typeName = type != null ? type.getText() : "";
            if (identifiers.length < 2) {
                GoExpr goExpr = goExprs[index];
                if (!functionParameter.getType().getType().isAssignableFrom(goExpr)) {
                    result.addProblem(
                            goExpr,
                            GoBundle.message("warning.functioncall.type.mismatch", typeName),
                            new CastTypeFix(goExpr, type),
                            new ChangeReturnsParametersFix(statement));
                    return false;
                }
                index++;
            } else {
                for (GoIdentifier goIdentifier : identifiers) {
                    GoExpr goExpr = goExprs[index];
                    if (!functionParameter.getType().getType().isAssignableFrom(goExpr)) {
                        result.addProblem(
                                goExpr,
                                GoBundle.message("warning.functioncall.type.mismatch", typeName),
                                new CastTypeFix(goExpr, type),
                                new ChangeReturnsParametersFix(statement));

                        return false;
                    }
                    index++;
                }
            }

        }
        return true;
    }
}
