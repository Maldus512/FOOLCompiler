package lib;

import ast.*;
import ast.types.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FOOLlib {
  
  private static int labCount=0; 
  
  private static int funLabCount=0; 

  private static String funCode=""; 

  //valuta se il tipo "a" � <= al tipo "b", dove "a" e "b" sono tipi di base: int o bool
  public static boolean isSubtype (TypeNode a, TypeNode b) {
    if ( (a instanceof BoolTypeNode) && (b instanceof IntTypeNode) ){
      return true;
    }
    else if (/* a.getClass().equals(b.getClass())*/(a instanceof IntTypeNode) && (b instanceof IntTypeNode) ){
      return true;
    } 
    else if (/* a.getClass().equals(b.getClass())*/(a instanceof BoolTypeNode) && (b instanceof BoolTypeNode) ){
      return true;
    } 
    else if ( (a instanceof ClassTypeNode) && (b instanceof ClassTypeNode) ){
      HashMap<String,TypeNode> fieldA = ((ClassTypeNode)a).getFieldTypeMap();
      HashMap<String,TypeNode> fieldB = ((ClassTypeNode)b).getFieldTypeMap();;
      HashMap<String,ArrowTypeNode> methodsA = ((ClassTypeNode)a).getMethodTypeMap();;
      HashMap<String,ArrowTypeNode> methodsB = ((ClassTypeNode)b).getMethodTypeMap();;
      
      if( fieldA.keySet().size() < fieldB.keySet().size() || methodsA.keySet().size() < methodsB.keySet().size()){
        System.out.println("Fatal error: subclass cannot have fewer fields/methods than superclass");
        return false;
      } 
      

      for (Map.Entry<String, TypeNode> entry : fieldB.entrySet()){
        if ( fieldA.get(entry.getKey()) == null ){
          System.out.println("Fatal error: field "+entry.getKey()+" not found in superclass");
          return false;
        }
        else if (! isSubtype(fieldA.get(entry.getKey()),entry.getValue()) ){
          System.out.println("Error: field "+entry.getKey()+" in subclass is not a subtype");
          return false;
        }
      }

      for (Map.Entry<String, ArrowTypeNode> entry : methodsB.entrySet()){
        if ( methodsA.get(entry.getKey()) == null ){
          System.out.println("Fatal error: method "+entry.getKey()+" not found in superclass");
          return false;
        }
        else if (! isSubtype(methodsA.get(entry.getKey()), entry.getValue()) ){
          System.out.println("Error: method "+entry.getKey()+" in subclass is not a subtype");
          return false;
        }
      }
      return true;
    }
    else if ( (a instanceof ArrowTypeNode) && (b instanceof ArrowTypeNode) ){
      
      ArrayList<TypeNode> parlistA = ((ArrowTypeNode)a).getParList(); 
      ArrayList<TypeNode> parlistB = ((ArrowTypeNode)b).getParList();
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