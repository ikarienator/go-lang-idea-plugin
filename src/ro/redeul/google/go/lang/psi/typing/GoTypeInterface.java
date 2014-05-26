package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeInterface;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;

public class GoTypeInterface
        extends GoTypePsiBacked<GoPsiTypeInterface>
        implements GoType {

    public GoTypeInterface(GoPsiTypeInterface psiType) {
        super(psiType);
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return type == this || type instanceof GoTypeInterface && this.getPsiType() == ((GoTypeInterface) type).getPsiType();
    }

    @Override
    public void accept(Visitor visitor) {
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        // TODO(ikarienator): implement assignability test.
        return super.isAssignableFrom(type);
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
            // FIXME
            return "interface{}";
        }
        return GoTypes.fromPsiType(typeNames[0]).getNameLocalOrGlobal(currentFile);
    }
}
