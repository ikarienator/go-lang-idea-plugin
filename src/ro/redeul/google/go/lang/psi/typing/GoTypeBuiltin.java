package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.literals.*;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedBool;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedString;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import java.math.BigInteger;
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

    private static final HashMap<GoTypeBuiltin, BigInteger> MIN = new HashMap<GoTypeBuiltin, BigInteger>();
    private static final HashMap<GoTypeBuiltin, BigInteger> MAX = new HashMap<GoTypeBuiltin, BigInteger>();

    static {
        MIN.put(Byte, BigInteger.ZERO);
        MIN.put(Uint, BigInteger.ZERO);
        MIN.put(Uint8, BigInteger.ZERO);
        MIN.put(Uint16, BigInteger.ZERO);
        MIN.put(Uint32, BigInteger.ZERO);
        MIN.put(Uint64, BigInteger.ZERO);
        MIN.put(Int, BigInteger.valueOf(Integer.MIN_VALUE));
        MIN.put(Int8, BigInteger.valueOf(java.lang.Byte.MIN_VALUE));
        MIN.put(Int16, BigInteger.valueOf(Short.MIN_VALUE));
        MIN.put(Int32, BigInteger.valueOf(Integer.MIN_VALUE));
        MIN.put(Int64, BigInteger.valueOf(Long.MIN_VALUE));
        MIN.put(Float32, new BigInteger("-340282356779733661637539395458142568447"));
        MIN.put(Float64, new BigInteger("-179769313486231550856124328384506240234343437157459335924404872448581845754556114388470639943126220321960804027157371570809852884964511743044087662767600909594331927728237078876188760579532563768698654064825262115771015791463983014857704008123419459386245141723703148097529108423358883457665451722744025579519"));

        MAX.put(Byte, BigInteger.valueOf(255));
        MAX.put(Uint, new BigInteger("4294967295"));
        MAX.put(Uint8, BigInteger.valueOf(255));
        MAX.put(Uint16, BigInteger.valueOf(65535));
        MAX.put(Uint32, new BigInteger("4294967295"));
        MAX.put(Uint64, new BigInteger("18446744073709551615"));
        MAX.put(Int, BigInteger.valueOf(Integer.MAX_VALUE));
        MAX.put(Int8, BigInteger.valueOf(java.lang.Byte.MAX_VALUE));
        MAX.put(Int16, BigInteger.valueOf(java.lang.Short.MAX_VALUE));
        MAX.put(Int32, BigInteger.valueOf(java.lang.Integer.MAX_VALUE));
        MAX.put(Int64, BigInteger.valueOf(java.lang.Long.MAX_VALUE));
        MAX.put(Float32, new BigInteger("-340282356779733661637539395458142568447"));
        MAX.put(Float64, new BigInteger("179769313486231550856124328384506240234343437157459335924404872448581845754556114388470639943126220321960804027157371570809852884964511743044087662767600909594331927728237078876188760579532563768698654064825262115771015791463983014857704008123419459386245141723703148097529108423358883457665451722744025579519"));
    }


    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        type = type.getUnderlyingType();
        if (type == this) {
            return true;
        }
        if (type instanceof GoTypeUntyped) {
            GoTypeUntyped untyped = (GoTypeUntyped) type;
            switch (this) {
                case Bool:
                    return untyped instanceof GoTypeUntypedBool;
                case Error:
                    // TODO:
                    return false;
                case Byte:
                case Rune:
                case Int:
                case Uint:
                case Uint8:
                case Uint16:
                case Uint32:
                case Uint64:
                case Int8:
                case Int16:
                case Int32:
                case Int64:
                    return untyped.isIntegral() && untyped.inRange(MIN.get(this), MAX.get(this));
                case Float32:
                case Float64:
                    return untyped.isReal() && untyped.inRange(MIN.get(this), MAX.get(this));
                case Complex64:
                case Complex128:
                    return untyped.isNumeric();
                case Uintptr:
                    return false;
                case String:
                    return untyped instanceof GoTypeUntypedString;
            }
        }
        return false;
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
