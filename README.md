# CloudCanards
Source code for the old version of CloudCanards, an action metroidvania RPG.
Unlike the current build, this version is built with Java and libgdx.

Test tileset by KnoblePersona

### Requirements
JDK8

### Updating Version Number
Build number is automatically updated every time you run the game (an hour
needs to have passed since the last run). The major.minor.patch number should
be updated manually on core/assets/data/cloudCanards/version.properties

### Running
Run the `run` task in `build.gradle` in the desktop folder

### Distributing
To create the jar file, run the `dist` task in `build.gradle` in the desktop folder.
Or in the terminal, you can run this command:
```
gradlew desktop:dist
```
You can run the jar file with this command:
```
java -jar NAME.jar
```
