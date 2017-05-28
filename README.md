# Cloud Canards
Source code for Cloud Canards, a 2D oriental steampunk RPG game.
The game is built with Java and libgdx
### Requirements
JDK8

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

### Contributing
[How to commit and push](https://docs.google.com/document/d/1KCgO_vruejmSxdvUqu2XfoC39zno_lhD3_o7mPA-Vtc/edit)

Asset naming conventions:
* All folders are lowercase without spaces (ex: greatName)
* All assets are lowercase without spaces (ex: greatName.type)
unless indicated otherwise
* Particles have the extension .pe
* Shaders have the extensions .frag and .vert and start with
an uppercase letter
* Localized properties files follow the format defined
[here](https://github.com/libgdx/libgdx/wiki/Internationalization-and-Localization#creating-properties-files)
except with the first letter in lowercase

Coding conventions:
* Use tabs instead of spaces
* Brackets line up
* Follow other standard java conventions