sources = $(wildcard src/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

server:
	@cd classes/; java ServerGUI

client:
	@cd classes/; java ClientGUI

javadoc:
	javadoc -d doc/ ${sources}

clean:
	rm -rf classes/*

.PHONY: all server client clean
