package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeSlice;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeSlice;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 2, 2010
 * Time: 12:50:44 PM
 */
public class GoPsiTypeSliceImpl extends GoPsiPackagedElementBase implements
                                                              GoPsiTypeSlice {
    public GoPsiTypeSliceImpl(@NotNull ASTNode node) {
        super(node);
    }

    public GoPsiType getElementType() {
        return findChildByClass(GoPsiType.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitSliceType(this);
    }

    @Override
    public GoType resolveType() {
        return new GoTypeSlice(this, getElementType().getType());
    }

    @NotNull
    @Override
    public String getPresentationTailText() {
        return String.format("[]%s", getElementType().getPresentationTailText());
    }
}
