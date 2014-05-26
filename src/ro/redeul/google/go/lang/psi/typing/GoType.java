package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;

/**
 * // TODO: mtoader ! Please explain yourself.
 */
public interface GoType {

    GoType[] EMPTY_ARRAY = new GoType[0];

    static final GoType Unknown = new GoType() {

        @Override
        public boolean isIdentical(@NotNull GoType type) {
            return type == this;
        }

        @Override
        public void accept(Visitor visitor) {

        }

        @Override
        public boolean isAssignableFrom(@NotNull GoType type) {
            return true;
        }

        @NotNull
        @Override
        public String getText() {
            return "Undefined";
        }

        @NotNull
        @Override
        public String getNameLocalOrGlobal(GoFile currentFile) {
            return "Undefined";
        }

        @NotNull
        @Override
        public GoType getUnderlyingType() {
            return this;
        }
    };

    boolean isIdentical(@NotNull GoType type);

    void accept(Visitor visitor);

    boolean isAssignableFrom(@NotNull GoType type);

    @NotNull
    String getText();

    @NotNull
    String getNameLocalOrGlobal(@Nullable GoFile currentFile);

    @NotNull
    GoType getUnderlyingType();

    public class Visitor<T> {

        T data;

        public Visitor(T data) {
            this.data = data;
        }

        public T visit(GoType node) {
            node.accept(this);
            return data;
        }

        protected void setData(T data) {
            this.data = data;
        }

        protected void visitTypeArray(GoTypeArray array) {
        }

        public void visitTypeChannel(GoTypeChannel channel) {
        }

        public void visitTypeName(GoTypeName name) {
        }

        public void visitTypeSlice(GoTypeSlice slice) {
        }

        public void visitTypePointer(GoTypePointer pointer) {
        }

        public void visitTypeMap(GoTypeMap map) {
        }
    }
}
