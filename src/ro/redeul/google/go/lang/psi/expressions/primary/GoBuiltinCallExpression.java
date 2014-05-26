package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 6/2/11
 * Time: 3:57 AM
 */
public interface GoBuiltinCallExpression extends GoCallOrConvExpression {

    public GoType[] getArgumentsType();
}
