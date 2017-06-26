package lib;

import ast.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FOOLlib {
  
  private static int labCount=0; 
  
  private static int funLabCount=0; 

  private static String funCode=""; 

  //valuta se il tipo "a" � <= al tipo "b", dove "a" e "b" sono tipi di base: int o bool
  public static boolean isSubtype (Node a, Node b) {
    if ( (a instanceof BoolTypeNode) && (b instanceof IntTypeNode) ){
      return true;
    }
    else if ( a.getClass().equals(b.getClass()) ){
      return true;
    } 
    else if ( (a instanceof ClassTypeNode) && (b instanceof ClassTypeNode) ){
      HashMap<String,Node> fieldA = ((ClassTypeNode)a).getFields();
      HashMap<String,Node> fieldB = ((ClassTypeNode)b).getFields();;
      HashMap<String,ArrowTypeNode> methodsA = ((ClassTypeNode)a).getMethods();;
      HashMap<String,ArrowTypeNode> methodsB = ((ClassTypeNode)b).getMethods();;
      
      if( fieldA.keySet().size() != fieldB.keySet().size() || methodsA.keySet().size() != methodsB.keySet().size()){
        return false;
      } 

      for (Map.Entry<String, Node> entry : fieldA.entrySet()){
        if ( fieldB.get(entry.getKey()) == null ){
          return false;
        }
        else if (! isSubtype(entry.getValue(), fieldB.get(entry.getKey())) ){
          return false;
        }
      }

      for (Map.Entry<String, ArrowTypeNode> entry : methodsA.entrySet()){
        if ( methodsB.get(entry.getKey()) == null ){
          return false;
        }
        else if (! isSubtype(entry.getValue(), methodsB.get(entry.getKey())) ){
          return false;
        }
      }
      return true;
    }
    else if ( (a instanceof ArrowTypeNode) && (b instanceof ArrowTypeNode) ){
      
      ArrayList<Node> parlistA = ((ArrowTypeNode)a).getParList(); 
      ArrayList<Node> parlistB = ((ArrowTypeNode)b).getParList();
      if(parlistA.size() != parlistB.size() ){
        return false;
      }
      for( int i=0; i<parlistA.size(); i++){
        if (!isSubtype(parlistB.get(i), parlistA.get(i)) ){
          return false;
        }
      } 
      return ( isSubtype(((ArrowTypeNode)a).getRet(), ((ArrowTypeNode)b).getRet() ));
    }
    else{
      return false;
    }
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