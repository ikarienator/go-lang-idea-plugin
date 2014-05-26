package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import java.util.HashMap;

public enum GoTypeBuiltin implements GoType {
    // Boolean
    Bool("bool"),

    // Interface
    Error("error"),

    // Integral
    Byte("byte"),
    Int("int"),
    Uint("uint"),
    Uint8("uint8"),
    Uint16("uint16"),
    Uint32("uint32"),
    Uint64("uint64"),
    Int8("int8"),
    Int16("int16"),
    Int32("int32"),
    Rune("rune"),
    Int64("int64"),

    // Floating
    Float32("float32"),
    Float64("float64"),
    Complex64("complex64"),
    Complex128("complex128"),

    // Pointer
    Uintptr("uintptr"),

    // String
    String("string");

    private final String text;
    private static final HashMap<String, GoTypeBuiltin> fromNameMap = new HashMap<java.lang.String, GoTypeBuiltin>();

    static {
        for (GoTypeBuiltin type : GoTypeBuiltin.values()) {
            fromNameMap.put(type.getText(), type);
        }
    }

    GoTypeBuiltin(String text) {
        this.text = text;
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return type == this;
    }

    @Override
    public void accept(Visitor visitor) {

    }

    @Nullable
    public static GoTypeBuiltin fromName(String name) {
        return fromNameMap.get(name);
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        return type == this;
    }

    public boolean isIntegral() {
        return Byte.ordinal() <= this.ordinal() && ordinal() <= Int64.ordinal();
    }

    public boolean isNumeric() {
        return Byte.ordinal() <= this.ordinal() && ordinal() <= Complex128.ordinal();
    }

    public boolean isAssignableFrom(GoExpr expr) {
        if (expr instanceof GoLiteralExpression) {
            GoLiteral literal = ((GoLiteralExpression) expr).getLiteral();
            if (literal instanceof GoLiteralIdentifier) {
                if (((GoLiteralIdentifier) literal).getUnqualifiedName().equals("nil")) {
                    return this == Error;
                }
                // Fallthrough.
            } else if (literal instanceof GoLiteralBool) {
                return this == Bool;
            } else if (literal instanceof GoLiteralChar) {
                return this.isIntegral();
            } else if (literal instanceof GoLiteralFloat) {
                return this.isNumeric();
            } else if (literal instanceof GoLiteralString) {
                return this == String;
            }
        }

        // TODO(ikarienator): test error interface
        GoType[] types = expr.getType();
        for (GoType type : types) {
            type = GoTypeUtils.resolveToFinalType(type);
            if (type instanceof GoTypeBuiltin) {
                if (type == this) {
                    return true;
                }
            } else if (type instanceof GoTypePointer) {
                if (this == Uintptr) {
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public String getText() {
        return this.text;
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        return this.text;
    }

    @NotNull
    @Override
    public GoType getUnderlyingType() {
        return this;
    }
}
