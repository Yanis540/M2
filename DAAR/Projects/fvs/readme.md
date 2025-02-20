# Feed Back Vertex Set (FVS) AAGA 
Projet **Master 2 STL** du module AAGA (analyse d'algorithmes et génération aléatoire) effectué par : 
Yanis Tabellout 21307532
Salim Tabellout 21307533

# Améloriation de l'algorithme : 
Cet algorithme se base sur l'algorithme d'optimisation vu en cours, le pseudo code de cet algorithme est le suivant : 
```python
def fvs_optimsation(g:Graph):Node[]
    points =g.points()
    fvs = init(points)
    while(mutate(fvs).size()> fvs.size()) : 
        fvs = mutate(fvs)
    return fvs
```    

# Initialisation 
## Approche Initiale : Min degré
Initialement nous avons essayé de prendre l'approche avec le max degrée, sauf que celle ci ne fonctionne pas car le max degré suggère que les points ayant le plus de degré sont ceux qui causent les cycles sauf que celà est faux par exemple pour les ponts. 
## Min degrée
Nous avons donc implémenter la solution en utilisant le min degrée qui part du principe que : nous considérons que tout les noeuds du graphes font partis de l'ensemble de départ **fvs**, puis nous allons tout les noeuds qui permettent de garder le graphe sans cycle lorsqu'on les retire de fvs. Pour avoir des meilleurs résultats, nous allons lancer cette fonction en parallèle sur **100 threads** tout en assurant de mélanger l'ensemble des points



# Mutation
Pour celà, nous avons utilisé la méthode décrite pendant le cour **($M_k$)**, nous alons donc utiliser la méthode **remove2add1** réprésentant le cas **$M_2$**  ainsi que la méthode **removeUnecassaryPoint** qui est le cas **$M_1$**. Pour optimser le temps de rendu, nous avons lancer la méthode **remove2add1** en parallèle pour permettre de trouver plus rapidement le point à ajouter dans l'ensemble en choisissant de manière aléatoire le noeud à ajouter. La méthode **removeUnecassaryPoint** permet de trouver les points à supprimer du **fvs** dans le cas ou la méthode **remove2add1** n'arrive pas à trouver des meilleurs solutions


# Performances 
Grâce à notre approche, nous avons pu rapprocher l'algorithme à un temps de exceptionnel de **6min**  et avec une moyenne de **80.56** noeud enlevé. A noter que la méthode proposé initialement aurait pris **52h** pour avoir le même résultat. 
