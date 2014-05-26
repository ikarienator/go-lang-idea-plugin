/*
* Copyright 2012 Midokura Europe SARL
*/
package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingType;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public abstract class GoAbstractType<
        UnderlyingType extends GoUnderlyingType
        > implements GoType {

    private UnderlyingType underlyingType;

    @Override
    public GoUnderlyingType getUnderlyingType() {
        return
                underlyingType != null
                        ? underlyingType
                        : GoUnderlyingType.Undefined;
    }

    void setUnderlyingType(UnderlyingType underlyingType) {
        this.underlyingType = underlyingType;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        if (type == this) {
            return true;
        }
        if (type instanceof GoTypeName) {
            GoType resolvedType = GoTypeUtils.resolveToFinalType(type);
            if (resolvedType == null)
                return false;
            type = resolvedType;
        }
        if (type instanceof GoTypeArray) {
            if (this.isIdentical(type)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    @Override
    public String getText() {
        return this.getNameLocalOrGlobal(null);
    }
}
