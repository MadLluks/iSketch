#include <stdio.h>
#include <stdlib.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h> 
#include <pthread.h>
#include <fthread.h>

#define INVALID_SOCKET -1
#define SOCKET_ERROR -1
#define FALSE 0
#define TRUE 1
#define PORT 2013

typedef int SOCKET;
typedef struct sockaddr_in SOCKADDR_IN;
typedef struct sockaddr SOCKADDR;

int debug = TRUE;

SOCKET sock; // Acces global de la socket pour tous les threads
ft_scheduler_t sched;

struct client{
  SOCKET socket; // socket du client
  char* nom;
  int num;
  int hisTurn;
  int tAction;
  char* command; // command envoyé par le client
  char* params[16]; // paramètre envoyé par le client
  char* text; // variable de text lié au client
  char* authorizedAction[4];
};

struct client joueurs[4];

int nbPersonne = 0;
int nbJoueurs = 0;

ft_thread_t tabThreadL[4], tabThreadE[4];
ft_event_t sendEvent[4];

/* =============== PROTOTYPES ================================= */

void afficherJoueur();

void tConnection(void* data);

void tSend(void* data);

void tReceive(void* data);

/* ============== CODE ======================================== */

int main(int argc, char const *argv[])
{
	int sock_err; 
	SOCKADDR_IN sin; // Socket et contexte d'adressage du serveur
	socklen_t recsize = sizeof(sin);

	sched = ft_scheduler_create();
	
	/* Création d'une socket */
	if( (sock = socket(AF_INET, SOCK_STREAM, 0)) == INVALID_SOCKET){
		perror("Socket");
		return EXIT_FAILURE;
	}
	if(debug)
		printf("Main : la socket %d est maintenand ouverte en mode TCP/IP\n", sock);

	int optval = 1;
	setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, sizeof(optval)); // Permet la réutilisation de la socket

	/* Configuration */
	sin.sin_addr.s_addr = htonl(INADDR_ANY);  /* Adresse IP automatique */
	sin.sin_family = AF_INET;                 /* Protocole familial (IP) */
	sin.sin_port = htons(PORT);               /* Listage du port */
	
	if( (sock_err = bind(sock, (SOCKADDR*)&sin, recsize)) == SOCKET_ERROR){
		perror("bind");
		return EXIT_FAILURE;
	}
	/* Démarrage du listage (mode server) */
	if( (sock_err = listen(sock, 5)) == SOCKET_ERROR){
		perror("listen");
		return EXIT_FAILURE;
	}

	if(debug)
		printf("Main : Socket %d\n", sock);

	sendEvent[0] = ft_event_create(sched);
	sendEvent[1] = ft_event_create(sched);
	sendEvent[2] = ft_event_create(sched);
	sendEvent[3] = ft_event_create(sched);

	/* Création du thread de connexion et démarrage du scheduler */
	ft_thread_create(sched, tConnection, NULL, &sock);

	ft_scheduler_start(sched);                   
	
	ft_exit();
	return 0;
}

void tConnection(void* data){
	int i, j;
	SOCKET tmp;
	SOCKADDR_IN csin;
	socklen_t crecsize = sizeof(csin);

	while(1){
		if(debug)
			printf("Patientez pendant que le client se connecte sur le port %d...\n", PORT);
		ft_thread_unlink();
		tmp = accept(sock, (SOCKADDR*)&csin, &crecsize);
		for(i = 0; i<nbJoueurs; i++){
			for(j = i+1; j<nbJoueurs; j++){
				if(joueurs[i].socket == 0){
					joueurs[i] = joueurs[j];
					joueurs[i].num = joueurs[i].num - 1;
				}
			}
		}
		nbPersonne = nbPersonne + 1;
		joueurs[nbPersonne-1].socket = tmp;
		joueurs[nbPersonne-1].num = nbPersonne;
		joueurs[nbPersonne-1].hisTurn = TRUE;

		ft_thread_link(sched);
		if(debug)
			printf("Un client se connecte avec la socket %d de %s:%d\n", joueurs[nbPersonne-1].socket, inet_ntoa(csin.sin_addr), htons(csin.sin_port));
		printf("nbPersonne -1 : %d\n", nbPersonne-1);
		tabThreadE[nbPersonne-1] = ft_thread_create(sched, tSend, NULL, (void *)&joueurs[nbPersonne-1]);
		tabThreadL[nbPersonne-1] = ft_thread_create(sched, tReceive, NULL, (void *)&joueurs[nbPersonne-1]);
		afficherJoueur();
	}
}

void tReceive(void *data){
	struct client * joueur = data;
  	char buffer[2048]; // Buffer qui contiendra les données envoyées par le client.
	int read = 0; // int contenant la valeur de retour du recv pour savoir si la réception c'est bien passé
	char reponse[2048] = "";
	int isConnect = FALSE;
	char tmp[2048];
	char text[2048];
	char nom[2048];

	while(joueur->socket != 0){
		ft_thread_unlink();
		memset(buffer, 0, sizeof (buffer));

		printf("Je me met en lecture.\n");
		read = recv(joueur->socket, buffer, 1, 0);
		printf("Messeage reçut.\n");

		if(read != 0){
			strcpy(tmp, buffer);
			strcpy(buffer, "");
			while(read != 0 && buffer[0] != 10)
			{ 
				memset(buffer, 0, sizeof (buffer));
				read = recv(joueur->socket, buffer, 1, 0);
				strcat(tmp, buffer);
			}
			ft_thread_link(sched);

			afficherJoueur();
			sleep(1);

			strcpy(text, tmp);
			printf("Lecture d'une donnée : %s\n", text);
		}
	}
}

void tSend(void *data){
	struct client *joueur = data;
	char ** text = NULL;
	int size = 0;
	int k, nb;

	while(1){
		ft_thread_await(sendEvent[joueur->num-1]);
		ft_thread_get_value(sendEvent[joueur->num-1], 0, (void**)&text);
		printf("Emission, évènement, client : %d, donnée : %s\n", joueur->num, *text);
		sendEvent[joueur->num-1] = ft_event_create(sched);

	}

}

void afficherJoueur(){
	int i;
	printf("Nombre de joueurs : %d\n", nbJoueurs);
	for(i = 0; i<nbJoueurs; i++){
		printf("Joueur numéro : %d, socket : %d, nom : %s\n", joueurs[i].num, joueurs[i].socket, joueurs[i].nom);
	}
}
