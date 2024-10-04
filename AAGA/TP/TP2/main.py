

class Node: 
    def __init__(self,value:int,left,right) -> None:
        self.value = value
        self.left = left
        self.right = right
    
    def is_leaf(self): 
        return not self.left and not self.right 
    def arbre_to_parenthese(self) :
        if(self.is_leaf()):
            str = "()"
        else : 
            str = "( "+ self.left.arbre_to_parenthese()+" "+ self.right.arbre_to_parenthese() +" )"
        return str

def arb2str(arbre:Node, file:str): 
    print(arbre.arbre_to_parenthese()) 
    pass 

arbre = Node(1,(Node(2,None,None)),(Node(3,Node(4,None,None),Node(5,None,None))))

arb2str(arbre,"hi")