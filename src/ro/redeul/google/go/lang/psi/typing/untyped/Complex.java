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

public class Complex {
    public final Rational real, imag;

    public Complex(Rational real, Rational imag) {
        this.real = real;
        this.imag = imag;
    }


    public Complex add(Complex op2) {
        return new Complex(this.real.add(op2.real), this.imag.add(op2.imag));
    }

    public Complex subtract(Complex op2) {
        return new Complex(this.real.subtract(op2.real), this.imag.subtract(op2.imag));
    }

    public Complex multiply(Complex op2) {
        return new Complex(this.real.multiply(op2.real).subtract(this.imag.multiply(op2.imag)), this.real.multiply(op2.imag).add(this.imag.multiply(op2.real)));
    }

    public Complex divide(Complex op2) {
        Rational denominator = op2.imag.multiply(op2.imag).add(op2.real.multiply(op2.real));
        return new Complex(this.real.multiply(op2.real).add(this.imag.multiply(op2.imag)).divide(denominator), this.imag.multiply(op2.real).subtract(this.real.multiply(op2.imag)).divide(denominator));
    }
}
