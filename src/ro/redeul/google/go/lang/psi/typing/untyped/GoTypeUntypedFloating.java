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

package ro.redeul.google.go.lang.psi.typing.untyped;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;
import ro.redeul.google.go.lang.psi.typing.GoTypeUntyped;

import java.math.BigInteger;

public final class GoTypeUntypedFloating extends GoTypeUntyped<Rational> {
    public GoTypeUntypedFloating(Rational value) {
        super(value);
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return this == type || type instanceof GoTypeUntypedFloating;
    }

    @Override
    public Kind getKind() {
        return Kind.Floating;
    }

    @Override
    public boolean isIntegral() {
        return value.isIntegral();
    }

    @Override
    public boolean isReal() {
        return true;
    }

    @Override
    public boolean inRange(@NotNull BigInteger min, @NotNull BigInteger max) {
        return value.compareTo(new Rational(min)) >= 0 && value.compareTo(new Rational(max)) <= 0;
    }

    @Override
    public GoType getTyped() {
        return GoTypeBuiltin.Float64;
    }

    @Nullable
    @Override
    protected GoTypeUntyped elevateTo(Kind kind) {
        switch (kind) {
            case Floating:
                return this;
            case Complex:
                return new GoTypeUntypedComplex(new Complex(value, Rational.ZERO));
        }
        return null;
    }
}
