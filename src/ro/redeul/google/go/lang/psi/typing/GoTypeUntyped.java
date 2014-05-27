/* Copyright (c) 2014 Shape Security, Inc. This source code and/or documentation is the confidential and copyrighted
 * property of Shape Security, Inc. All rights are reserved to Shape Security, Inc. The reproduction, preparation of
 * derivative works, distribution, public performance or public display of the source code and/or documentation is
 * not authorized unless expressly licensed.
 * Please contact
 * 		Shape Security, Inc., Attn: Licensing Department,
 * 		P.O. Box 772, Palo Alto, CA 94302
 * 		licensing@shapesecurity.com
 * 		650-399-0400
 * for information regarding commercial and/or trial license availability.
 */

package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedString;

import java.math.BigInteger;

public abstract class GoTypeUntyped<Value> implements GoType {

    public enum Kind {
        Bool,
        Integer,
        Rune,
        Floating,
        Complex,
        String
    }

    private final static Kind[] ELEVATION_TYPES = new Kind[]{Kind.Integer, Kind.Rune, Kind.Floating, Kind.Complex};

    protected final Value value;

    public GoTypeUntyped(Value value) {
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeUntyped(this);
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        return false;
    }

    @NotNull
    @Override
    public String getText() {
        return value.toString();
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        return value.toString();
    }

    @NotNull
    @Override
    public GoType getUnderlyingType() {
        return this;
    }

    public Value getValue() {
        return value;
    }

    public abstract Kind getKind();

    public boolean isNumeric() {
        Kind kind = this.getKind();
        return kind == Kind.Rune || kind == Kind.Integer || kind == Kind.Floating || kind == Kind.Complex;
    }

    public abstract boolean isIntegral();

    public abstract boolean isReal();

    public abstract boolean inRange(@NotNull BigInteger min, @NotNull BigInteger max);

    public abstract GoType getTyped();

    @Nullable
    protected abstract GoTypeUntyped elevateTo(Kind kind);

    /**
     * Elevate two untyped constant for binary operation.
     * Returns null if failed.
     */
    @Nullable
    public static GoTypeUntyped[] elevateForBinrayOpration(@NotNull GoTypeUntyped leftOp, @NotNull GoTypeUntyped rightOp) {
        if (leftOp instanceof GoTypeUntypedString && rightOp instanceof GoTypeUntypedString) {
            return new GoTypeUntyped[]{leftOp, rightOp};
        }

        int leftOpKind = 0, rightOpKind = 0;
        for (Kind k : ELEVATION_TYPES) {
            if (leftOp.getKind() == k) {
                break;
            }
            leftOpKind++;
        }
        if (leftOpKind == ELEVATION_TYPES.length) return null;
        for (Kind k : ELEVATION_TYPES) {
            if (rightOp.getKind() == k) {
                break;
            }
            rightOpKind++;
        }
        if (rightOpKind == ELEVATION_TYPES.length) return null;
        Kind elevationType = ELEVATION_TYPES[Math.max(leftOpKind, rightOpKind)];
        GoType elevatedLeft = leftOp.elevateTo(elevationType);
        if (elevatedLeft == null) return null;
        GoType elevatedRight = leftOp.elevateTo(elevationType);
        if (elevatedRight == null) return null;
        return new GoTypeUntyped[]{leftOp.elevateTo(elevationType), rightOp.elevateTo(elevationType)};
    }
}
