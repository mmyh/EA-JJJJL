package com.mmyh.eajjjjl.compiler.render;

import com.mmyh.eajjjjl.compiler.EAWidgetInfo;
import com.squareup.javapoet.CodeBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EARenderFactory {

    private static List<EAAbstractRender> renders = new ArrayList<>();

    static {
        renders.add(new EATextRender());
        renders.add(new EATextColorRender());
        renders.add(new EAImageRender());
    }

    public static void render(CodeBlock.Builder cb, EAWidgetInfo widgetInfo, String fieldName, String value, boolean isModelVariable) {
        Set<String> types = widgetInfo.annotationsMap.keySet();
        for (String type : types) {
            for (EAAbstractRender render : renders) {
                if (render.canRender(type)) {
                    render.render(cb, widgetInfo, fieldName, value, isModelVariable);
                    break;
                }
            }
        }
    }
}
