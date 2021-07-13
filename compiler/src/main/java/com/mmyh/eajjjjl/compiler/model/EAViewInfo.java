package com.mmyh.eajjjjl.compiler.model;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;

public class EAViewInfo {

    public List<TypeMirror> viewModels = new ArrayList<>();

    public List<TypeMirror> bindings = new ArrayList<>();

    public List<String> bindingsString = new ArrayList<>();

    public TypeMirror superClass;

    public String viewName;

    public TypeMirror listModel;

    public List<TypeMirror> listBindings = new ArrayList<>();

    public TypeMirror headViewBinding;

    public TypeMirror headViewModel;

    public TypeMirror footViewBinding;

    public TypeMirror footViewModel;

}
