package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFloat;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedFloating;
import ro.redeul.google.go.lang.psi.typing.untyped.Rational;

import java.math.BigDecimal;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 6/2/11
 * Time: 3:44 AM
 */
public class GoLiteralFloatImpl extends GoPsiElementBase
    implements GoLiteralFloat {

    private final GoType[] type;

    public GoLiteralFloatImpl(@NotNull ASTNode node) {
        super(node);
        type = new GoType[]{new GoTypeUntypedFloating(this.getValue())};
    }

    @NotNull
    @Override
    public Rational getValue() {
        return new Rational(new BigDecimal(this.getText()));
    }

    @NotNull
    @Override
    public Type getLiteralType() {
        return Type.Float;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        return type;
    }
}
