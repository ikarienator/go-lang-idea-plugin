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

import java.math.BigDecimal;
import java.math.BigInteger;

public class Rational {
    public static final Rational ZERO = new Rational(BigInteger.ZERO);

    // Denominator is always positive and gcd(numerator, denominator) == 1.
    private final BigInteger numerator, denominator;

    public Rational(BigDecimal decimal) {
        if (decimal.scale() < 0) {
            numerator = decimal.unscaledValue().multiply(BigInteger.TEN.pow(-decimal.scale()));
            denominator = BigInteger.ONE;
        } else {
            BigInteger numerator = decimal.unscaledValue();
            BigInteger denominator = BigInteger.TEN.pow(decimal.scale());
            BigInteger gcd = numerator.gcd(denominator);
            this.numerator = numerator.divide(gcd);
            this.denominator = denominator.divide(gcd);
        }
    }

    public Rational(BigInteger numerator) {
        this(numerator, BigInteger.ONE);
    }

    public Rational(BigInteger numerator, BigInteger denominator) {
        if (denominator.compareTo(BigInteger.ZERO) < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
        BigInteger gcd = numerator.gcd(denominator);
        this.numerator = numerator.divide(gcd);
        this.denominator = denominator.divide(gcd);
    }

    public BigDecimal toDecimal() {
        return new BigDecimal(numerator).divide(new BigDecimal(denominator));
    }

    public Rational add(Rational op2) {
        return new Rational(this.numerator.multiply(op2.denominator).add(op2.numerator.multiply(this.denominator)), this.denominator.multiply(op2.denominator));
    }

    public Rational subtract(Rational op2) {
        return new Rational(this.numerator.multiply(op2.denominator).subtract(op2.numerator.multiply(this.denominator)), this.denominator.multiply(op2.denominator));
    }

    public Rational multiply(Rational op2) {
        return new Rational(this.numerator.multiply(op2.numerator), this.denominator.multiply(op2.denominator));
    }

    public Rational divide(Rational op2) {
        return new Rational(this.numerator.multiply(op2.denominator), this.denominator.multiply(op2.numerator));
    }

    public int compareTo(Rational op2) {
        return this.numerator.multiply(op2.denominator).compareTo(op2.numerator.multiply(this.denominator));
    }

    public boolean isIntegral() {
        return denominator.compareTo(BigInteger.ONE) == 0;
    }
}
