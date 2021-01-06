package com.mmyh.eajjjjl.compiler.render;

import com.mmyh.eajjjjl.compiler.EAConstant;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.EAWidgetInfo;
import com.squareup.javapoet.CodeBlock;

public class EAImageRender extends EAAbstractRender {

    @Override
    public Class<?> getRenderType() {
        return EAImageRender.class;
    }

    @Override
    public void render(CodeBlock.Builder cb, EAWidgetInfo widgetInfo, String fieldName, String value, boolean isModelVariable) {
        if (EAUtil.isEmpty(widgetInfo.viewName)) {
            if (isModelVariable) {
                cb.add("$N($N, $N.$N);\n", EAConstant.RenderImageView, widgetInfo.id, fieldName, value);
            } else {
                cb.add("$N($N, $N);\n", EAConstant.RenderImageView, widgetInfo.id, fieldName);
            }
        } else {
            String viewSimpleName = widgetInfo.viewName.substring(widgetInfo.viewName.lastIndexOf(".") + 1);
            cb.add("if($N.this instanceof $T){\n", viewSimpleName, EAUtil.getCN(EAConstant.EAIImageRender));
            if (isModelVariable) {
                cb.add("$T.setImage(($T)$N.this, $N, $N.$N);\n}\n", EAUtil.getCN(EAConstant.RENDER), EAUtil.getCN(EAConstant.EAIImageRender), viewSimpleName, widgetInfo.id, fieldName, value);
            } else {
                cb.add("$T.setImage(($T)$N.this, $N, $N);\n}\n", EAUtil.getCN(EAConstant.RENDER), EAUtil.getCN(EAConstant.EAIImageRender), viewSimpleName, widgetInfo.id, fieldName);
            }
            cb.add("else {\n");
            if (isModelVariable) {
                cb.add("$N($N, $N.$N);\n}\n", EAConstant.RenderImageView, widgetInfo.id, fieldName, value);
            } else {
                cb.add("$N($N, $N);\n}\n", EAConstant.RenderImageView, widgetInfo.id, fieldName);
            }
        }
    }
}
