package com.mmyh.eajjjjl.compiler.processor;

import com.google.auto.service.AutoService;
import com.mmyh.eajjjjl.annotation.EAServiceAuto;
import com.mmyh.eajjjjl.compiler.annotationhandler.EAServiceAnnotationHandler;
import com.mmyh.eajjjjl.compiler.codegenerate.EAServiceCodeGenerater;
import com.mmyh.eajjjjl.compiler.model.EAServiceInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class EAServiceProcessor extends EABaseProcessor {

    EAServiceAnnotationHandler annotationHandler;

    EAServiceCodeGenerater codeGenerater;

    List<EAServiceInfo> serviceInfoList = new ArrayList<>();

    boolean isProcessed = false;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotationHandler = new EAServiceAnnotationHandler(eaUtil);
        codeGenerater = new EAServiceCodeGenerater(eaUtil);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(EAServiceAuto.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        printNote("start");
        if (!roundEnv.processingOver() && !isProcessed) {
            try {
                annotationHandler.handle(serviceInfoList);
                for (EAServiceInfo eaServiceInfo : serviceInfoList) {
                    codeGenerater.generate(eaServiceInfo);
                }
                serviceInfoList.clear();
                isProcessed = true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            printNote("stop");
        }
        return false;
    }


}
