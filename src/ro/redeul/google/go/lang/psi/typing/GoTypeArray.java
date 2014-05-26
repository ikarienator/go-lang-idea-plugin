package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypeArray;
import ro.redeul.google.go.lang.psi.types.underlying.GoUnderlyingTypes;

public class GoTypeArray extends GoAbstractType<GoUnderlyingTypeArray> implements GoType {

    private final GoType elementType;
    private GoPsiType elementPsiType = null;
    private final int length;

    public GoTypeArray(GoPsiTypeArray type) {
        this(type.getArrayLength(), GoTypes.fromPsiType(type.getElementType()));
        elementPsiType = type;
    }

    public GoTypeArray(int length, GoType elementType) {
        this.length = length;
        this.elementType = elementType;
        setUnderlyingType(GoUnderlyingTypes.getArray(elementType.getUnderlyingType(), 1));
    }

    public GoPsiType getPsiType() {
        return elementPsiType;
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        if (type == this) return true;
        if (!(type instanceof GoTypeArray)) {
            return false;
        }

        GoTypeArray otherArray = (GoTypeArray) type;

        return this.length == ((GoTypeArray) type).length && elementType.isIdentical(otherArray.getElementType());
    }

    public int getLength() {
        return length;
    }

    public GoType getElementType() {
        return elementType;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeArray(this);
    }

    @NotNull
    @Override
    public String getText() {
        return String.format("[%s]%s", length < 0 ? "...": Integer.toString(length), elementType.getText());
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(GoFile currentFile) {
        return String.format("[%s]%s", length < 0 ? "...": Integer.toString(length), elementType.getNameLocalOrGlobal(currentFile));
    }
}
