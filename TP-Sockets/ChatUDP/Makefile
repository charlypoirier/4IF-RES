sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

user:
	@cd classes/; java GroupChat 230.0.0.0 5000

javadoc:
	javadoc -d doc/ ${sources}

clean:
	rm -rf classes/*

.PHONY: all user clean
