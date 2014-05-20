package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

import java.util.HashMap;
import java.util.Map;

public class GoTypeInterface extends GoTypeBase<GoPsiTypeInterface> {
    @NotNull
    private final HashMap<String, GoType> methods = new HashMap<String, GoType>();
    @NotNull
    private final GoTypeNamed[] embededTypes;
    private Map functionDeclarations;

    public GoTypeInterface(@NotNull GoPsiTypeInterface psiType, @NotNull String[] functionNames, @NotNull GoType[] functionDeclTypes) {
        super(psiType);
        GoPsiTypeName[] typeNames = psiType.getTypeNames();
        embededTypes = new GoTypeNamed[typeNames.length];
        for (int i = 0; i < embededTypes.length; i++) {
            embededTypes[i] = (GoTypeNamed) typeNames[i].getType();
        }
        for (int i = 0; i < functionNames.length; i++) {
            methods.put(functionNames[i], functionDeclTypes[i]);
        }
    }

    @Override
    public boolean isIdentical(GoType other) {
        if (this == other) return true;
        if (other instanceof GoTypeInterface) {
            GoTypeInterface otherInterface = (GoTypeInterface) other;
            if (methods.size() != otherInterface.methods.size()) {
                return false;
            }
            for (String key : methods.keySet()) {
                if (otherInterface.methods.containsKey(key)) {
                    return false;
                }
                // TODO: Lower-case method names from different packages are always different
                if (!methods.get(key).isIdentical(otherInterface.methods.get(key))) {
                    return false;
                }
            }
        }
        return true;
    }

    @NotNull
    public GoTypeNamed[] getEmbededTypes() {
        return embededTypes;
    }

    @NotNull
    public HashMap<String, GoType> getMethods() {
        return methods;
    }
}
