package com.mmyh.eajjjjl.compiler.codegenerate;

import com.mmyh.eajjjjl.compiler.EAUtil;
import com.squareup.javapoet.ClassName;

public abstract class BaseCodeGenerater {

    public EAUtil eaUtil;

    public BaseCodeGenerater(EAUtil eaUtil) {
        this.eaUtil = eaUtil;
    }

    protected ClassName getCN(String s) {
        return ClassName.bestGuess(s);
    }
}
