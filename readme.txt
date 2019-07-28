Para ambas preguntas compilar con el comando make.
Luego para:
P1: make funciones
P2: make sort

Para ejecutar correctamente la pregunta 1, debe existir el archivo funciones.txt en nuestro directorio principal, en esta tarea viene incorporado uno con el ejemplo dado en el enunciado.

Explicación del algoritmo empleado en la pregunta 2:

Se utilizó el algoritmo conocido 'Quicksort' con algunas modificaciones para ordenar de mayor a menor.
Por cada vez que se ejecute el algoritmo se creará un thread que resuelva el ordenamiento, así mismo, cuando se divida el arreglo en dos mitades ambas se resolveran simultaneamente. Para luego volver a realizar esta división repetitivamente hasta que el arreglo se encuentre ordenado.

El pivote será siempre el ultimo elemento del arreglo
