package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.types.GoPsiType;

public enum GoTypeBuiltin implements GoType {
    Bool,
    Byte,
    Complex64,
    Complex128,
    Error,
    Float32,
    Float64,
    Int,
    Int8,
    Int16,
    Int32,
    Int64,
    Rune,
    String,
    uInt,
    uInt8,
    uInt16,
    uInt32,
    uInt64,
    uIntPtr;

    private GoTypeBuiltin() {

    }

    @Nullable
    public static GoType getForName(@NotNull String text) {
        if (text.equals("bool")) {
            return Bool;
        } else if (text.equals("byte")) {
            return Byte;
        } else if (text.equals("complex64")) {
            return Complex64;
        } else if (text.equals("complex128")) {
            return Complex128;
        } else if (text.equals("error")) {
            return Error;
        } else if (text.equals("float32")) {
            return Float32;
        } else if (text.equals("float64")) {
            return Float64;
        } else if (text.equals("int")) {
            return Int;
        } else if (text.equals("int8")) {
            return Int8;
        } else if (text.equals("int16")) {
            return Int16;
        } else if (text.equals("int32")) {
            return Int32;
        } else if (text.equals("int64")) {
            return Int64;
        } else if (text.equals("rune")) {
            return Rune;
        } else if (text.equals("string")) {
            return String;
        } else if (text.equals("uint")) {
            return uInt;
        } else if (text.equals("uint8")) {
            return uInt8;
        } else if (text.equals("uint16")) {
            return uInt16;
        } else if (text.equals("uint32")) {
            return uInt32;
        } else if (text.equals("uint64")) {
            return uInt64;
        } else if (text.equals("uintptr")) {
            return uIntPtr;
        } else {
            return null;
        }
    }

    @Override
    public boolean isIdentical(GoType other) {
        return this == other;
    }

    @Nullable
    @Override
    public GoPsiType getPsiType() {
        return null;
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoExpr x) {
        GoType[] types = x.getType();
        if (types.length == 0) return false;
        if (isIdentical(types[0])) return true;
        GoType resolvedType = types[0];
        if (resolvedType instanceof GoTypeNamed) {
            resolvedType = ((GoTypeNamed) resolvedType).getDefinition();
        }
        if (resolvedType instanceof GoTypeUntyped) {
            Object value = ((GoTypeUntyped) resolvedType).getValue();
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
