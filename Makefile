SRC_DIR = FNAF1
OUT_DIR = out
JAR_NAME = FNAF.jar
MAIN_CLASS = Class.Main
MANIFEST = manifest.mf
JAVAC = javac
JAVA = java
JAR = jar



all: clean compile run

compile:
	javac -d FNAF1/bin $(shell find FNAF1 -name "*.java")

run:
	java -cp FNAF1/bin Class.Main

clean:
	rm -rf FNAF1/bin $(JAR_NAME)

jar:
	@echo "Compilation..."
	mkdir -p $(OUT_DIR)
	$(JAVAC) -d $(OUT_DIR) $(shell find $(SRC_DIR) -name "*.java")
	@echo "Creation du manifest..."
	echo "Main-Class: $(MAIN_CLASS)" > $(MANIFEST)
	echo "" >> $(MANIFEST)
	@echo "Creation du JAR..."
	$(JAR) cfm $(JAR_NAME) $(MANIFEST) -C $(OUT_DIR) .
	@echo "Execution du jeu..."
	$(JAVA) -jar $(JAR_NAME)
	@echo "Nettoyage..."
	rm -rf $(OUT_DIR) $(MANIFEST)

runjar:
	$(JAVA) -jar $(JAR_NAME)

