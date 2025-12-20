all: clean compile run

compile:
	javac -d FNAF1/bin $(shell find FNAF1 -name "*.java")

run:
	java -cp FNAF1/bin Class.Main

clean:
	rm -rf FNAF1/bin
