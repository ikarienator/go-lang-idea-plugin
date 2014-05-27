package ro.redeul.google.go.lang.psi.impl.expressions.literals.composite;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoLiteralCompositeImpl extends GoPsiElementBase
    implements GoLiteralComposite
{
    public GoLiteralCompositeImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoPsiType getPsiType() {
        return findChildByClass(GoPsiType.class);
    }

    @NotNull
    @Override
    public GoLiteralCompositeValue getValue() {
        return findChildByClass(GoLiteralCompositeValue.class);
    }

    @Override
    public Type getLiteralType() {
        return Type.Composite;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        GoPsiType literalType = this.getPsiType();
        if (literalType == null) {
            return GoType.EMPTY_ARRAY;
        }
        return new GoType[]{
                GoTypes.fromPsiType(literalType)
        };
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralComposite(this);
    }
}
