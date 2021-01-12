package com.mmyh.eajjjjl.compiler.model;

import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

public class EAApiInfo {

    public String apiClass;

    public String apiMethod;

    public String requestClassName;

    public boolean controlLoadingDialog;

    public TypeName returnType;

    public List<String> paramsClassName = new ArrayList<>();

    public String callBack;
}
