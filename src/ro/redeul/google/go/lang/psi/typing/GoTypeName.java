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
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoTypeName extends GoTypePsiBacked<GoPsiTypeName> implements GoType {

    private static final Logger LOG = Logger.getInstance(GoTypeName.class);

    private final String name;
    private GoType definition;

    public GoTypeName(GoPsiTypeName type) {
        super(type);
        name = type.getName();

        assert !type.isPrimitive();
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return type == this || type instanceof GoTypeName && this.getPsiType().getIdentifier().getCanonicalName().equals(((GoTypeName) type).getPsiType().getIdentifier().getCanonicalName());
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
        if (definition == null) {
            GoTypeSpec spec = resolveTypeSpec(this.getPsiType());
            if (spec != null && spec.getType() != null) {
                definition = GoTypes.fromPsiType(spec.getType());
            }
        }
        return definition;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        if (type.isIdentical(this)) return true;
        if (type instanceof GoTypeName) return false;
        GoType resolvedType = GoTypeUtils.resolveToFinalType(this);
        if (resolvedType == null) return false;
        if (resolvedType instanceof GoTypeName) return false;
        return resolvedType.isAssignableFrom(type);
    }

    @NotNull
    @Override
    public GoType getUnderlyingType() {
        if (this.getDefinition() == null || this.getDefinition() == this) return this;
        return this.getDefinition().getUnderlyingType();
    }
}
