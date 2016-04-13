package com.withparadox2.patchtool;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.DexBackedField;
import org.jf.dexlib2.dexbacked.DexBackedMethod;
import org.jf.dexlib2.dexbacked.DexBackedMethodImplementation;
import org.jf.dexlib2.dexbacked.DexBackedTryBlock;
import org.jf.dexlib2.iface.instruction.Instruction;

public class DexDiffer {
  public DiffInfo diff(File newFile, File oldFile) throws IOException {
    DexBackedDexFile newDexFile = DexFileFactory.loadDexFile(newFile, 19);
    DexBackedDexFile oldDexFile = DexFileFactory.loadDexFile(oldFile, 19);
    DiffInfo info = DiffInfo.getInstance();
    boolean contains = false;
    for (DexBackedClassDef newClazz : newDexFile.getClasses()) {
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
    compareMethod(newClazz.getMethods(), oldClazz.getMethods(), info);
  }

  public void compareMethod(Iterable<? extends DexBackedMethod> news,
      Iterable<? extends DexBackedMethod> olds, DiffInfo info) {
    for (DexBackedMethod reference : news) {
      if (!reference.getName().equals("<clinit>")) {
        compareMethod(reference, (Iterable) olds, info);
      }
    }
  }

  public void compareMethod(DexBackedMethod object, Iterable<? extends DexBackedMethod> olds,
      DiffInfo info) {
    for (DexBackedMethod reference : olds) {
      if (reference.equals(object)) {
        if (reference.getImplementation() == null && object.getImplementation() != null) {
          info.addModifiedMethods(object);
          return;
        } else if (reference.getImplementation() != null && object.getImplementation() == null) {
          info.addModifiedMethods(object);
          return;
        } else if ((reference.getImplementation() != null) && !equalsMethod(
            reference.getImplementation(), object.getImplementation())) {
          info.addModifiedMethods(object);
          return;
        } else {
          return;
        }
      }
    }
    info.addAddedMethods(object);
  }

  public boolean equalsMethod(DexBackedMethodImplementation obj1,
      DexBackedMethodImplementation obj2) {
    boolean re =
        obj1.getRegisterCount() == obj2.getRegisterCount() && equalTryBlocks(obj1.getTryBlocks(),
            obj2.getTryBlocks()) && equalParameterNames(obj1.getInstructions(),
            obj2.getInstructions());
    return re;
  }

  private boolean equalTryBlocks(List<? extends DexBackedTryBlock> a,
      List<? extends DexBackedTryBlock> b) {
    if (a.size() != b.size()) {
      return false;
    }
    for (int i = 0; i < a.size(); i++) {
      if (!((DexBackedTryBlock) a.get(i)).equals((DexBackedTryBlock) b.get(i))) {
        return false;
      }
    }
    return true;
  }

  private boolean equalParameterNames(Iterable<? extends Instruction> ai,
      Iterable<? extends Instruction> bi) {
    ImmutableList<? extends Instruction> a = ImmutableList.copyOf((Iterable) ai);
    ImmutableList<? extends Instruction> b = ImmutableList.copyOf((Iterable) bi);
    if (a.size() != b.size()) {
      return false;
    }
    for (int i = 0; i < a.size(); i++) {
      if (!equalsInstruction((Instruction) a.get(i), (Instruction) b.get(i))) {
        return false;
      }
    }
    return true;
  }

  public boolean equalsInstruction(Instruction obj1, Instruction obj2) {
    return obj1.getOpcode() == obj2.getOpcode();
  }

  public void compareField(DexBackedClassDef newClazz, DexBackedClassDef oldClazz, DiffInfo info) {
    compareField(newClazz.getFields(), oldClazz.getFields(), info);
  }

  public void compareField(Iterable<? extends DexBackedField> news,
      Iterable<? extends DexBackedField> olds, DiffInfo info) {
    for (DexBackedField reference : news) {
      compareField(reference, (Iterable) olds, info);
    }
  }

  public void compareField(DexBackedField object, Iterable<? extends DexBackedField> olds,
      DiffInfo info) {
    for (DexBackedField reference : olds) {
      if (reference.equals(object)) {
        if (reference.getInitialValue() == null && object.getInitialValue() != null) {
          info.addModifiedFields(object);
          return;
        } else if (reference.getInitialValue() != null && object.getInitialValue() == null) {
          info.addModifiedFields(object);
          return;
        } else if ((reference.getInitialValue() != null || object.getInitialValue() != null)
            && reference.getInitialValue().compareTo(object.getInitialValue()) != 0) {
          info.addModifiedFields(object);
          return;
        } else {
          return;
        }
      }
    }
    info.addAddedFields(object);
  }
}
