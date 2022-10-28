package ru.vsu.cs.processor;

import com.google.auto.service.AutoService;
import ru.vsu.cs.annotation.TestAnnotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

@SupportedAnnotationTypes({"ru.vsu.cs.annotation.TestAnnotation"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class TestAnnotationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        for (Element e : roundEnv.getElementsAnnotatedWith(TestAnnotation.class)) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "START GEN");
        String name = capitalize(e.getSimpleName().toString());
        TypeElement clazz = (TypeElement) e.getEnclosingElement();
        try {
            JavaFileObject f = processingEnv.getFiler().
                    createSourceFile(clazz.getQualifiedName() + "Extras");
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "Creating " + f.toUri());
            Writer w = f.openWriter();
            try {
                PrintWriter pw = new PrintWriter(w);
                pw.println("package "
                        + clazz.getEnclosingElement().getSimpleName() + ";");
                pw.println("public abstract class "
                        + clazz.getSimpleName() + "Extras {");
                pw.println("    protected " + clazz.getSimpleName()
                        + "Extras() {}");
                TypeMirror type = e.asType();
                pw.println("    /** Handle something. */");
                pw.println("    protected final void handle" + name
                        + "(" + type + " value) {");
                pw.println("        System.out.println(value);");
                pw.println("    }");
                pw.println("}");
                pw.flush();
            } finally {
                w.close();
            }
        } catch (IOException x) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    x.toString());
        }
        JavaFileObject builderFile = null;
        try {
            builderFile = processingEnv.getFiler().createSourceFile("ru.vsu.cs.TestClass");
        } catch (IOException ex1) {
            ex1.printStackTrace();
        }
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            out.println("""
                    package ru.vsu.cs;
                    
                    public class TestClass {
                        
                        public void sout() {
                            System.out.println("test");
                        }
                    }
                    """);
            out.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        }

        return true;
    }

    private static String capitalize(String name) {
        char[] c = name.toCharArray();
        c[0] = Character.toUpperCase(c[0]);
        return new String(c);
    }
}
