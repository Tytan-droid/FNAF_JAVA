all: clean compile1 run1

compile1:
	javac -d FNAF1/bin $(shell find . -name "*.java")

run1:
	java -cp FNAF1/bin main

clean:
	rm -rf FNAF*/bin
