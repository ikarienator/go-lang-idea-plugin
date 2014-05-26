package ro.redeul.google.go.lang.psi.typing;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoImportDeclarations;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypePredeclared;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoTypeName extends GoTypePsiBacked<GoPsiTypeName, GoUnderlyingType> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);

    private final String name;
    private GoType definition;

    public GoTypeName(GoPsiTypeName type) {
        super(type);
        name = type.getName();

        setUnderlyingType(GoUnderlyingType.Undefined);

        if (type.isPrimitive()) {
            setUnderlyingType(GoUnderlyingTypePredeclared.getForName(name));
        } else {
            GoTypeSpec spec = resolveTypeSpec(type);
            if (spec != null && spec.getType() != null) {
                definition = GoTypes.fromPsiType(spec.getType());
                if (definition.getUnderlyingType() != null)
                    setUnderlyingType(definition.getUnderlyingType());
            }
        }
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return this == type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeName(this);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        if (currentFile == null) {
            return this.getName();
        }

        GoPsiTypeName type = this.getPsiType();
        StringBuilder stringBuilder = new StringBuilder();
        GoTypeSpec goTypeSpec = resolveTypeSpec(type);
        if (goTypeSpec == null)
            return this.getName();
        PsiDirectory containingDirectory = goTypeSpec.getContainingFile().getContainingDirectory();
        boolean isInSameDir = currentFile.getContainingDirectory().equals(containingDirectory);
        if (type.isPrimitive() || isInSameDir) {
            stringBuilder.append(type.getName());
        } else {
            FORLOOP:
            for (GoImportDeclarations declarations : currentFile.getImportDeclarations())
                for (GoImportDeclaration declaration : declarations.getDeclarations()) {
                    String canonicalPath = containingDirectory.getVirtualFile().getCanonicalPath();
                    GoLiteralString importPath = declaration.getImportPath();
                    if (importPath != null && canonicalPath != null && canonicalPath.endsWith(importPath.getValue())) {
                        String visiblePackageName = declaration.getVisiblePackageName();
                        if (visiblePackageName.equals(".")) {
                            stringBuilder.append(type.getName());
                        } else {
                            stringBuilder.append(visiblePackageName).append(".").append(type.getName());
                        }
                        break FORLOOP;
                    }
                }
        }
        return stringBuilder.toString();
    }

    public String getName() {
        return name;
    }

    public GoType getDefinition() {
        return definition;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        if (type == this) return true;
        if (type instanceof GoTypeName) return false;
        GoType resolvedType = GoTypeUtils.resolveToFinalType(this);
        if (resolvedType == null) return false;
        if (resolvedType instanceof GoTypeName) return false;
        return resolvedType.isAssignableFrom(type);
    }
}
