package com.mmyh.eajjjjl.compiler.render;

import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.EAWidgetInfo;
import com.squareup.javapoet.CodeBlock;

public abstract class EAAbstractRender {

    public abstract Class<?> getRenderType();

    public abstract void render(CodeBlock.Builder cb, EAWidgetInfo widgetInfo, String fieldName, String value, boolean isModelVariable);

    public boolean canRender(String type) {
        return EAUtil.equals(type, getRenderType().getCanonicalName());
    }
}
