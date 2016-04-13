package com.withparadox2.patchtool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jf.baksmali.Adaptors.ClassDefinition;
import org.jf.baksmali.baksmaliOptions;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.util.SyntheticAccessorResolver;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.jf.dexlib2.writer.io.FileDataStore;
import org.jf.util.ClassFileNameHandler;
import org.jf.util.IndentingWriter;

public class AppMain {

  public static void main(String[] args) {
    System.out.println("hello");

    String pathOldApk = "E:\\android\\projects\\NuwaPatchGenerator\\patch\\apks\\old.apk";
    String pathNewApk = "E:\\android\\projects\\NuwaPatchGenerator\\patch\\apks\\new.apk";
    try {
      new AppMain().extractApk(pathOldApk, pathNewApk);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void extractApk(String oldApk, String newApk) throws IOException {
    File oldFile = new File(oldApk);
    if (!oldFile.exists()) {
      return;
    }
    File newFile = new File(newApk);
    if (!newFile.exists()) {
      return;
    }
    DiffInfo info = new DexDiffer().diff(newFile, oldFile);
    for (DexBackedClassDef classDef : info.getModifiedClasses()) {
      System.out.println(classDef.getType());
    }

    File out = new File("E:\\android\\projects\\NuwaPatchGenerator\\patch\\apks");
    File smaliDir = new File(out, "smali");
    if (!smaliDir.exists()) {
      smaliDir.mkdir();
    }
    try {
      FileUtils.cleanDirectory(smaliDir);
    } catch (Exception e) {

    }
    File dexFile = new File(out, "diff.dex");
    if (dexFile.exists() && !dexFile.delete()) {
      return;
    }

    Set<String> classes = new HashSet();
    Set<DexBackedClassDef> list = new HashSet();
    list.addAll(info.getAddedClasses());
    list.addAll(info.getModifiedClasses());
    baksmaliOptions options = new baksmaliOptions();
    options.deodex = false;
    options.noParameterRegisters = false;
    options.useLocalsDirective = true;
    options.useSequentialLabels = true;
    options.outputDebugInfo = true;
    options.addCodeOffsets = false;
    options.jobs = -1;
    options.noAccessorComments = false;
    options.registerInfo = 0;
    options.ignoreErrors = false;
    options.inlineResolver = null;
    options.checkPackagePrivateAccess = false;
    if (!options.noAccessorComments) {
      options.syntheticAccessorResolver = new SyntheticAccessorResolver(list);
    }
    ClassFileNameHandler outFileNameHandler = new ClassFileNameHandler(smaliDir, ".smali");
    ClassFileNameHandler inFileNameHandler = new ClassFileNameHandler(smaliDir, ".smali");
    DexBuilder dexBuilder = DexBuilder.makeDexBuilder();
    for (DexBackedClassDef classDef : list) {
      String className = classDef.getType();
      disassembleClass(classDef, outFileNameHandler, options);
      File smaliFile = inFileNameHandler.getUniqueFilenameForClass(newType(className));
      classes.add(newType(className).substring(1, newType(className).length() - 1)
          .replace(IOUtils.DIR_SEPARATOR_UNIX, '.'));
      try {
        SmaliMod.assembleSmaliFile(smaliFile, dexBuilder, true, true);
      } catch (RecognitionException e) {
        e.printStackTrace();
      }
    }
    dexBuilder.writeTo(new FileDataStore(dexFile));
  }

  public static String newType(String type) {
    return type.substring(0, type.length() - 1) + ";";
  }

  private static boolean disassembleClass(ClassDef classDef, ClassFileNameHandler fileNameHandler,
      baksmaliOptions options) {
    /**
     * The path for the disassembly file is based on the package name
     * The class descriptor will look something like:
     * Ljava/lang/Object;
     * Where the there is leading 'L' and a trailing ';', and the parts of the
     * package name are separated by '/'
     */
    String classDescriptor = classDef.getType();

    //validate that the descriptor is formatted like we expect
    if (classDescriptor.charAt(0) != 'L'
        || classDescriptor.charAt(classDescriptor.length() - 1) != ';') {
      System.err.println(
          "Unrecognized class descriptor - " + classDescriptor + " - skipping class");
      return false;
    }

    File smaliFile = fileNameHandler.getUniqueFilenameForClass(classDescriptor);

    //create and initialize the top level string template
    ClassDefinition classDefinition = new ClassDefinition(options, classDef);

    //write the disassembly
    Writer writer = null;
    try {
      File smaliParent = smaliFile.getParentFile();
      if (!smaliParent.exists()) {
        if (!smaliParent.mkdirs()) {
          // check again, it's likely it was created in a different thread
          if (!smaliParent.exists()) {
            System.err.println(
                "Unable to create directory " + smaliParent.toString() + " - skipping class");
            return false;
          }
        }
      }

      if (!smaliFile.exists()) {
        if (!smaliFile.createNewFile()) {
          System.err.println("Unable to create file " + smaliFile.toString() + " - skipping class");
          return false;
        }
      }

      BufferedWriter bufWriter =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(smaliFile), "UTF8"));

      writer = new IndentingWriter(bufWriter);
      classDefinition.writeTo((IndentingWriter) writer);
    } catch (Exception ex) {
      System.err.println("\n\nError occurred while disassembling class "
          + classDescriptor.replace('/', '.')
          + " - skipping class");
      ex.printStackTrace();
      // noinspection ResultOfMethodCallIgnored
      smaliFile.delete();
      return false;
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Throwable ex) {
          System.err.println("\n\nError occurred while closing file " + smaliFile.toString());
          ex.printStackTrace();
        }
      }
    }
    return true;
  }
}
