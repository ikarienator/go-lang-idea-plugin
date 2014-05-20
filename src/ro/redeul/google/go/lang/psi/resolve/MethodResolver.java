package ro.redeul.google.go.lang.psi.resolve;

import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.resolve.references.MethodReference;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeNamed;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;

import java.util.Set;

import static ro.redeul.google.go.lang.completion.GoCompletionContributor.DUMMY_IDENTIFIER;

public class MethodResolver extends GoPsiReferenceResolver<MethodReference> {
    public MethodResolver(MethodReference reference) {
        super(reference);
    }

    @Override
    public void visitMethodDeclaration(GoMethodDeclaration declaration) {
        if (isReferenceTo(declaration))
            addDeclaration(declaration, declaration.getNameIdentifier());
    }

    boolean isReferenceTo(GoMethodDeclaration declaration) {
        GoType receiverType = declaration.getMethodReceiver().getType().getType();

        if (receiverType == null)
            return false;

        if (receiverType instanceof GoTypePointer) {
            receiverType = ((GoTypePointer) receiverType).getTargetType();
        }

        if (!(receiverType instanceof GoTypeNamed))
            return false;

        GoTypeNamed methodTypeName = (GoTypeNamed) receiverType;

        Set<GoTypeNamed> receiverTypes = getReference().resolveBaseReceiverTypes();

        GoIdentifier identifier = getReference().getElement().getIdentifier();
        if (identifier == null) {
            return false;
        }
        for (GoTypeNamed type : receiverTypes) {
            if (type.getName().equals(methodTypeName.getName())) {
                String methodName = declaration.getFunctionName();
                String referenceName = identifier.getUnqualifiedName();

                return referenceName.contains(DUMMY_IDENTIFIER) ||
                        referenceName.equals(methodName);
            }
        }

        return false;
    }
}
