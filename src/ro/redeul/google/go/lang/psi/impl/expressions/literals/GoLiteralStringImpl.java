package ro.redeul.google.go.lang.psi.impl.expressions.literals;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.untyped.GoTypeUntypedString;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public class GoLiteralStringImpl extends GoPsiElementBase
    implements GoLiteralString
{
    private final GoType[] type;
    public GoLiteralStringImpl(@NotNull ASTNode node) {
        super(node);
        type = new GoType[] { new GoTypeUntypedString(this.getValue()) };
    }

    @Override
    @NotNull
    public String getValue() {
        return GoPsiUtils.getStringLiteralValue(getText());
    }

    @NotNull
    @Override
    public Type getLiteralType() {
        return getText().startsWith("`")
            ? Type.RawString : Type.InterpretedString;
    }

    @NotNull
    @Override
    public GoType[] getType() {
        return type;
    }
}
