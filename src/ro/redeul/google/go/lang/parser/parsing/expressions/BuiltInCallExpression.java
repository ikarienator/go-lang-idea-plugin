package ro.redeul.google.go.lang.parser.parsing.expressions;

import com.google.common.collect.ImmutableSet;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.parser.GoParser;
import ro.redeul.google.go.lang.parser.parsing.util.ParserUtils;
import sun.net.www.ParseUtil;

import java.util.Set;

/**
 * User: mtoader
 * Date: Aug 16, 2010
 * Time: 7:53:26 AM
 */
class BuiltInCallExpression implements GoElementTypes {

    private static final Set<String> hasTypeParameter = ImmutableSet.of(
        "new",
        "make"
    );

    private static final  Set<String> noTypeParameter = ImmutableSet.of(
        "append",
        "cap",
        "close",
        "complex",
        "copy",
        "delete",
        "imag",
        "len",
        "panic",
        "print",
        "println",
        "real",
        "recover"
    );

    private static final  Set<String> defaultConversions = ImmutableSet.of(
        "uint8",
        "uint16",
        "uint32",
        "uint64",
        "int8",
        "int16",
        "int32",
        "int64",
        "float32",
        "float64",
        "complex64",
        "complex128",
        "byte",
        "rune",
        "uint",
        "int",
        "uintptr",
        "string",
        "error",
        "bool"
    );


    private static boolean isBuiltInCall(String methodCall) {
        return
            defaultConversions.contains(methodCall) ||
                hasTypeParameter.contains(methodCall) ||
                noTypeParameter.contains(methodCall);
    }

    public static boolean parse(PsiBuilder builder, GoParser parser, PsiBuilder.Marker mark) {

        String callName = builder.getTokenText();
        IElementType elementType = BUILTIN_CALL_EXPRESSION;

        if (!ParserUtils.lookAhead(builder, mIDENT, pLPAREN))
            return false;

        if (!isBuiltInCall(callName))
            return false;

        ParserUtils.eatElement(builder, IDENTIFIER);
        mark.done(IDENTIFIER_EXPRESSION);
        mark = mark.precede();
        ParserUtils.getToken(builder, pLPAREN, "open.parenthesis.expected");

        if (hasTypeParameter.contains(callName)) {
            parser.parseType(builder);
            if (oCOMMA == builder.getTokenType()) {
                builder.advanceLexer();
            }
        }

        if (builder.getTokenType() != pRPAREN) {
            PsiBuilder.Marker expressionList = builder.mark();
            if (parser.parseExpressionList(builder) > 1 || hasTypeParameter.contains(callName)) {
                expressionList.done(GoElementTypes.EXPRESSION_LIST);
            } else {
                expressionList.drop();
            }
            if (ParserUtils.getToken(builder, oTRIPLE_DOT)) {
                elementType = BUILTIN_CALL_EXPRESSION_VARIADIC;
            }
            ParserUtils.getToken(builder, oCOMMA);
        }

        ParserUtils.getToken(builder, pRPAREN, "closed.parenthesis.expected");

        mark.done(elementType);

        return true;
    }
}
