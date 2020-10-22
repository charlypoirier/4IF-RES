client_src = $(wildcard src/http/client/*.java)
server_src = $(wildcard src/http/server/*.java)
client_cls = $(client_src:.java=.class)
server_cls = $(server_cls:.java=.class)

all:
	javac -d bin/ $(client_src) $(server_src)

server:
	@cd bin/; java http.server.WebServer

client:
	@cd bin/; java http.client.WebPing localhost 3000

javadoc:
	javadoc -d doc/ ${client_src} ${server_src}

clean:
	rm -rf bin/*

.PHONY: all server client clean
