package ro.redeul.google.go.lang.psi.expressions;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Sep 4, 2010
 * Time: 10:42:42 PM
 */
public interface GoIdentifier extends PsiNameIdentifierOwner, GoPsiElement {
    GoIdentifier[] EMPTY_ARRAY = new GoIdentifier[0];

    boolean isBlank();

    boolean isQualified();

    String getUnqualifiedName();

    @Nullable
    String getLocalPackageName();

    @NotNull
    String getCanonicalName();
}
