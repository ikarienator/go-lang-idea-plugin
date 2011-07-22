package ro.redeul.google.go.lang.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.IncorrectOperationException;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/24/11
 * Time: 9:00 PM
 */
public abstract class GoStubPsiElementBase<T extends StubElement> extends StubBasedPsiElementBase<T> implements GoPsiElement {

    protected GoStubPsiElementBase(final T stub, IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public GoStubPsiElementBase(final ASTNode node) {
        super(node);
    }

    @Override
    public void delete() throws IncorrectOperationException {
        getParent().deleteChildRange(this, this);
    }

    // @Override
    // public abstract PsiElement getParent();

    public void accept(GoElementVisitor visitor) {
        visitor.visitElement(this);
    }

    @Override
    public void acceptChildren(GoElementVisitor visitor) {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof GoPsiElement) {
                ((GoPsiElement) child).accept(visitor);
            }

            child = child.getNextSibling();
        }
    }
}