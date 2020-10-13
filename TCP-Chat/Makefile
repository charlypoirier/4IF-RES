sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all: 
	javac -d classes/ $(sources)

clean:
	rm -f  *.class

.PHONY: all clean