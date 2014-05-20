package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.impl.GoFunction;
import ro.redeul.google.go.lang.psi.statements.GoBlockStatement;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

public class GoLiteralFunctionImpl extends GoFunction
        implements GoLiteralFunction {

    public GoLiteralFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public GoFunctionDeclaration getValue() {
        return this;
    }

    @Override
    public Type getConstantType() {
        return Type.Function;
    }

    @Override
    public String getFunctionName() {
        return null;
    }

    @Override
    public boolean isMain() {
        return false;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        return null;
    }

    @Override
    public GoBlockStatement getBlock() {
        return findChildByClass(GoBlockStatement.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionLiteral(this);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        for (GoFunctionParameter functionParameter : getParameters()) {
            if (!processor.execute(functionParameter, state)) {
                return false;
            }
        }

        return processor.execute(this, state);
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public String getQualifiedName() {
        return "";
    }

    @NotNull
    @Override
    public GoPsiType[] getReturnType() {
        List<GoPsiType> types = new ArrayList<GoPsiType>();

        GoFunctionParameter[] results = getResults();
        for (GoFunctionParameter result : results) {
            GoIdentifier identifiers[] = result.getIdentifiers();

            if (identifiers.length == 0 && result.getType() != null) {
                types.add(result.getType());
            } else {
                for (GoIdentifier identifier : identifiers) {
                    types.add(result.getType());
                }
            }
        }

        return types.toArray(new GoPsiType[types.size()]);
    }
}
