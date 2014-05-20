package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeMap;

public class GoTypeMap extends GoTypeBase<GoPsiTypeMap> {

    @NotNull
    private final GoType keyType;

    @NotNull
    private final GoType valueType;

    public GoTypeMap(@Nullable GoPsiTypeMap map, @NotNull GoType keyType, @NotNull GoType valueType) {
        super(map);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public boolean isIdentical(GoType other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @NotNull
    public GoType getKeyType() {
        return keyType;
    }

    @NotNull
    public GoType getValueType() {
        return valueType;
    }
}
