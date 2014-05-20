/**
 * Copyright (c) 2014, Bei Zhang
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package ro.redeul.google.go.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameterList;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeFunction;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

public abstract class GoFunction extends GoPsiPackagedElementBase implements GoPsiTypeFunction {

    public GoFunction(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoType resolveType() {
        GoFunctionParameter[] parameters = this.getParameters();
        GoFunctionParameter[] results = this.getResults();
        GoType[] parameterTypes = new GoType[parameters.length];
        GoType[] resultTypes = new GoType[results.length];
        boolean isVariadic = false;
        int i = 0;
        for (GoFunctionParameter parameter : parameters) {
            if (parameter.isVariadic()) {
                isVariadic = true;
            }
            parameterTypes[i++] = parameter.getType().getType();
        }
        i = 0;
        for (GoFunctionParameter resultType : results) {
            resultTypes[i++] = resultType.getType().getType();
        }
        return new GoTypeFunction(this, parameterTypes, resultTypes, isVariadic);
    }

    @NotNull
    public GoFunctionParameter[] getParameters() {
        GoFunctionParameterList parameterList =
                findChildByClass(GoFunctionParameterList.class);

        if (parameterList == null) {
            return GoFunctionParameter.EMPTY_ARRAY;
        }

        return parameterList.getFunctionParameters();
    }

    @NotNull
    public GoFunctionParameter[] getResults() {
        PsiElement result = findChildByType(GoElementTypes.FUNCTION_RESULT);

        return GoPsiUtils.getParameters(result);
    }
}
