package ro.redeul.google.go.lang.psi.typing;

import ro.redeul.google.go.lang.psi.types.GoPsiType;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public abstract class GoTypePsiBacked<PsiType extends GoPsiType> extends GoAbstractType {

    private final PsiType psiType;

    GoTypePsiBacked(PsiType psiType) {
        this.psiType = psiType;
    }

    public PsiType getPsiType() {
        return psiType;
    }

}
