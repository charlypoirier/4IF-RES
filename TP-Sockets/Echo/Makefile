sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

server:
	@cd classes/; java stream.ChatServer 5555

client:
	@cd classes/; java stream.ChatClient 127.0.0.1 5555

javadoc:
	javadoc -d doc/ ${sources}

clean:
	rm -rf classes/*

.PHONY: all server client clean