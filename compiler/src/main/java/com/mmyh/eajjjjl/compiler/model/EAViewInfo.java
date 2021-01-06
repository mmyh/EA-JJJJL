package com.mmyh.eajjjjl.compiler.model;


import com.mmyh.eajjjjl.compiler.EAWidgetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.type.TypeMirror;

public class EAViewInfo {

    public List<TypeMirror> viewModels = new ArrayList<>();

    public List<TypeMirror> bindings = new ArrayList<>();

    public List<String> bindingsString = new ArrayList<>();

    public List<EAWidgetInfo> widgetsList = new ArrayList<>();

    public TypeMirror superClass;

    public String viewName;

    public Map<TypeMirror, List<EAWidgetInfo>> widgetMap = new HashMap<>();

    public TypeMirror listModel;

    public List<TypeMirror> listBindings = new ArrayList<>();

    public TypeMirror headViewBinding;

    public TypeMirror headViewModel;

    public TypeMirror footViewBinding;

    public TypeMirror footViewModel;

}
