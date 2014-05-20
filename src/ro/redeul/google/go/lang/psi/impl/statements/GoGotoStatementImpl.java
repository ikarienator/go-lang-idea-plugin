package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoGotoStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoGotoStatementImpl extends GoPsiElementBase
        implements GoGotoStatement {

    public GoGotoStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier getLabel() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitGotoStatement(this);
    }
}
