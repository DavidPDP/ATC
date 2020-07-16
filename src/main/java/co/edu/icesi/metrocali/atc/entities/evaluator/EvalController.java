package co.edu.icesi.metrocali.atc.entities.evaluator;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;


public class EvalController {;
    
    private Controller controller;
  
    public EvalController(Controller controller){
        this.controller=controller;
    }
    @JsonProperty(value = "lastEvent")
    public EvalEvent getLastEvent(){
        if(controller.getLastEvent()!=null){
            return new EvalEvent(controller.getLastEvent());
        }
        return null;
    }
    @JsonProperty(value = "lastName")
    public String getLastName(){
        return controller.getLastName();
    }
    @JsonProperty(value = "name")
    public String getName(){
        return controller.getName();
    }
    @JsonProperty(value = "accountName")
    public String getAccountName(){
        return controller.getAccountName();
    }
    @JsonProperty(value = "email")
    public String getEmail(){
        return controller.getEmail();
    }
    @JsonIgnore
    public Integer getId(){
        return controller.getId();
    }
    @JsonIgnore
    public List<UserTrack> getUserTracks(){
        return controller.getUserTracks();
    }
    
}