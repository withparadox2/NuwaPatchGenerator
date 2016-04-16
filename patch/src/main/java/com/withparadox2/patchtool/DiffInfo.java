package com.withparadox2.patchtool;

import java.util.HashSet;
import java.util.Set;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedField;
import org.jf.dexlib2.dexbacked.DexBackedMethod;

public class DiffInfo {

  private static DiffInfo info;
  private Set<DexBackedClassDef> addedClasses;
  private Set<DexBackedClassDef> modifiedClasses;

  static {
    info = new DiffInfo();
  }

  private DiffInfo() {
    this.addedClasses = new HashSet();
    this.modifiedClasses = new HashSet();
  }

  public static synchronized DiffInfo getInstance() {
    DiffInfo diffInfo;
    synchronized (DiffInfo.class) {
      diffInfo = info;
    }
    return diffInfo;
  }

  public Set<DexBackedClassDef> getAddedClasses() {
    return this.addedClasses;
  }

  public void addAddedClasses(DexBackedClassDef clazz) {
    System.out.println("add new Class:" + clazz.getType());
    if (AppMain.shouldAcceptClass(clazz.getType()) && !addedClasses.contains(clazz)) {
      this.addedClasses.add(clazz);
    }
  }

  public Set<DexBackedClassDef> getModifiedClasses() {
    return this.modifiedClasses;
  }

  public void addModifiedClasses(DexBackedClassDef clazz) {
    System.out.println("add modified Class:" + clazz.getType());
    if (AppMain.shouldAcceptClass(clazz.getType()) && !modifiedClasses.contains(clazz)) {
      this.modifiedClasses.add(clazz);
    }
  }

  public void addAddedFields(DexBackedField field) {
    addModifiedClasses((DexBackedClassDef) field.classDef);
  }

  public void addModifiedFields(DexBackedField field) {
    addModifiedClasses((DexBackedClassDef) field.classDef);
  }

  public void addAddedMethods(DexBackedMethod method) {
    addModifiedClasses(method.classDef);
  }

  public void addModifiedMethods(DexBackedMethod method) {
    addModifiedClasses(method.classDef);
  }
}
