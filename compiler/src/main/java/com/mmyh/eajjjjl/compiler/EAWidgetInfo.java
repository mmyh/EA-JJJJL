package com.mmyh.eajjjjl.compiler;


import com.mmyh.eajjjjl.annotation.EAText;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.VariableElement;

public class EAWidgetInfo {

    public String id;

    public String binding;

    public Map<EAWidgetController.EAWidgetType, String> infoMap = new HashMap<>();

    public String viewName;

    public String widgetType;

    public Map<String, AnnotationValue> annotationsMap = new HashMap<>();

    public void initAnnotation(VariableElement element) {
        EAText eaText = element.getAnnotation(EAText.class);
        if (eaText != null) {
            AnnotationValue value = new AnnotationValue();
            value.vm = eaText.vm();
            value.m = eaText.m();
            annotationsMap.put(EAText.class.getCanonicalName(), value);
        }
    }

    public static final class AnnotationValue {

        public String vm;

        public String m;
    }

}
