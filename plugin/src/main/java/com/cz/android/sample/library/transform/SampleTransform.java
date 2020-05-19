package com.cz.android.sample.library.transform;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.cz.android.sample.api.AndroidSampleConstant;
import com.cz.android.sample.api.item.CategoryItem;
import com.cz.android.sample.api.item.RegisterItem;
import com.cz.android.sample.library.checker.AnnotationChecker;
import com.cz.android.sample.library.confiuration.AndroidManifest;
import com.cz.android.sample.library.create.AndroidProjectFileCreator;
import com.cz.android.sample.library.create.AndroidSampleTemplateCreator;
import com.cz.android.sample.library.visitor.ActivityClassVisitor;
import com.cz.android.sample.library.visitor.AnnotationCheckerVisitor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import org.apache.commons.io.FileUtils;
import org.gradle.api.Project;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES;

/**
 * Created by cz
 * @date 2020-05-17 11:57
 * @email bingo110@126.com
 */
public class SampleTransform extends Transform {
    private final Project project;

    public SampleTransform(Project project) {
        this.project = project;
    }

    @Override
    public String getName() {
        return "Sample";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return ImmutableSet.of(CLASSES);
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT);
    }

    @Override
    public Set<? super QualifiedContent.Scope> getReferencedScopes() {
        return Sets.immutableEnumSet(QualifiedContent.Scope.PROJECT);
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws IOException {
        if (transformInvocation.isIncremental()) {
            throw new UnsupportedOperationException("Unsupported incremental build!");
        }
        //Parse the manifest file
        AndroidManifest androidManifest = new AndroidManifest();
        File manifestFile;
        Context context = transformInvocation.getContext();
        String variantName = context.getVariantName();
        if("release".equals(variantName)){
            manifestFile=new File(project.getBuildDir(),"intermediates/manifests/full/release/AndroidManifest.xml");
            if(!manifestFile.exists()){
                manifestFile=new File(project.getBuildDir(),"intermediates/merged_manifests/release/AndroidManifest.xml");
            }
        } else {
            manifestFile=new File(project.getBuildDir(),"intermediates/manifests/full/debug/AndroidManifest.xml");
            if(!manifestFile.exists()){
                manifestFile=new File(project.getBuildDir(),"intermediates/merged_manifests/debug/AndroidManifest.xml");
            }
        }
        AndroidManifest.ManifestInformation manifestInformation = androidManifest.parseManifestFile(manifestFile);
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        outputProvider.deleteAll();
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //Copy all the jar and classes to the where they need to...
        for (TransformInput input : inputs) {
            input.getJarInputs().parallelStream().forEach(jarInput -> {
                File dest = outputProvider.getContentLocation(jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(), Format.JAR);
                if (dest.exists()) {
                    throw new RuntimeException("Jar file " + jarInput.getName() + " already exists!" +
                            " src: " + jarInput.getFile().getPath() + ", dest: " + dest.getPath());
                }
                try {
                    FileUtils.copyFile(jarInput.getFile(), dest);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }

        File outputFile=null;
        Map<String, List<String>> configurationClassMap=new HashMap<>();
        List<CategoryItem> categoryList=new ArrayList<>();
        List<RegisterItem> registerList=new ArrayList<>();
        List<String> projectFiles=new ArrayList<>();
        for (TransformInput input : inputs) {
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            if (null==outputFile && null != directoryInputs && !directoryInputs.isEmpty()) {
                DirectoryInput directoryInput = directoryInputs.iterator().next();
                outputFile = outputProvider.getContentLocation(directoryInput.getName(), directoryInput.getContentTypes(), directoryInput.getScopes(), Format.DIRECTORY);
            }
            directoryInputs.forEach(dir -> {
                try {
                    final File file = dir.getFile();
                    if (file.isDirectory()) {
                        final String filePath = file.getPath();
                        Files.walk(file.toPath()).filter(path -> {
                            String name = path.toFile().getName();
                            return name.endsWith(".class") && !name.startsWith("R$") &&
                                    !"R.class".equals(name) && !"BuildConfig.class".equals(name);
                        }).forEach(path -> {
                            File classFile = path.toFile();
                            String classFilePath = classFile.getPath();
                            String packageClassName = classFilePath.substring(filePath.length()+1);
                            String classPath;
                            if(filePath.contains("kotlin-classes")){
                                classPath = packageClassName.replace(".class",".kt");
                            } else {
                                classPath = packageClassName.replace(".class",".java");
                            }
                            if(!classPath.contains("$")){
                                projectFiles.add(classPath);
                            }
                            try {
                                processJavaClassFile(file,classFile, manifestInformation,configurationClassMap,categoryList,registerList);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    File destFolder = outputProvider.getContentLocation(dir.getName(), dir.getContentTypes(), dir.getScopes(), Format.DIRECTORY);
                    FileUtils.copyDirectory(dir.getFile(), destFolder);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
        try {
            generateProjectFileClass(outputFile,projectFiles);
            generateConfigurationClassFile(outputFile,configurationClassMap,categoryList,registerList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Copy source file to application's assets folder.
    }

    private void processJavaClassFile(File classFolder,File file,
                                      AndroidManifest.ManifestInformation manifestInformation, Map<String,List<String>> configurationMap,
                                      List<CategoryItem> categoryList,List<RegisterItem> registerList) throws IOException {
        //The first step: We process context classes include the Application class file.
        String absolutePath = file.getAbsolutePath();
        int classNameLength = classFolder.getAbsolutePath().length();
        String classPath = absolutePath.substring(classNameLength+1,absolutePath.length()-".class".length()).replace('/','.');
        byte[] bytes = Files.readAllBytes(file.toPath());
        if(manifestInformation.isActivityClassFile(classPath)){
            try {
                //Change the super activity class of this sample.
                changeActivitySuperClass(file,bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Check if the class is the configured class.
        collectConfigurationFile(bytes,configurationMap,categoryList,registerList);
    }

    private void changeActivitySuperClass(File file, byte[] bytes) throws IOException {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS);
        classReader.accept(new ActivityClassVisitor(classWriter), ClassReader.EXPAND_FRAMES);
        byte[] code = classWriter.toByteArray();
        FileOutputStream fos = new FileOutputStream(file.getParentFile().getAbsoluteFile() + File.separator + file.getName());
        fos.write(code);
        fos.close();
    }

    private void collectConfigurationFile(byte[] bytes, Map<String,List<String>> configurationMap,
                                          List<CategoryItem> categoryList,List<RegisterItem> registerList) {
        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(classReader,ClassWriter.COMPUTE_MAXS);
        AnnotationCheckerVisitor configurationVisitor = new AnnotationCheckerVisitor(classWriter);
        classReader.accept(configurationVisitor, ClassReader.EXPAND_FRAMES);

        AnnotationChecker matchAnnotationChecker = configurationVisitor.getAnnotationChecker();
        if(null!=matchAnnotationChecker){
            String annotation = matchAnnotationChecker.getAnnotation();
            List<String> configurationList = configurationMap.get(annotation);
            if(null==configurationList){
                configurationList=new ArrayList<>();
                configurationMap.put(annotation,configurationList);
            }
            String className = configurationVisitor.getClassName();
            configurationList.add(className);

            CategoryItem categoryItem = configurationVisitor.getCategoryItem();
            if(null!=categoryItem){
                categoryList.add(categoryItem);
            }
            RegisterItem registerItem = configurationVisitor.getRegisterItem();
            if(null!=registerItem){
                registerList.add(registerItem);
            }
        }
    }

    private void generateProjectFileClass(File outputFile, List<String> projectFiles) throws Exception {
        String classPath=AndroidSampleConstant.CLASS_PACKAGE.replace(".","/")+
                "/"+AndroidSampleConstant.PROJECT_FILE_CLASS_NAME;

        AndroidProjectFileCreator.create(outputFile,classPath,projectFiles);
    }

    private void generateConfigurationClassFile(File outputFile, Map<String, List<String>> configurationMap,
                                                List<CategoryItem> categoryList,List<RegisterItem> registerList) throws Exception {
        List<String> functionList = configurationMap.get(AnnotationCheckerVisitor.ANNOTATION_FUNCTION);
        List<String> componentList = configurationMap.get(AnnotationCheckerVisitor.ANNOTATION_COMPONENT);
        List<String> processorList = configurationMap.get(AnnotationCheckerVisitor.ANNOTATION_ACTION_PROCESSOR);
        List<String> testCaseList = configurationMap.get(AnnotationCheckerVisitor.ANNOTATION_TEST_CASE);
        List<String> mainComponentList = configurationMap.get(AnnotationCheckerVisitor.ANNOTATION_MAIN_COMPONENT);

        String mainComponent=null;
        if(null!=mainComponentList&&!mainComponentList.isEmpty()){
            mainComponent=mainComponentList.get(0);
        }

        String classPath=AndroidSampleConstant.CLASS_PACKAGE.replace(".","/")+
                "/"+AndroidSampleConstant.ANDROID_SIMPLE_CLASS_NAME;
        AndroidSampleTemplateCreator.create(outputFile,classPath,
                categoryList,registerList,functionList,
                componentList,processorList,testCaseList,mainComponent);
    }
}