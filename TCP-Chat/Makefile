sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

clean:
	rm -rf classes/*

.PHONY: all clean