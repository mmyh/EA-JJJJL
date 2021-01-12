package com.mmyh.eajjjjl.compiler.processor;

import com.mmyh.eajjjjl.compiler.EAUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.tools.Diagnostic;

public abstract class EABaseProcessor extends AbstractProcessor {

    protected EAUtil eaUtil;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        eaUtil = new EAUtil(this, processingEnv);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    public void printNote(String string) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, getClass().getSimpleName() + ":" + string);
    }
}
