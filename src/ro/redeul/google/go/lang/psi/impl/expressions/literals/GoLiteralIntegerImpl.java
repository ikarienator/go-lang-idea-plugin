package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralInteger;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

import java.math.BigInteger;

public class GoLiteralIntegerImpl extends GoPsiElementBase
        implements GoLiteralInteger {

    public GoLiteralIntegerImpl(@NotNull ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public BigInteger getValue() {
        int radix = 10;
        String textValue = getText();
        if (textValue.length() > 1) {
            if (textValue.startsWith("0x") || textValue.startsWith("0X")) {
                radix = 16;
                textValue = textValue.substring(2);
            } else if (textValue.startsWith("0")) {
                radix = 8;
                textValue = textValue.substring(1);
            }
        }

        return new BigInteger(textValue, radix);
    }

    @Override
    public Type getConstantType() {
        return Type.Int;
    }
}
