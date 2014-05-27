/* Copyright (c) 2014 Shape Security, Inc. This source code and/or documentation is the confidential and copyrighted
 * property of Shape Security, Inc. All rights are reserved to Shape Security, Inc. The reproduction, preparation of
 * derivative works, distribution, public performance or public display of the source code and/or documentation is
 * not authorized unless expressly licensed.
 * Please contact
 * 		Shape Security, Inc., Attn: Licensing Department,
 * 		P.O. Box 772, Palo Alto, CA 94302
 * 		licensing@shapesecurity.com
 * 		650-399-0400
 * for information regarding commercial and/or trial license availability.
 */

package ro.redeul.google.go.lang.psi.typing.untyped;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.typing.GoTypeUntyped;

/**
 * Calculate the type of two untyped constants under binary operation.
 */
public class Calculator implements GoTokenTypes {
    @Nullable
    public static GoTypeUntyped operate(IElementType operator, GoTypeUntyped leftType, GoTypeUntyped rightType) {
        if (operator == oPLUS) {
            return add(leftType, rightType);
        } else if (operator == oMINUS) {
            return subtract(leftType, rightType);
        } else if (operator == oMUL) {
            return multiply(leftType, rightType);
        } else if (operator == oQUOTIENT) {
            return divide(leftType, rightType);
        } else if (operator == oREMAINDER) {
            return modular(leftType, rightType);
        } else if (operator == oBIT_OR) {
            return bitOr(leftType, rightType);
        } else if (operator == oBIT_XOR) {
            return bitXor(leftType, rightType);
        } else if (operator == oBIT_AND) {
            return bitAnd(leftType, rightType);
        } else if (operator == oBIT_CLEAR) {
            return bitClear(leftType, rightType);
        } else if (operator == oCOND_OR) {
            return or(leftType, rightType);
        } else if (operator == oCOND_AND) {
            return and(leftType, rightType);
        } else if (operator == oSHIFT_LEFT) {
            return shiftLeft(leftType, rightType);
        } else if (operator == oSHIFT_RIGHT) {
            return shiftRight(leftType, rightType);
        } else if (operator == oEQ) {
            return eq(leftType, rightType);
        } else if (operator == oNOT_EQ) {
            return neq(leftType, rightType);
        } else if (operator == oLESS) {
            return lt(leftType, rightType);
        } else if (operator == oLESS_OR_EQUAL) {
            return lte(leftType, rightType);
        } else if (operator == oGREATER) {
            return gt(leftType, rightType);
        } else if (operator == oGREATER_OR_EQUAL) {
            return gte(leftType, rightType);
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped add(GoTypeUntyped a, GoTypeUntyped b) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(a, b);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().add(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().add(((GoTypeUntypedRune) right).getValue()));
        }
        if (left instanceof GoTypeUntypedFloating) {
            return new GoTypeUntypedFloating(((GoTypeUntypedFloating) left).getValue().add(((GoTypeUntypedFloating) right).getValue()));
        }
        if (left instanceof GoTypeUntypedComplex) {
            return new GoTypeUntypedComplex(((GoTypeUntypedComplex) left).getValue().add(((GoTypeUntypedComplex) right).getValue()));
        }
        if (left instanceof GoTypeUntypedString) {
            return new GoTypeUntypedString(((GoTypeUntypedString) left).getValue() + ((GoTypeUntypedString) right).getValue());
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped subtract(GoTypeUntyped a, GoTypeUntyped b) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(a, b);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().subtract(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().subtract(((GoTypeUntypedRune) right).getValue()));
        }
        if (left instanceof GoTypeUntypedFloating) {
            return new GoTypeUntypedFloating(((GoTypeUntypedFloating) left).getValue().subtract(((GoTypeUntypedFloating) right).getValue()));
        }
        if (left instanceof GoTypeUntypedComplex) {
            return new GoTypeUntypedComplex(((GoTypeUntypedComplex) left).getValue().subtract(((GoTypeUntypedComplex) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped multiply(GoTypeUntyped a, GoTypeUntyped b) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(a, b);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().multiply(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().multiply(((GoTypeUntypedRune) right).getValue()));
        }
        if (left instanceof GoTypeUntypedFloating) {
            return new GoTypeUntypedFloating(((GoTypeUntypedFloating) left).getValue().multiply(((GoTypeUntypedFloating) right).getValue()));
        }
        if (left instanceof GoTypeUntypedComplex) {
            return new GoTypeUntypedComplex(((GoTypeUntypedComplex) left).getValue().multiply(((GoTypeUntypedComplex) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped divide(GoTypeUntyped a, GoTypeUntyped b) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(a, b);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().divide(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().divide(((GoTypeUntypedRune) right).getValue()));
        }
        if (left instanceof GoTypeUntypedFloating) {
            return new GoTypeUntypedFloating(((GoTypeUntypedFloating) left).getValue().divide(((GoTypeUntypedFloating) right).getValue()));
        }
        if (left instanceof GoTypeUntypedComplex) {
            return new GoTypeUntypedComplex(((GoTypeUntypedComplex) left).getValue().divide(((GoTypeUntypedComplex) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped modular(GoTypeUntyped a, GoTypeUntyped b) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(a, b);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().mod(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().mod(((GoTypeUntypedRune) right).getValue()));
        }
        if (left instanceof GoTypeUntypedFloating) {
            return new GoTypeUntypedFloating(((GoTypeUntypedFloating) left).getValue().divide(((GoTypeUntypedFloating) right).getValue()));
        }
        if (left instanceof GoTypeUntypedComplex) {
            return new GoTypeUntypedComplex(((GoTypeUntypedComplex) left).getValue().divide(((GoTypeUntypedComplex) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped bitOr(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(leftType, rightType);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().or(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().or(((GoTypeUntypedRune) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped bitXor(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(leftType, rightType);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().xor(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().xor(((GoTypeUntypedRune) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped bitAnd(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(leftType, rightType);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().and(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().and(((GoTypeUntypedRune) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped bitClear(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(leftType, rightType);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().andNot(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().andNot(((GoTypeUntypedRune) right).getValue()));
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped and(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        if (leftType instanceof GoTypeUntypedBool && rightType instanceof GoTypeUntypedBool) {
            return new GoTypeUntypedBool(((GoTypeUntypedBool) leftType).getValue() && ((GoTypeUntypedBool) rightType).getValue());
        }
        return null;
    }

    @Nullable
    public static GoTypeUntyped or(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        if (leftType instanceof GoTypeUntypedBool && rightType instanceof GoTypeUntypedBool) {
            return new GoTypeUntypedBool(((GoTypeUntypedBool) leftType).getValue() || ((GoTypeUntypedBool) rightType).getValue());
        }
        return null;
    }

    public static GoTypeUntyped lt(GoTypeUntyped leftType, GoTypeUntyped rightType) {
        GoTypeUntyped[] elevatedTypes = GoTypeUntyped.elevateForBinrayOpration(a, b);
        if (elevatedTypes == null) return null;
        GoTypeUntyped left = elevatedTypes[0];
        GoTypeUntyped right = elevatedTypes[1];
        if (left instanceof GoTypeUntypedInteger) {
            return new GoTypeUntypedInteger(((GoTypeUntypedInteger) left).getValue().add(((GoTypeUntypedInteger) right).getValue()));
        }
        if (left instanceof GoTypeUntypedRune) {
            return new GoTypeUntypedRune(((GoTypeUntypedRune) left).getValue().add(((GoTypeUntypedRune) right).getValue()));
        }
        if (left instanceof GoTypeUntypedFloating) {
            return new GoTypeUntypedFloating(((GoTypeUntypedFloating) left).getValue().add(((GoTypeUntypedFloating) right).getValue()));
        }
        if (left instanceof GoTypeUntypedComplex) {
            return new GoTypeUntypedComplex(((GoTypeUntypedComplex) left).getValue().add(((GoTypeUntypedComplex) right).getValue()));
        }
        if (left instanceof GoTypeUntypedString) {
            return new GoTypeUntypedString(((GoTypeUntypedString) left).getValue() + ((GoTypeUntypedString) right).getValue());
        }
        return null;
    }
}
