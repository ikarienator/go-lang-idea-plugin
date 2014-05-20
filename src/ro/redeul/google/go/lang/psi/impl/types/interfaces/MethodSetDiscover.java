package ro.redeul.google.go.lang.psi.impl.types.interfaces;

import ro.redeul.google.go.lang.psi.types.interfaces.GoTypeInterfaceMethodSet;
import ro.redeul.google.go.lang.psi.typing.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodSetDiscover {

    private final GoTypeInterface myType;
    private final Set<String> myIgnoredNames;
    private GoTypeInterfaceMethodSet myMethodSet;

    public MethodSetDiscover(GoTypeInterface psiType) {
        this(psiType, new HashSet<String>());
    }

    private MethodSetDiscover(GoTypeInterface psiType,
                              Set<String> ignoredNames) {
        this.myType = psiType;
        this.myIgnoredNames = ignoredNames;
    }

    public GoTypeInterfaceMethodSet getMethodSet() {
        discover();
        return myMethodSet;
    }

    private void discover() {
        myMethodSet = new GoTypeInterfaceMethodSet();

        for (GoTypeNamed embeddedInterface : myType.getEmbededTypes()) {
            GoTypeInterface typeInterface = GoTypes.resolveToInterface(embeddedInterface);

            if (typeInterface == null) {
                continue;
            }

            GoTypeInterfaceMethodSet methodSet =
                    new MethodSetDiscover(typeInterface, myIgnoredNames).getMethodSet();

            myMethodSet.merge(methodSet);
        }

        for (Map.Entry<String, GoType> functionDeclaration : myType.getMethods().entrySet()) {
            myMethodSet.add((ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration) functionDeclaration.getValue().getPsiType());
        }
    }
}
