# Projet PPC AKKA  

Ce projet de Master 2 STL, destiné au parcours STL, a été réalisé par :  
- **Salim Tabellout** *(21307533)*  
- **Yanis Tabellout** *(21307532)*  

## Hiérarchie des acteurs  

Le **musicien** constitue l’acteur principal du système, instanciant les autres acteurs :  
- **Oreille** : Permet d’écouter les événements externes, tels que la disparition d’un acteur ou la mise à jour du statut du **chef d’orchestre** (*Conductor*).  
- **Cœur** : Composant chargé d’émettre, toutes les **50 ms**, un signal aux autres musiciens afin d’attester de son bon fonctionnement.  
- **Conductor** : Composant permettant aux musiciens de désigner un **chef d’orchestre** et de procéder à son élection.  

## Élection du Conductor  

L’élection du **chef d’orchestre** s’articule autour de plusieurs étapes :  
- **Phase d’attente** : À leur arrivée, les musiciens doivent observer une période d’attente de **5 secondes** avant d’être considérés comme disponibles. Durant cette période, ils demeurent actifs mais indisponibles.  
- **Phase de communication entre conductors** : Chaque **Conductor** échange avec ses homologues afin de mettre à jour leur statut et d’identifier l’existence éventuelle d’un chef d’orchestre. Chaque composant transmet un signal toutes les **50 ms**.  
- **Phase d’élection** : Deux cas de figure peuvent se présenter :  
  - Si un **chef d’orchestre** est déjà en place, aucune élection n’est nécessaire.  
  - Dans le cas contraire, les **Conductors** procèdent à une élection en sélectionnant le musicien disponible ayant l’indice le plus bas.  

Il est noté que le chef d'orchestre attend 30 secondes maximum avant de quitter si aucun musicien se manifeste.
## Disparition d’un musicien  

Un musicien est considéré comme inactif s’il n’a pas émis de signal durant **2 secondes**, sachant que chaque musicien envoie une notification toutes les **50 ms**.  

### Gestion de la disparition d’un musicien actif  
Si le musicien en charge de l’exécution de la musique devient inactif, le **chef d’orchestre** désigne alors le premier musicien disponible selon l’ordre des indices.  

### Gestion de la disparition du chef d’orchestre  
En cas de défaillance du **chef d’orchestre**, un nouveau **Conductor** est immédiatement élu selon le même processus que lors de l’élection initiale, à ceci près qu’aucune période d’attente de **5 secondes** n’est requise.  
