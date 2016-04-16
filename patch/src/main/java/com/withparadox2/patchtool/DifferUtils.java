package com.withparadox2.patchtool;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.jf.dexlib2.dexbacked.DexBackedMethodImplementation;
import org.jf.dexlib2.dexbacked.DexBackedTryBlock;
import org.jf.dexlib2.iface.instruction.Instruction;

public class DifferUtils {
  public static boolean equalsMethod(DexBackedMethodImplementation obj1,
      DexBackedMethodImplementation obj2) {
    boolean re =
        obj1.getRegisterCount() == obj2.getRegisterCount() && equalTryBlocks(obj1.getTryBlocks(),
            obj2.getTryBlocks()) && equalParameterNames(obj1.getInstructions(),
            obj2.getInstructions());
    return re;
  }

  public static boolean equalTryBlocks(List<? extends DexBackedTryBlock> a,
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

  public static boolean equalParameterNames(Iterable<? extends Instruction> ai,
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

  public static boolean equalsInstruction(Instruction obj1, Instruction obj2) {
    return obj1.getOpcode() == obj2.getOpcode();
  }
}
