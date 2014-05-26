package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;

public class GoTypeMap extends GoTypePsiBacked<GoPsiTypeMap> implements GoType {

    private final GoType keyType;
    private final GoType elementType;

    public GoTypeMap(GoPsiTypeMap type) {
        super(type);

        keyType = GoTypes.fromPsiType(type.getKeyType());
        elementType = GoTypes.fromPsiType(type.getElementType());
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return type == this || type instanceof GoTypeMap && ((GoTypeMap) type).elementType.isIdentical(this.elementType) && ((GoTypeMap) type).keyType.isIdentical(this.keyType);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeMap(this);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        return String.format("map[%s]%s", keyType.getNameLocalOrGlobal(currentFile), elementType.getNameLocalOrGlobal(currentFile));
    }

    public GoType getKeyType() {
        return keyType;
    }

    public GoType getElementType() {
        return elementType;
    }
}
