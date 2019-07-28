JFLAGS = -d
JC = javac
JVM = java 
OUT = ./out/
LIB = './out/exp4j-0.4.8.jar'

default:
	$(JC) $(JFLAGS) $(OUT) -cp .:$(LIB) ./src/P1/funciones.java
	$(JC) $(JFLAGS) $(OUT) ./src/P2/Sort.java

clean:
	$(RM) $(OUT)*.class

funciones:
	$(JVM) -cp .:$(OUT):$(LIB) funciones

sort:
	$(JVM) -cp $(OUT) Sort
