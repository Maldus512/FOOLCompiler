package lib;

import ast.*;

public class FOOLlib {
  
  private static int labCount=0; 
  
  private static int funLabCount=0; 

  private static String funCode=""; 

  //valuta se il tipo "a" � <= al tipo "b", dove "a" e "b" sono tipi di base: int o bool
  public static boolean isSubtype (Node a, Node b) {
    return a.getClass().equals(b.getClass()) ||
    	   ( (a instanceof BoolTypeNode) && (b instanceof IntTypeNode) ); //
  } 
  
  /* probabilmente non servirà mai a nulla, nel caso la cencellerò
  // S <: T, means that any term of type S can be safely used in a context where a term of type T is expected
  public static boolean isSubclass (ClassNode s, ClassNode t, Environment env){
    if (s.superClassId == null){ // se s non ha superclasse allora non è sottotipo
      return false;
    }
    else if (s.superClassId == t.id){ //se la superclasse di s è t allora è sottotipo di t
      return true;
    }
    else{ //se non ci siamo fermati s potrebbe essere una sottoclasse n-esima di t, quindi richiamo ricorsivamente sulla superclasse di s e t
      HashMap<String,STentry> hm = env.getST().get(env.getNestLevel());
      STentry superClassEntry = hm.get( superClassId );
      return isSubclass( superClassEntry.getClassNode() , t);  //non sono sicuro sia giusto
    }
  }
*/
  public static String freshLabel() { 
	return "label"+(labCount++);
  } 

  public static String freshFunLabel() { 
	return "function"+(funLabCount++);
  } 
  
  public static void putCode(String c) { 
    funCode+="\n"+c; //aggiunge una linea vuota di separazione prima di funzione
  } 
  
  public static String getCode() { 
    return funCode;
  } 


}