/*

! Polymorphisme : 
? étendre le système de tpe est général 

? instanciation qq soit alphaT -> on peut le remplacer dans n'importe quel type 
?   système F 
? => Système F n'est pas implemntable 


? solution => let polymorphisme 
? pour typer (lambda x. M) N 
? Γ |- N : T et Γ[x:Gen(T)] |- M: U  
? ===> Γ |- (lambda x. M) N : U
? Gen(T) l’op´eration syntaxique qui transforme T en ∀α1∀α2 . . . ∀αn.T si α1, . . . , αn  sont les variables de types libres de T

*/