package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralImaginary;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.untyped.Complex;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedComplex;
import ro.redeul.google.go.lang.psi.typing.untyped.Rational;

import java.math.BigDecimal;

import static ro.redeul.google.go.lang.lexer.GoTokenTypes.litFLOAT_I;

public class GoLiteralImaginaryImpl extends GoPsiElementBase implements GoLiteralImaginary {
    private final GoType[] type;

    public GoLiteralImaginaryImpl(@NotNull ASTNode node) {
        super(node);
        type = new GoType[]{new GoTypeUntypedComplex(getValue())};
    }

    @NotNull
    @Override
    public Complex getValue() {
        return new Complex(Rational.ZERO, new Rational(new BigDecimal(this.getText().substring(0, this.getText().length() - 1))));
    }

    @Override
    @NotNull
    public Type getLiteralType() {
        return findChildByType(litFLOAT_I) != null
                ? Type.ImaginaryFloat : Type.ImaginaryInt;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        return type;
    }
}
