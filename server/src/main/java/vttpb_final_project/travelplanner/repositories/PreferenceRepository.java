package vttpb_final_project.travelplanner.repositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonObject;
import vttpb_final_project.travelplanner.models.Preference;
import static vttpb_final_project.travelplanner.models.Preference.*;

@Repository
public class PreferenceRepository {
    
    @Autowired
    private JdbcTemplate template;

    private static final String GET_PREFERENCE = """
            select * from user_preferences where username = ?
            """;
    private static final String UPDATE_PREFERENCE = """
            insert into user_preferences (username, diet, allergies, serving) values (?, ?, ?, ?)
            on duplicate key update
                diet = ?,
                allergies = ?,
                serving = ?;
            """;

    // get preference
    public Optional<Preference> getUserPreference(String username){
        SqlRowSet rs = template.queryForRowSet(GET_PREFERENCE, username);
        if (!rs.next())
            return Optional.empty();
        return Optional.of(toPreference(rs));
    }

    // insert or update preferences
    public int updatePreference(String username, JsonObject j){
        return template.update(UPDATE_PREFERENCE, 
            username, 
            j.getString("diet"), 
            j.getString("allergies"), 
            j.getInt("serving"),
            j.getString("diet"), 
            j.getString("allergies"), 
            j.getInt("serving"));
    }
}
