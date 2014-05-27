package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralChar;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedRune;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import java.math.BigInteger;

public class GoLiteralCharImpl extends GoPsiElementBase
        implements GoLiteralChar {
    private final GoType[] type;

    public GoLiteralCharImpl(@NotNull ASTNode node) {
        super(node);
        type = new GoType[]{new GoTypeUntypedRune(BigInteger.valueOf(this.getText().codePointAt(0)))};
    }

    @NotNull
    @Override
    public BigInteger getValue() {
        return BigInteger.valueOf(GoPsiUtils.getRuneValue(getText()));
    }

    @NotNull
    @Override
    public Type getLiteralType() {
        return Type.Char;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        return type;
    }


}
