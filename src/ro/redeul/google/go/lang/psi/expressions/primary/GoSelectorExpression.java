package ro.redeul.google.go.lang.psi.expressions.primary;

import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/19/11
 * Time: 10:56 PM
 */
public interface GoSelectorExpression extends GoPrimaryExpression {

    GoPrimaryExpression getBaseExpression();

    @Nullable
    GoIdentifier getIdentifier();
}
