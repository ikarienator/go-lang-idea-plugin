package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeChannel;
public class GoTypeChannel extends GoTypePsiBacked<GoPsiTypeChannel> implements GoType {

    private final GoType elementType;
    private final ChannelType channelType;

    public GoTypeChannel(GoPsiTypeChannel psiType) {
        super(psiType);

        elementType = GoTypes.fromPsiType(psiType.getElementType());
        channelType = psiType.getChannelType();
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        return type == this || type instanceof GoTypeChannel && (((GoTypeChannel) type).getChannelType() == this.getChannelType() && ((GoTypeChannel) type).elementType.isIdentical(this.elementType));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeChannel(this);
    }

    @Override
    public boolean isAssignableFrom(@NotNull GoType type) {
        return type instanceof GoTypeChannel && (((GoTypeChannel) type).channelType == ChannelType.Bidirectional || ((GoTypeChannel) type).channelType == this.channelType) && ((GoTypeChannel) type).elementType.isIdentical(this.elementType);
    }

    @NotNull
    @Override
    public String getText() {
        return String.format("%s %s", this.getChannelType().toString(), this.elementType.getText());
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(GoFile currentFile) {
        return String.format("%s %s", this.getChannelType().toString(), this.elementType.getNameLocalOrGlobal(currentFile));
    }

    public GoType getElementType() {
        return elementType;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public static enum ChannelType {
        Bidirectional,
        Sending,
        Receiving;

        public static String getText(ChannelType channelType) {
            switch (channelType) {
                case Bidirectional:
                    return "chan";
                case Sending:
                    return "chan<-";
                case Receiving:
                    return "<-chan";
            }

            return "";
        }
    }
}
