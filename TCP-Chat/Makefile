sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

server:
	@cd classes/; java stream.ChatServer 5555

client:
	@cd classes/; java stream.ChatClient 127.0.0.1 5555

clean:
	rm -rf classes/*

.PHONY: all clean