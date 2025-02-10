/*

! modèle acteur : 
?   pour acoomplir une tâche il faudra répartir les tâches : e.g : 
?       -   commander un café par plusieurs personnes au même temps, (je veux un café et que y'a 1000 personnes qui sont là)   


acteur => 20 lignes pas 200

un acteur est un agent spécialisé sur un domaine 


?   communication entre acteurs :
?       -   message asynchrone 
?       -   chaque acteur a une boite aux lettres de messages
?       -   chaque acteur a un comportement (réaction à un message)


! AXIOME / 
?   Acteur parle qu'avec un nombre limité d'acteurs


?   créer un acteur : spawn(Module,Function,Args) => PID  
module : module qui contient la fonction
function : fonction à exécuter
args : arguments de la fonction


PID c'est L'identifiant de l'acteur SON ADDRESSE (PID = Process ID)


pour envoyer un message  il faut : 
PID !  message (e.g : ECHO ! "Hello")

?   dans les fonctio de l'acteur n on fait un receive : 
?       -   les receives sont les message à retrouver 

? faut faire attention : 
quand vous avez une retourne une valeur, il faut que l'acteur soit toujours vivant en faisant un appel récursive pour s'assurer qu'il est toujours vivant e.g : 

run () -> 
    S = spawn(counter,counter,[0]) // initialise la variable à 0
    send_msgs(S,1000). // envoie 1000 messages


counter(Sum) -> 
    receive 
        value -> print("Value is ~p~n",[Sum]); 
        {inc,Amount} -> counter(Sum + Amount)
    end.


send_msgs(_,0) ->true;
send_msgs(S,N) ->
    S ! {inc,1},
    send_msgs(S,N-1).
*/