package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

public abstract class GoPsiReferenceResolver<Reference extends PsiReference>
    extends GoPsiResolver {

    private final Reference reference;

    GoPsiReferenceResolver(Reference reference) {
        this.reference = reference;
    }

    public Reference getReference() {
        return reference;
    }

    @Override
    protected String getRefName() {
        return reference.getElement().getText();
    }

    @Override
    protected boolean isReferenceTo(PsiElement element) {
        return reference.isReferenceTo(element);
    }
}
