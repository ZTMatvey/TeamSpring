package TeamSpring;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@RestController
public class TeamController {
    private static final String ADD_TEAM_QUERY = "INSERT INTO team (name, kind_of_sport, founding_date) VALUES ";
    @RequestMapping(value = "/teams/getAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamsResponse> getAllTeams() throws Exception {
        return getAll("SELECT * FROM team");
    }

    @RequestMapping(value = "/teams/getAllFiltered", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamsResponse> getAllTeamsFiltered() throws Exception {
        return getAll("SELECT * FROM team t ORDER BY t.kind_of_sport");
    }

    private ResponseEntity<TeamsResponse> getAll(String query) throws Exception {
        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);
        TeamsResponse result = new TeamsResponse();
        result.setSuccess(1);

        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(query);
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        while(set.next()){
            Team team = new Team();
            team.setId(set.getInt("id"));
            team.setName(set.getString("name"));
            team.setKindOfSport(set.getString("kind_of_sport"));
            String date = formatter.format(set.getDate("founding_date").getTime());
            team.setFoundingDate(date);

            result.addTeam(team);
        }

        return new ResponseEntity<TeamsResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teams/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> addTeam(@RequestBody @Valid Team team) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        if (team.getName() == null ||
            team.getKindOfSport() == null ||
            team.getFoundingDate() == null) {
            result.setSuccess(0);
            return new ResponseEntity<EmptyResponse>(result, HttpStatus.BAD_REQUEST);
        }
        String strMinDate = "01-01-1800";
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date minDate = formatter.parse(strMinDate);
        Date actualDate = formatter.parse(team.getFoundingDate());
        if (actualDate.before(minDate)){
            result.setSuccess(0);
            return new ResponseEntity<EmptyResponse>(result, HttpStatus.BAD_REQUEST);
        }

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);
        Statement statement = connection.createStatement();
        String query = ADD_TEAM_QUERY + String.format("('%s', '%s', '%s')", team.getName(), team.getKindOfSport(), team.getFoundingDate());
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }
    @RequestMapping(value = "/teams/getByDateFilter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DateFilterResponse> getByDateFilter(@RequestBody @Valid DateRange dateRange) throws Exception {
        DateFilterResponse result = new DateFilterResponse();
        result.setSuccess(1);

        if (dateRange.getFrom() == null ||
            dateRange.getTo() == null) {
            result.setSuccess(0);
            return new ResponseEntity<DateFilterResponse>(result, HttpStatus.BAD_REQUEST);
        }
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date from = formatter.parse(dateRange.getFrom());
        Date to = formatter.parse(dateRange.getTo());
        if (to.before(from)){
            result.setSuccess(0);
            return new ResponseEntity<DateFilterResponse>(result, HttpStatus.CONFLICT);
        }

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);
        Statement statement = connection.createStatement();
        String query = String.format("SELECT * FROM team t WHERE t.founding_date > '%s' AND t.founding_date < '%s'", from, to);
        ResultSet set = statement.executeQuery(query);

        while(set.next()){
            Team team = new Team();
            team.setId(set.getInt("id"));
            team.setName(set.getString("name"));
            team.setKindOfSport(set.getString("kind_of_sport"));
            String date = formatter.format(set.getDate("founding_date").getTime());
            team.setFoundingDate(date);

            result.addTeam(team);
        }

        return new ResponseEntity<DateFilterResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teams/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> updateInfo(@RequestBody @Valid Team team) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        if (team.getId() == null ||
                (team.getFoundingDate() == null &&
                        team.getName() == null &&
                        team.getKindOfSport() == null)){
            result.setSuccess(0);
            return new ResponseEntity<EmptyResponse>(result, HttpStatus.BAD_REQUEST);
        }

        String update = "";
        if(team.getFoundingDate() != null){
            update += String.format("founding_date = '%s',", team.getFoundingDate());
        }
        if(team.getName() != null){
            update += String.format("name = '%s',", team.getName());
        }
        if(team.getKindOfSport() != null){
            update += String.format("kind_of_sport = '%s',", team.getKindOfSport());
        }

        update = update.substring(0, update.length() - 1);

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);

        Statement statement = connection.createStatement();
        String query = String.format("UPDATE team SET %s WHERE ID = %s", update, team.getId());
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/teams/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> delete(@RequestParam Integer teamID) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);

        Statement statement = connection.createStatement();
        String query = String.format("DELETE FROM team WHERE ID = %s", teamID);
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }
}
