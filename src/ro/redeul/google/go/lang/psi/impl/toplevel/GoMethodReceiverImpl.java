package ro.redeul.google.go.lang.psi.impl.toplevel;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodReceiver;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoMethodReceiverImpl extends GoPsiElementBase
    implements GoMethodReceiver
{
    public GoMethodReceiverImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier getIdentifier() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    public boolean isReference() {
        return findChildByType(GoTokenTypes.oMUL) != null;
    }

    @Override
    public GoPsiType getType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitMethodReceiver(this);
    }
}
