package model;

import java.util.HashMap;
import java.util.List;

public class RenamedClassesList {

    private static RenamedClassesList instance = null;
    private static HashMap<String,String> renamedClasses;

    private RenamedClassesList(){
        renamedClasses = new HashMap<>();
    }

    public static RenamedClassesList getInstance(){
        if(instance == null){
            instance = new RenamedClassesList();
        }
        return instance;
    }

    public HashMap<String, String> getRenamedClasses(){
        return renamedClasses;
    }


}
