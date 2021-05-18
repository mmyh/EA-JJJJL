package com.mmyh.eajjjjl.compiler.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class EAServiceInfo {

    public String simpleServiceName;

    public String serviceName;

    public String packageName;

    public List<MethodInfo> methods = new ArrayList<>();

    public static final class MethodInfo {

        public String name;

        public String returnType;

        public LinkedHashMap<String, String> params = new LinkedHashMap<>();

    }
}
