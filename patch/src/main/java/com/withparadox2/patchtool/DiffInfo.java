package com.withparadox2.patchtool;

import java.util.HashSet;
import java.util.Set;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedField;
import org.jf.dexlib2.dexbacked.DexBackedMethod;

public class DiffInfo {

  private static DiffInfo info;
  private Set<DexBackedClassDef> addedClasses;
  private Set<DexBackedField> addedFields;
  private Set<DexBackedMethod> addedMethods;
  private Set<DexBackedClassDef> modifiedClasses;
  private Set<DexBackedField> modifiedFields;
  private Set<DexBackedMethod> modifiedMethods;

  static {
    info = new DiffInfo();
  }

  private DiffInfo() {
    this.addedClasses = new HashSet();
    this.modifiedClasses = new HashSet();
    this.addedFields = new HashSet();
    this.modifiedFields = new HashSet();
    this.addedMethods = new HashSet();
    this.modifiedMethods = new HashSet();
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

  public DexBackedClassDef getAddedClasses(String clazz) {
    for (DexBackedClassDef classDef : this.addedClasses) {
      if (classDef.getType().equals(clazz)) {
        return classDef;
      }
    }
    return null;
  }

  public void addAddedClasses(DexBackedClassDef clazz) {
    System.out.println("add new Class:" + clazz.getType());
    this.addedClasses.add(clazz);
  }

  public Set<DexBackedClassDef> getModifiedClasses() {
    return this.modifiedClasses;
  }

  public DexBackedClassDef getModifiedClasses(String clazz) {
    for (DexBackedClassDef classDef : this.modifiedClasses) {
      if (classDef.getType().equals(clazz)) {
        return classDef;
      }
    }
    return null;
  }

  public void addModifiedClasses(DexBackedClassDef clazz) {
    System.out.println("add modified Class:" + clazz.getType());
    this.modifiedClasses.add(clazz);
  }

  public Set<DexBackedField> getAddedFields() {
    return this.addedFields;
  }

  public void addAddedFields(DexBackedField field) {
    this.addedFields.add(field);
    if (!this.modifiedClasses.contains(field.classDef)) {
      this.modifiedClasses.add((DexBackedClassDef) field.classDef);
    }
  }

  public Set<DexBackedField> getModifiedFields() {
    return this.modifiedFields;
  }

  public void addModifiedFields(DexBackedField field) {
    this.modifiedFields.add(field);
    if (!this.modifiedClasses.contains(field.classDef)) {
      this.modifiedClasses.add((DexBackedClassDef) field.classDef);
    }
  }

  public Set<DexBackedMethod> getAddedMethods() {
    return this.addedMethods;
  }

  public void addAddedMethods(DexBackedMethod method) {
    this.addedMethods.add(method);
    if (!this.modifiedClasses.contains(method.classDef)) {
      this.modifiedClasses.add(method.classDef);
    }
  }

  public Set<DexBackedMethod> getModifiedMethods() {
    return this.modifiedMethods;
  }

  public void addModifiedMethods(DexBackedMethod method) {

    this.modifiedMethods.add(method);
    if (!this.modifiedClasses.contains(method.classDef)) {
      this.modifiedClasses.add(method.classDef);
    }
  }
}
