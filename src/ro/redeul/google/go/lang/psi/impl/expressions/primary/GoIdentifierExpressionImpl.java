package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoUnaryExpression;
import ro.redeul.google.go.lang.psi.expressions.binary.GoBinaryExpression;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIdentifierExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinCallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CallOrConversionReference;
import ro.redeul.google.go.lang.psi.resolve.references.CompositeElementOfStructFieldReference;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.statements.GoForWithRangeAndVarsStatement;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeClause;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeGuard;
import ro.redeul.google.go.lang.psi.statements.switches.GoSwitchTypeStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.util.GoUtil;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findParentOfType;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

public class GoIdentifierExpressionImpl extends GoExpressionBase implements GoIdentifierExpression {

    private int iotaValue;
    private final boolean iota;
    private final GoIdentifier identifier;

    public GoIdentifierExpressionImpl(@NotNull ASTNode node) {
        this(node, false);
    }

    public GoIdentifierExpressionImpl(@NotNull ASTNode node, boolean iota) {
        super(node);
        identifier = findChildByClass(GoIdentifier.class);
        assert identifier != null;
        this.iota = iota;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitIdentifierExpression(this);
    }

    @Override
    public String getName() {
        return this.getIdentifier().getName();
    }

    @Override
    public GoIdentifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isBlank() {
        return getText().equals("_");
    }

    @Override
    public boolean isIota() {
        return iota;
    }

    @Override
    public Integer getIotaValue() {
        return iotaValue;
    }

    @Override
    public void setIotaValue(int value) {
        iotaValue = value;
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        if (BuiltinCallOrConversionReference.MATCHER.accepts(this)) {
            if (getText().matches("print|println"))
                return refs(PsiReference.EMPTY_ARRAY);

            return refs(new BuiltinCallOrConversionReference(this));
        }

        if (CallOrConversionReference.MATCHER.accepts(this))
            return refs(
                    new CallOrConversionReference(this),
                    new VarOrConstReference(this));


        if (CompositeElementOfStructFieldReference.MATCHER_KEY.accepts(this)) {
            GoLiteralComposite composite = findParentOfType(this, GoLiteralComposite.class);
            if (resolveToFinalType(composite.getLiteralType().getType()) instanceof GoTypeStruct) {
                return refs(
                        new CompositeElementOfStructFieldReference(this)
                );
            }

            return refs(
                    new CompositeElementOfStructFieldReference(this),
                    new VarOrConstReference(this)
            );
        }

        if (CompositeElementOfStructFieldReference.MATCHER_ELEMENT.accepts(this))
            return refs(new VarOrConstReference(this));

        return refs(new VarOrConstReference(this));
    }

    @Override
    public boolean isConstantExpression() {
        if (isIota()) {
            return true;
        }

        GoIdentifier identifier = getIdentifier();

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

        return false;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        PsiElement resolved = GoUtil.ResolveReferece(this);
        if (resolved == null) {
            return GoType.EMPTY_ARRAY;
        }

        PsiElement parent = resolved.getParent();
        if (parent instanceof GoVarDeclaration) {
            GoVarDeclaration varDeclaration = (GoVarDeclaration) parent;
            GoType identifierType = varDeclaration.getIdentifierType((GoIdentifier) resolved);

            if (identifierType == null)
                return GoType.EMPTY_ARRAY;

            return new GoType[]{identifierType};
        }

        if (parent instanceof GoConstDeclaration) {
            GoPsiType identifiersType = ((GoConstDeclaration) parent).getIdentifiersType();
            if (identifiersType == null)
                return GoType.EMPTY_ARRAY;
            return new GoType[]{GoTypes.fromPsiType(identifiersType)};
        }

        if (parent instanceof GoFunctionParameter) {
            GoFunctionParameter functionParameter = (GoFunctionParameter) parent;
            if (functionParameter.getType() != null) {
                if (functionParameter.isVariadic()) {
                    return new GoType[]{
                            new GoTypeSlice(null, GoTypes.fromPsiType(functionParameter.getType()))
                    };
                } else {
                    return new GoType[]{
                            GoTypes.fromPsiType(functionParameter.getType())
                    };
                }
            }
        }

        if (parent instanceof GoMethodReceiver) {
            GoMethodReceiver receiver =
                    (GoMethodReceiver) parent;

            if (receiver.getType() != null) {
                return new GoType[]{
                        GoTypes.fromPsiType(receiver.getType())
                };
            }
        }

        if (parent instanceof GoFunctionDeclaration) {
            GoFunctionDeclaration functionDeclaration =
                    (GoFunctionDeclaration) parent;

            return new GoType[]{
                    GoTypes.fromPsiType(functionDeclaration)
            };
        }

        if (parent instanceof GoSwitchTypeGuard) {
            GoSwitchTypeGuard guard = (GoSwitchTypeGuard) parent;
            GoSwitchTypeStatement switchStatement = (GoSwitchTypeStatement) guard.getParent();
            TextRange litRange = identifier.getTextRange();
            for (GoSwitchTypeClause clause : switchStatement.getClauses()) {
                TextRange clauseTextRange = clause.getTextRange();
                if (clauseTextRange.contains(litRange)) {
                    return GoTypes.fromPsiType(clause.getTypes());
                }
            }
        }

        if (GoElementPatterns.VAR_IN_FOR_RANGE.accepts(resolved)) {
            GoForWithRangeAndVarsStatement statement = (GoForWithRangeAndVarsStatement) parent;

            if (statement.getKey() == resolved) {
                return statement.getKeyType();
            } else if (statement.getValue() == resolved) {
                return statement.getValueType();
            }
        }
        return GoType.EMPTY_ARRAY;
    }
}
