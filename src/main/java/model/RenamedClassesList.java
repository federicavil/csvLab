package model;

import java.util.HashMap;
import java.util.Map;

public class RenamedClassesList {

    private static RenamedClassesList instance = null;
    private static HashMap<String,String> renamedClasses = new HashMap<>();

    private RenamedClassesList(){
    }

    public static RenamedClassesList getInstance(){
        if(instance == null){
            instance = new RenamedClassesList();
        }
        return instance;
    }

    public Map<String, String> getRenamedClasses(){
        return renamedClasses;
    }


}
