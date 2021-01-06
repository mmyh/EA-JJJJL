package com.mmyh.eajjjjl.compiler.render;

import com.mmyh.eajjjjl.annotation.EAText;
import com.mmyh.eajjjjl.compiler.EAConstant;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.EAWidgetInfo;
import com.squareup.javapoet.CodeBlock;

public class EATextRender extends EAAbstractRender {

    @Override
    public Class<?> getRenderType() {
        return EAText.class;
    }

    @Override
    public void render(CodeBlock.Builder cb, EAWidgetInfo widgetInfo, String fieldName, String value, boolean isModelVariable) {
        if (isModelVariable) {
            cb.add("$T.setText($N, $N.$N);\n", EAUtil.getCN(EAConstant.RENDER), widgetInfo.id, fieldName, value);
        } else {
            cb.add("$T.setText($N, $N);\n", EAUtil.getCN(EAConstant.RENDER), widgetInfo.id, fieldName);
        }
    }

}
