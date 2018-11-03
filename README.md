## Contents

1.- [Getting started](1-gettingStarted/)

2.- [Language Basics](2-languageBasics/)

3.- [Object-Oriented Programming](3-oop/)

4.- [Object-Oriented Programming in Java](4-oop-java/)

5.- [Java Support Classes](5-classes/)

## Practical assignments

* [Práctica 1: Ejercicios básicos de Java](assignments/p01/p1.pdf)

* [Práctica 2: Introducción a la POO en Java](assignments/p02/p2.pdf)

* [Práctica 3: Herencia y sobrecarga en Java](assignments/p03/p3.pdf)

* [Práctica 4: Excepciones, E/S y colecciones](assignments/p04/p4.pdf)

* [Práctica 5: Bucle principal de un juego](assignments/p05/p5.pdf)

* [Práctica 6: Patrones de diseño](assignments/p06/p6.pdf)

## Compilation

Use xelatex or lualatex as latex processors, otherones would raise a compilation error. Custom UAH fonts are needed to properly compile the project. The original template used to format the slides, including the fonts, can be downloaded from [this repository](https://github.com/dfbarrero/UAH-beamer-template). By default, the theme will use UAH fonts installed in the system, edit the theme to easily change this behaviour.

*Manual execution of xelatex or lualatex would generate a compilation error*, since the tex documents require a variable containing the course for which the slides are compiled. You should use instead the command make which calls xelatex with the proper variable definition.

Several Makefiles are provided to ease repository management. Make executed from the root folder would compile the entire collections of slides for all the courses, while "make view" would open a PDF viewer with all the complided slides. On the contrary, if make is executed within a subfolder, it would only compile the slided contained in that folder. By default, make compiles the slides for all the courses, while "make course" would build the slides for that course.

The courses for which the slides are compiled are defined in the TARGET variable, in the begining of the Makefiles. The courses details such their names are defined in [gsi-parametros.sty](gsi-parametros.sty), in the root folder.

## Credits

This course is based on "[The Python Tutorial](https://docs.python.org/3/tutorial/index.html)".
