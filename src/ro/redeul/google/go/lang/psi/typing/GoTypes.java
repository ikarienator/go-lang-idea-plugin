package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public class GoTypes {
    @NotNull
    public static GoType fromPsiType(GoPsiType type) {
        if (type == null) return GoType.Undefined;
        return type.getType();
    }

    @NotNull
    public static GoType[] fromPsiType(GoPsiType[] type) {
        if (type == null || type.length == 0) {
            return GoType.EMPTY_ARRAY;
        }
        GoType[] result = new GoType[type.length];
        for (int i = 0; i < type.length; i++) {
            result[i] = fromPsiType(type[i]);
        }
        return result;
    }


    public static <T extends GoType> T resolveTo(GoType type, Class<T> targetType) {
        while (type != null && type != GoType.Undefined && !targetType.isAssignableFrom(type.getClass())) {
            if (type instanceof GoTypeNamed) {
                type = ((GoTypeNamed) type).getDefinition();
            } else {
                type = GoType.Undefined;
            }
        }

        if (type == null || type == GoType.Undefined) {
            return null;
        }
        return targetType.cast(type);
    }

    public static GoTypeInterface resolveToInterface(GoType typeName) {
        return resolveTo(typeName, GoTypeInterface.class);
    }


    public static GoTypeStruct resolveToStruct(GoType type) {
        while (type != null && !(type instanceof GoTypeStruct)) {
            if (type instanceof GoTypePointer) {
                type = ((GoTypePointer) type).getTargetType();
            } else if (type instanceof GoTypeNamed) {
                type = ((GoTypeNamed) type).getDefinition();
            } else {
                type = null;
            }
        }

        if (type == null)
            return null;

        return (GoTypeStruct) type;
    }
}
