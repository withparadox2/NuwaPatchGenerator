package com.withparadox2.patchtool;

import java.io.File;
import java.io.IOException;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedField;
import org.jf.dexlib2.dexbacked.DexBackedMethod;
import org.jf.dexlib2.dexbacked.DexBackedMethodImplementation;
import org.jf.dexlib2.iface.value.EncodedValue;

import static com.withparadox2.patchtool.DifferUtils.equalsMethod;

public class DexDiffer {
  public DiffInfo diff(File newFile, File oldFile) throws IOException {
    DexBackedDexFile newDexFile = DexFileFactory.loadDexFile(newFile, 19);
    DexBackedDexFile oldDexFile = DexFileFactory.loadDexFile(oldFile, 19);
    DiffInfo info = DiffInfo.getInstance();
    for (DexBackedClassDef newClazz : newDexFile.getClasses()) {
      boolean contains = false;
      for (DexBackedClassDef oldClazz : oldDexFile.getClasses()) {
        if (newClazz.equals(oldClazz)) {
          compareField(newClazz, oldClazz, info);
          compareMethod(newClazz, oldClazz, info);
          contains = true;
          break;
        }
      }
      if (!contains) {
        info.addAddedClasses(newClazz);
      }
    }
    return info;
  }

  public void compareMethod(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
    for (DexBackedMethod reference : newClazz.getMethods()) {
      compareMethod(reference, oldClazz.getMethods(), info);
    }
  }

  public void compareMethod(DexBackedMethod object, Iterable<? extends DexBackedMethod> olds,
      DiffInfo info) {
    for (DexBackedMethod reference : olds) {
      if (reference.equals(object)) {
        DexBackedMethodImplementation impOld = reference.getImplementation();
        DexBackedMethodImplementation impNew = object.getImplementation();

        if (impNew != null || impOld != null) {
          boolean equal = impNew != null && impOld != null && equalsMethod(impNew, impOld);
          if (!equal) {
            info.addModifiedMethods(object);
          }
        }
        return;
      }
    }
    info.addAddedMethods(object);
  }

  public void compareField(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
    for (DexBackedField reference : newClazz.getFields()) {
      compareField(reference, oldClazz.getFields(), info);
    }
  }

  public void compareField(DexBackedField object, Iterable<? extends DexBackedField> olds,
      DiffInfo info) {
    for (DexBackedField reference : olds) {
      if (reference.equals(object)) {
        EncodedValue valNew = object.getInitialValue();
        EncodedValue valOld = reference.getInitialValue();

        if (valNew != null || valOld != null) {
          boolean equal = valNew != null && valOld != null && valNew.compareTo(valOld) == 0;
          if (!equal) {
            info.addModifiedFields(object);
          }
        }
        return;
      }
    }
    info.addAddedFields(object);
  }
}
