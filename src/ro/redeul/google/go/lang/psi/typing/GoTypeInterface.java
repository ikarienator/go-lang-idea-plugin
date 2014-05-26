package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class GoTypeInterface
        extends GoTypePsiBacked<GoPsiTypeInterface>
        implements GoType {

    @NotNull
    private final HashMap<String, GoTypeFunction> methods = new HashMap<String, GoTypeFunction>();

    @NotNull
    private final HashSet<GoTypeName> embededTypes = new HashSet<GoTypeName>();

    private HashMap<String, GoTypeFunction> allMethods;

    public GoTypeInterface(GoPsiTypeInterface psiType) {
        super(psiType);
        for (GoFunctionDeclaration decl : psiType.getFunctionDeclarations()) {
            methods.put(decl.getFunctionName(), (GoTypeFunction) GoTypes.fromPsiType(decl));
        }


        GoPsiTypeName[] embededPsiTypes = psiType.getTypeNames();
        int i = 0;
        for (GoPsiTypeName typeName : embededPsiTypes) {
            this.embededTypes.add((GoTypeName) GoTypes.fromPsiType(typeName));
        }
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        if (type == this) return true;
        if (!(type instanceof GoTypeInterface)) return false;

        GoTypeInterface interfaceType = (GoTypeInterface) type;
        if (this.methods.size() != interfaceType.methods.size()) return false;
        if (this.embededTypes.size() != interfaceType.embededTypes.size()) return false;

        for (String name : this.methods.keySet()) {
            if (!interfaceType.methods.containsKey(name)) return false;
            if (!this.methods.get(name).isIdentical(interfaceType.methods.get(name))) return false;
        }

        for (GoTypeName embededType : this.embededTypes) {
            if (!interfaceType.embededTypes.contains(embededType)) return false;
        }
        return true;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeInterface(this);
    }

    @NotNull
    public HashMap<String, GoTypeFunction> getMethods() {
        return methods;
    }

    @NotNull
    public HashSet<GoTypeName> getEmbededTypes() {
        return embededTypes;
    }

    @NotNull
    public HashMap<String, GoTypeFunction> getAllMethods() {
        if (allMethods == null) {
            allMethods = resolveAllMethods();
        }
        return allMethods;
    }

    @NotNull
    private HashMap<String, GoTypeFunction> resolveAllMethods() {
        HashMap<String, GoTypeFunction> result = new HashMap<String, GoTypeFunction>();
        HashSet<GoTypeInterface> addedInterfaces = new HashSet<GoTypeInterface>();
        Queue<GoTypeInterface> resolutionQueue = new LinkedList<GoTypeInterface>();
        resolutionQueue.add(this);
        while (!resolutionQueue.isEmpty()) {
            GoTypeInterface curr = resolutionQueue.poll();
            if (addedInterfaces.contains(curr)) {
                continue;
            }
            addedInterfaces.add(curr);
            result.putAll(curr.getMethods());
            for (GoTypeName embededType : embededTypes) {
                GoType resolvedType = embededType.getUnderlyingType();
                if (resolvedType instanceof GoTypeInterface) {
                    if (addedInterfaces.contains(resolvedType)) {
                        continue;
                    }
                    resolutionQueue.add((GoTypeInterface) resolvedType);
                }
            }
        }
        return result;
    }


    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        if (this.isIdentical(type)) return true;
        type = type.getUnderlyingType();
        if (type instanceof GoTypeInterface) {
            HashMap<String, GoTypeFunction> myMethods = this.getAllMethods();
            HashMap<String, GoTypeFunction> allMethods = ((GoTypeInterface) type).getAllMethods();
            for (String key : myMethods.keySet()) {
                if (!allMethods.containsKey(key)) return false;
                if (!allMethods.get(key).isIdentical(myMethods.get(key))) return false;
            }
            return true;
        }
        // TODO(ikarienator): Match other types.
        return false;
    }

    @NotNull
    @Override
    public String getText() {
        return getNameLocalOrGlobal(null);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        GoPsiTypeInterface psiType = this.getPsiType();
        GoPsiTypeName[] typeNames = psiType.getTypeNames();
        if (typeNames.length == 0) {
            StringBuilder sb = new StringBuilder("interface{");
            boolean first = true;
            for (String name : this.methods.keySet()) {
                if (first) {
                    first = false;
                } else {
                    sb.append("; ");
                }
                sb.append(name);
                sb.append(this.methods.get(name).getNameLocalOrGlobal(currentFile));
            }
            for (GoTypeName name : this.embededTypes) {
                if (first) {
                    first = false;
                } else {
                    sb.append("; ");
                }
                sb.append(name.getNameLocalOrGlobal(currentFile));
            }
            sb.append("}");
            return sb.toString();
        }
        return GoTypes.fromPsiType(typeNames[0]).getNameLocalOrGlobal(currentFile);
    }
}
