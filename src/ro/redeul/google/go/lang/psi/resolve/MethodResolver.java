package ro.redeul.google.go.lang.psi.resolve;

import com.intellij.psi.PsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.MethodReference;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.typing.GoTypeName;

import java.util.Set;

import static ro.redeul.google.go.lang.completion.GoCompletionContributor.DUMMY_IDENTIFIER;

public class MethodResolver extends GoPsiResolver {
    private final Set<GoTypeName> receiverTypes;
    private final GoLiteralIdentifier identifier;


    public MethodResolver(MethodReference reference) {
        this(reference.resolveBaseReceiverTypes(), reference.getElement().getIdentifier());
    }

    public MethodResolver(Set<GoTypeName> receiverTypes, GoLiteralIdentifier identifier) {
        this.receiverTypes = receiverTypes;
        this.identifier = identifier;
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        if (isReferenceToMethodDeclaration(declaration))
            addDeclaration(declaration, declaration.getNameIdentifier());
    }

    boolean isReferenceToMethodDeclaration(GoMethodDeclaration declaration) {
        GoPsiType receiverType = declaration.getMethodReceiver().getType();

        if (receiverType == null)
            return false;

        if (receiverType instanceof GoPsiTypePointer) {
            receiverType = ((GoPsiTypePointer) receiverType).getTargetType();
        }

        if (!(receiverType instanceof GoPsiTypeName))
            return false;

        GoPsiTypeName methodTypeName = (GoPsiTypeName) receiverType;

        if (identifier == null) {
            return false;
        }
        for (GoTypeName type : receiverTypes) {
            if (type.getName().equals(methodTypeName.getName())) {
                String methodName = declaration.getFunctionName();
                String referenceName = identifier.getUnqualifiedName();

                return referenceName.contains(DUMMY_IDENTIFIER) ||
                        referenceName.equals(methodName);
            }
        }

        return false;
    }

    @Override
    protected String getRefName() {
        return identifier.getText();
    }

    @Override
    protected boolean isReferenceTo(PsiElement element) {
        return element instanceof GoMethodDeclaration && isReferenceToMethodDeclaration((GoMethodDeclaration) element);
    }
}
