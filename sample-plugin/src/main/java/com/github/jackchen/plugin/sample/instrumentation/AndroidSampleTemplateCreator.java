package com.github.jackchen.plugin.sample.instrumentation;

import java.io.File;
import java.io.FileOutputStream;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AndroidSampleTemplateCreator implements Opcodes {
    private static final String CONFIGURATION_CLASS_NAME = "SampleConfiguration";
    private static final String CONFIGURATION_CLASS_PATH =
            "com/github/jackchen/android/core/" + CONFIGURATION_CLASS_NAME;

    public static void create(File outputFile, String configurationJsonText) throws Exception {
        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;
        classWriter.visit(V11, ACC_PUBLIC | ACC_SUPER, CONFIGURATION_CLASS_PATH, null, "java/lang/Object",
                null);

        classWriter.visitSource(CONFIGURATION_CLASS_NAME + ".java", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC, "CONFIGURATION_JSON",
                    "Ljava/lang/String;", null, configurationJsonText);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(3, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "L" + CONFIGURATION_CLASS_PATH + ";", null, label0, label1, 0);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        File classFile = new File(outputFile, CONFIGURATION_CLASS_PATH + ".class");
        if (!classFile.getParentFile().exists()) {
            classFile.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(classFile)) {
            fos.write(classWriter.toByteArray());
        }
    }
}
