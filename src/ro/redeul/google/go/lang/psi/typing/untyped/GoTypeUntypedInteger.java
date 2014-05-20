package ro.redeul.google.go.lang.psi.typing.untyped;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;
import ro.redeul.google.go.lang.psi.typing.GoTypeUntyped;

import java.math.BigInteger;

public class GoTypeUntypedInteger extends GoTypeUntyped<BigInteger> {

    public GoTypeUntypedInteger(@NotNull BigInteger value) {
        super(value);
    }

    @Override
    public boolean isIdentical(GoType other) {
        return other == this;
    }

    public static final BigInteger MAX_UINT8 = new BigInteger("255");
    public static final BigInteger MAX_UINT16 = new BigInteger("65535");
    public static final BigInteger MAX_UINT32 = new BigInteger("4294967295");
    public static final BigInteger MAX_UINT64 = new BigInteger("18446744073709551615");

    public static final BigInteger MAX_INT8 = new BigInteger("127");
    public static final BigInteger MAX_INT16 = new BigInteger("32767");
    public static final BigInteger MAX_INT32 = new BigInteger("2147483647");
    public static final BigInteger MAX_INT64 = new BigInteger("9223372036854775807");

    public static final BigInteger MIN_INT8 = new BigInteger("-128");
    public static final BigInteger MIN_INT16 = new BigInteger("-32768");
    public static final BigInteger MIN_INT32 = new BigInteger("-2147483648");
    public static final BigInteger MIN_INT64 = new BigInteger("-9223372036854775808");

    // (1 - 2^-25 - 2^-54) * 2^(2^7) - 1
    public static final BigInteger MAX_FLOAT32 = new BigInteger("340282356779733642748073463979561713663");
    public static final BigInteger MIN_FLOAT32 = new BigInteger("-340282356779733642748073463979561713663");

    // (1 - 2^-54) * 2^(2^10) - 1
    public static final BigInteger MAX_FLOAT64 = new BigInteger("179769313486231580793728971405303415079934132710037826936173778980444968292764750946649017977587207096330286416692887910946555547851940402630657488671505820681908902000708383676273854845817711531764475730270069855571366959622842914819860834936475292719074168444365510704342711559699508093042880177904174497791");
    public static final BigInteger MIN_FLOAT64 = new BigInteger("-179769313486231580793728971405303415079934132710037826936173778980444968292764750946649017977587207096330286416692887910946555547851940402630657488671505820681908902000708383676273854845817711531764475730270069855571366959622842914819860834936475292719074168444365510704342711559699508093042880177904174497791");


    public boolean isAssignableToBuiltin(GoTypeBuiltin builtin) {
        switch (builtin) {
            case Bool:
            case Error:
            case String:
                return false;
            case Byte:
            case uInt8:
                if (value.compareTo(BigInteger.ZERO) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_UINT8) > 0) {
                    return false;
                }
                break;
            case uInt16:
                if (value.compareTo(BigInteger.ZERO) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_UINT16) > 0) {
                    return false;
                }
                break;
            case uInt32:
            case uInt:
                if (value.compareTo(BigInteger.ZERO) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_UINT32) > 0) {
                    return false;
                }
                break;
            case uInt64:
                if (value.compareTo(BigInteger.ZERO) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_UINT64) > 0) {
                    return false;
                }
                break;
            case uIntPtr:
                if (value.compareTo(BigInteger.ZERO) < 0) {
                    return false;
                }
                break;
            case Int8:
                if (value.compareTo(MIN_INT8) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_INT8) > 0) {
                    return false;
                }
                break;
            case Int16:
                if (value.compareTo(MIN_INT16) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_INT16) > 0) {
                    return false;
                }
                break;
            case Int:
            case Rune:
            case Int32:
                if (value.compareTo(MIN_INT32) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_INT32) > 0) {
                    return false;
                }
                break;
            case Int64:
                if (value.compareTo(MIN_INT64) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_INT64) > 0) {
                    return false;
                }
                break;
            case Float32:
            case Complex64:
                if (value.compareTo(MIN_FLOAT32) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_FLOAT32) > 0) {
                    return false;
                }
                break;
            case Float64:
            case Complex128:
                if (value.compareTo(MIN_FLOAT64) < 0) {
                    return false;
                }
                if (value.compareTo(MAX_FLOAT64) > 0) {
                    return false;
                }
                break;
        }
        return false;
    }
}
