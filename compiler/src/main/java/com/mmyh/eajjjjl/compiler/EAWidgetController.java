package com.mmyh.eajjjjl.compiler;


import com.mmyh.eajjjjl.compiler.render.EAAbstractRender;
import com.mmyh.eajjjjl.compiler.render.EAImageRender;
import com.mmyh.eajjjjl.compiler.render.EATextColorRender;
import com.mmyh.eajjjjl.compiler.render.EATextRender;

import java.util.HashMap;
import java.util.Map;

public class EAWidgetController {

    private Map<String, String> textMap = new HashMap<>();

    private Map<String, String> textColorMap = new HashMap<>();

    private Map<String, String> imageMap = new HashMap<>();

    private void initMap(String[] texts, Map<String, String> map) {
        for (String text : texts) {
            String[] tmps = text.split(EAConstant.SPLIT_DOUHAO);
            map.put(tmps[0], tmps.length > 1 ? tmps[1] : EAConstant.str_BaseData);
        }
    }

    public enum EAWidgetType {

        Text(createRender(EATextRender.class)),
        TextColor(createRender(EATextColorRender.class)),
        Image(createRender(EAImageRender.class));

        EAAbstractRender render;

        private EAWidgetType(EAAbstractRender render) {
            this.render = render;
        }

        public EAAbstractRender getRender() {
            return render;
        }
    }

    public void process(EAWidgetInfo widgetInfo) {
        String textValue = textMap.get(widgetInfo.id);
        if (!EAUtil.isEmpty(textValue)) {
            widgetInfo.infoMap.put(EAWidgetType.Text, textValue);
        }
        String textColorValue = textColorMap.get(widgetInfo.id);
        if (!EAUtil.isEmpty(textColorValue)) {
            widgetInfo.infoMap.put(EAWidgetType.TextColor, textColorValue);
        }
        String imageValue = imageMap.get(widgetInfo.id);
        if (!EAUtil.isEmpty(imageValue)) {
            widgetInfo.infoMap.put(EAWidgetType.Image, imageValue);
        }
    }

    static Map<Class<? extends EAAbstractRender>, EAAbstractRender> map = new HashMap<>();

    private static EAAbstractRender createRender(Class<? extends EAAbstractRender> cls) {
        EAAbstractRender render = map.get(cls);
        if (render == null) {
            try {
                render = cls.newInstance();
                map.put(cls, render);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return render;
    }

}
