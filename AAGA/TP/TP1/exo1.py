
def decomposeNumberToTable(n)->list:
    if (n==0):
        return[0]
    t=[]
    while(n!=0): 
        t.append(n%10)
        n=n/10
    return t

def vonNeumann(N:int):
    "N : supposé à 10 chiffre" 
    n_2 = N**2
    n_2_table = decomposeNumberToTable(n_2)
    if(len(n_2_table) <20): 
        while(len(n_2_table)<20): 
            n_2_table=[0]+n_2_table
    # initializing K 
    K = 10
    
    # computing strt, and end index 
    strt_idx = (len(n_2_table) // 2) - (K // 2)
    end_idx = (len(n_2_table) // 2) + (K // 2)
    
    # using loop to get indices 
    res = []
    for idx in range(len(n_2_table)):
        
        # checking for elements in range
        if idx >= strt_idx and idx <= end_idx:
            res.append(n_2_table[idx])
    
    total=0 
    p=1 
    for e in res[::-1]:
        total+=e*p 
        p*=10
    return total

print(vonNeumann(123_456_789_0))

