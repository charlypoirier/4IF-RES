sources = $(wildcard src/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

server:
	@cd classes/; java ServerGUI

client:
	@cd classes/; java ClientGUI

clean:
	rm -rf classes/*

.PHONY: all server client clean
