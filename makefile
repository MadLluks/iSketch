NAME=pc2r_isketch
EXEPATH=bin/
OBJPATH=obj/
SRCPATH=src/
INCLUDEPATH=include/
LIBPATH=lib/

ARG=3 5 10

CC=gcc
DEBUG=-g
CFLAGS=-Wall
LFLAGS=
EXEC=$(EXEPATH)server

all: $(EXEC)

$(EXEPATH)server : $(OBJPATH)server.o
	$(CC) $(OBJPATH)server.o -o $(EXEPATH)server -L $(LIBPATH) -I $(INCLUDEPATH) -lfthread -lpthread

	
$(OBJPATH)server.o : $(SRCPATH)server.c
	$(CC) $(DEBUG) $(CFLAGS) -c $(SRCPATH)server.c -o $(OBJPATH)server.o -L $(LIBPATH) -I $(INCLUDEPATH)

server: $(EXEPATH)server

tar:
	make clean; tar -zcvf $(NAME).tar.gz bin/ include/ lib/ obj/ src/ README makefile fich1 fichTest1 fichTest2; make all

clean:
	rm $(OBJPATH)*.o $(EXEPATH)*