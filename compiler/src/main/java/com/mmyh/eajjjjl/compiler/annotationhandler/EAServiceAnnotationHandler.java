package com.mmyh.eajjjjl.compiler.annotationhandler;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.mmyh.eajjjjl.annotation.EAServicePrivate;
import com.mmyh.eajjjjl.compiler.EAUtil;
import com.mmyh.eajjjjl.compiler.model.EAServiceInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EAServiceAnnotationHandler extends EABaseAnnotationHandler {
    public EAServiceAnnotationHandler(EAUtil eaUtil) {
        super(eaUtil);
    }

    public void handle(List<EAServiceInfo> serviceInfoList, List<String> myServiceList) throws Exception {
        List<String> list = new ArrayList<>();
        String filepath = System.getProperty("user.dir") + File.separator + "service.config";
        File file = new File(filepath);
        if (file.exists()) {
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String str;
                while ((str = in.readLine()) != null) {
                    list.add(str);
                }
            } catch (IOException e) {
            }
        }
        for (String serviceInfo : list) {
            if (!serviceInfo.endsWith("Impl")) {
                exception(serviceInfo + " must endsWith Impl");
            }
            String[] strs = serviceInfo.split(",");
            if (strs.length != 3) {
                exception(serviceInfo + " incorrect format");
            }
            String moduleName = strs[0];
            String packageName = strs[1];
            String serviceName = strs[2];
            //printNote("Service:" + serviceName);
            EAServiceInfo eaServiceInfo = new EAServiceInfo();
            eaServiceInfo.simpleServiceName = serviceName;
            eaServiceInfo.packageName = packageName;
            eaServiceInfo.serviceName = packageName + "." + serviceName;

            TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
            TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(System.getProperty("user.dir") + "/" + moduleName + "/src/main/java"));

            CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
            combinedSolver.add(reflectionTypeSolver);
            combinedSolver.add(javaParserTypeSolver);

            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
            StaticJavaParser
                    .getConfiguration()
                    .setSymbolResolver(symbolSolver);

            CompilationUnit cu = null;
            try {
                List<String> importsList = new ArrayList<>();
                cu = StaticJavaParser.parse(new File(System.getProperty("user.dir") + "/" + moduleName + "/src/main/java/" + packageName.replace(".", "/") + "/", serviceName + ".java"));
                cu.findAll(ImportDeclaration.class).forEach(id -> {
                    importsList.add(id.getNameAsString());
                    //printNote("import:" + id.getNameAsString());
                });
                cu.findAll(MethodDeclaration.class).forEach(md -> {
                    Optional<AnnotationExpr> annotationByClass = md.getAnnotationByClass(EAServicePrivate.class);
                    if (!annotationByClass.isPresent()
                            || myServiceList.contains(eaServiceInfo.serviceName)) {
                        EAServiceInfo.MethodInfo methodInfo = new EAServiceInfo.MethodInfo();
                        methodInfo.name = md.getNameAsString();
                        //printNote(methodInfo.name);
                        String returnType = getType(md.getType(), importsList);
                        //printNote("returnType:" + returnType);
                        methodInfo.returnType = returnType;
                        NodeList<Modifier> modifiers = md.getModifiers();
                        if (modifiers != null
                                && modifiers.contains(Modifier.publicModifier())
                                && !modifiers.contains(Modifier.staticModifier())) {
                            NodeList<Parameter> parameters = md.getParameters();
                            for (Parameter parameter : parameters) {
                                String type = getType(parameter.getType(), importsList);
                                if (!eaUtil.isEmptyStr(type)) {
                                    methodInfo.params.put(type, parameter.getNameAsString());
                                }
                            }
                        }
                        eaServiceInfo.methods.add(methodInfo);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            serviceInfoList.add(eaServiceInfo);
        }
    }

    private void exception(String str) throws Exception {
        throw new Exception(str);
    }

    private String getType(Type type, List<String> importsList) {
        String typeStr = type.asString();
        for (String importStr : importsList) {
            if (importStr.endsWith("." + typeStr)) {
                return importStr;
            }
        }
        if (type.isPrimitiveType()) {
            return type.asPrimitiveType().asString();
        } else if (type.isReferenceType()) {
            return type.asClassOrInterfaceType().resolve().getQualifiedName();
        } else if (type.isVoidType()) {
            return Void.class.getCanonicalName();
        }
        return null;
    }
}
