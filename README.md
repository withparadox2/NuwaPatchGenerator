# NuwaPatchGenerator
Create patch file from apks for project [Nuwa][1]. Note, this project is rewritten from the implementation of patch tools of project AndFix, which claims to open source later on.

# How to use?
Setup project first, then execute gradle task by `gradlew jar`, you will get a jar file **patch.jar** under *project/patch/build/libs*.

Put your apks in some folder (e.g. *apkpath*), with a structure like this:
```
apkpath/
   new.apk
   old.apk
```

Generate patch file **diff.dex** by using command line:
```
java -jar patch.jar apkpath
```

If you also put **patch.jar** under the folder *apkpath*, command line showed above can be simplified:
```
java -jar patch.jar
```

When you finally get the patch file **diff.dex**, you can rename it to anything you need without extra working of packaging it to a jar file.

[1]: http://square.github.io/picasso/
