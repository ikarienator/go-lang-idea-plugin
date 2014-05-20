package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.impl.GoFunction;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoPsiTypeFunctionImpl extends GoFunction implements GoPsiTypeFunction {
    public GoPsiTypeFunctionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitFunctionType(this);
    }
}
