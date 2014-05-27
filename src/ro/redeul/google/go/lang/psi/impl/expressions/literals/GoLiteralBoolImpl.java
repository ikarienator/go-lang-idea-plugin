package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralBool;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedBool;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralBoolImpl extends GoPsiElementBase
    implements GoLiteralBool {

    private final GoType[] type;

    public GoLiteralBoolImpl(@NotNull ASTNode node) {
        super(node);
        type = new GoType[] { new GoTypeUntypedBool(getValue()) };
    }

    @NotNull
    @Override
    public Type getLiteralType() {
        return Type.Bool;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        return type;
    }

    @NotNull
    @Override
    public Boolean getValue() {
        return this.getText().equals("true");
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralBool(this);
    }
}
