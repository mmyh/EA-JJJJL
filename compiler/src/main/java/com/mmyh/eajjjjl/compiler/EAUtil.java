package com.mmyh.eajjjjl.compiler;

import com.squareup.javapoet.ClassName;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

public class EAUtil {

    ProcessingEnvironment processingEnv;

    AbstractProcessor processor;

    public Elements elementUtils;

    public Types types;

    public Filer filer;

    public EAUtil(AbstractProcessor processor, ProcessingEnvironment processingEnv) {
        this.processor = processor;
        this.processingEnv = processingEnv;
        elementUtils = processingEnv.getElementUtils();
        types = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
    }

    public void printError(String string) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.ERROR, processor.getClass().getSimpleName() + ":" + string);
    }

    public void printNote(String string) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.NOTE, processor.getClass().getSimpleName() + ":" + string);
    }

    public static boolean isEmpty(String string) {
        return null == string || string.length() == 0;
    }

    public boolean isEmptyStr(String string) {
        return null == string || string.length() == 0;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static ClassName getCN(String s) {
        return ClassName.bestGuess(s);
    }

    public static String toUpperCaseFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public String firstToUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public String firstToLowerCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'A' && ch[0] <= 'Z') {
            ch[0] = (char) (ch[0] + 32);
        }
        return new String(ch);
    }
}
