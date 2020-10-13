sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all: program
program: $(classes)

%.class: %.java
	javac -d classes/ $<

clean:
	rm -f  *.class

.PHONY: all program clean