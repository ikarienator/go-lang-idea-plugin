package ro.redeul.google.go.lang.psi.expressions.primary;

import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;

public interface GoIdentifierExpression extends GoPrimaryExpression {

    String getName();

    GoIdentifier getIdentifier();

    boolean isBlank();

    boolean isIota();

    Integer getIotaValue();

    public void setIotaValue(int value);
}
