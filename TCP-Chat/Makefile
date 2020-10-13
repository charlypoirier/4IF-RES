sources = $(wildcard src/stream/*.java)
classes = $(sources:.java=.class)

all:
	javac -d classes/ $(sources)

server:
	@cd classes/; java stream.EchoServerMultiThreaded 5555

client:
	@cd classes/; java stream.EchoClient 127.0.0.1 5555

clean:
	rm -rf classes/*

.PHONY: all clean