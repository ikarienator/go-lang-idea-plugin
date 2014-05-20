package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeChannel;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 1:22:29 PM
 */
public class GoPsiTypeChannelImpl extends GoPsiPackagedElementBase implements GoPsiTypeChannel {

    private final GoTypeChannel.Direction direction;

    public GoPsiTypeChannelImpl(@NotNull ASTNode node, GoTypeChannel.Direction direction) {
        super(node);
        this.direction = direction;
    }

    public GoTypeChannel.Direction getDirection() {
        return direction;
    }

    public GoPsiType getElementType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitChannelType(this);
    }

    @Override
    public GoType resolveType() {
        return GoType.Undefined;
    }

    @Override
    public String getPresentationTailText() {
        return GoTypeChannel.Direction.getText(getDirection()) + getElementType().getPresentationTailText();    //To change body of overridden methods use File | Settings | File Templates.
    }
}
