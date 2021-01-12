package com.mmyh.eajjjjl.compiler.codegenerate;

import com.mmyh.eajjjjl.compiler.EAUtil;
import com.squareup.javapoet.ClassName;

public abstract class EABaseCodeGenerater {

    public EAUtil eaUtil;

    public EABaseCodeGenerater(EAUtil eaUtil) {
        this.eaUtil = eaUtil;
    }

    protected ClassName getCN(String s) {
        return ClassName.bestGuess(s);
    }
}
