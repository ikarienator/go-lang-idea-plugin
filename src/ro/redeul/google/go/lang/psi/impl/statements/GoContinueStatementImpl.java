package ro.redeul.google.go.lang.psi.impl.statements;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.statements.GoContinueStatement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

public class GoContinueStatementImpl extends GoPsiElementBase
        implements GoContinueStatement {

    public GoContinueStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier getLabel() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitContinueStatement(this);
    }
}
