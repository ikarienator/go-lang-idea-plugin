package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoTypeNamed extends GoTypeBase<GoPsiTypeName> {
    private GoType definition;

    public GoTypeNamed(@Nullable GoPsiTypeName psiType) {
        super(psiType);
    }

    @NotNull
    public String getName() {
        assert psiType != null;
        GoIdentifier identifier = psiType.getIdentifier();
        return identifier.getText();
    }

    @Override
    public boolean isIdentical(GoType other) {
        return other == this;
    }

    @NotNull
    public GoType getDefinition() {
        if (definition == null) {
            GoTypeSpec spec = resolveTypeSpec(psiType);
            if (spec != null && spec.getType() != null) {
                definition = spec.getType().getType();
            } else {
                definition = GoType.Undefined;
            }
        }
        return definition;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoExpr x) {
        if (psiType == null) {
            return false;
        }
        GoType[] xTypes = x.getType();
        if (xTypes.length == 0) {
            return false;
        }
        if (xTypes[0] instanceof GoTypeNamed) {
            return ((GoTypeNamed) xTypes[0]).psiType == this.psiType;
        }
        return getDefinition().isAssignableFrom(x);
    }
}
