package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoPsiTypeParenthesizedImpl extends GoPsiPackagedElementBase
    implements GoPsiTypeParenthesized
{
    public GoPsiTypeParenthesizedImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoPsiType getInnerType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public String getPresentationTailText() {
        return String.format("(%s)", getInnerType().getPresentationTailText());
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeParenthesized(this);
    }
}
