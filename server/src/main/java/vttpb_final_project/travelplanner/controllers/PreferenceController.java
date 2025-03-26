package vttpb_final_project.travelplanner.controllers;

import java.io.StringReader;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttpb_final_project.travelplanner.models.Preference;
import vttpb_final_project.travelplanner.repositories.PreferenceRepository;
import static vttpb_final_project.travelplanner.models.Preference.*;

@RestController
@RequestMapping("/api")
public class PreferenceController {

    private static final Logger logger = Logger.getLogger(PreferenceController.class.getName());
    
    @Autowired
    private PreferenceRepository preferenceRepo;

    @GetMapping(path = "/{username}/preference", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getUserPreference(@PathVariable String username){
        Optional<Preference> opt = preferenceRepo.getUserPreference(username);
        if (opt.isEmpty()){
            logger.info(">>> no preference");
            JsonObject obj = Json.createObjectBuilder()
                .add("message", "You have not set your preference")
                .build();
            return ResponseEntity.status(404).body(obj.toString());
        }
        return ResponseEntity.ok(toJson(opt.get()).toString());
    }

    @PutMapping(path = "/{username}/preference", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> putUserPreference(@PathVariable String username, @RequestBody String payload){

        // convert payload to json
        JsonReader reader = Json.createReader(new StringReader(payload));
        JsonObject j = reader.readObject();
        System.out.println(j.toString());

        int updated = preferenceRepo.updatePreference(username, j);
        if (updated > 0){
            logger.info(">>> updated preferences");
            JsonObject obj = Json.createObjectBuilder()
                .add("message", "Your preference has been updated successfully!")
                .build();
            return ResponseEntity.status(201).body(obj.toString());
        } else {
            logger.info(">>> failed to update preference");
            JsonObject obj = Json.createObjectBuilder()
                .add("message", "Failed to update your preference")
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj.toString());
        }

    }
}
