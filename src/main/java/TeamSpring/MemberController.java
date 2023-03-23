package TeamSpring;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@RestController
public class MemberController {
    @RequestMapping(value = "/members/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> addMember(@RequestBody @Valid Member member) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        if (member.getTeamID() == null ||
            member.getDateOfBirth() == null ||
            member.getFirstName() == null ||
            member.getSecondName() == null ||
            member.getSurname() == null ||
            member.getPosition() == null){
            result.setSuccess(0);
            return new ResponseEntity<EmptyResponse>(result, HttpStatus.BAD_REQUEST);
        }

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);

        Statement statement = connection.createStatement();
        String query = String.format("INSERT INTO member (team_id, surname, first_name, second_name, date_of_birth, position)" +
                " VALUES (%s, '%s', '%s', '%s', '%s', '%s')", member.getTeamID(), member.getSurname(), member.getFirstName(),
                member.getSecondName(), member.getDateOfBirth(), member.getPosition());
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/members/getAllByTeamID", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetMembersByTeamIDResponse> getAllByTeamID(@RequestParam Integer teamID) throws Exception {
        if (teamID == null){
            GetMembersByTeamIDResponse result = new GetMembersByTeamIDResponse();
            return new ResponseEntity<GetMembersByTeamIDResponse>(result, HttpStatus.BAD_REQUEST);
        }

        String query = String.format("SELECT * FROM member m WHERE m.team_id = %s", teamID);
        return getAll(query);
    }

    @RequestMapping(value = "/members/getAllByTeamIDFiltered", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetMembersByTeamIDResponse> getAllByTeamIDFiltered(@RequestParam Integer teamID) throws Exception {
        if (teamID == null){
            GetMembersByTeamIDResponse result = new GetMembersByTeamIDResponse();
            return new ResponseEntity<GetMembersByTeamIDResponse>(result, HttpStatus.BAD_REQUEST);
        }

        String query = String.format("SELECT * FROM member m WHERE m.team_id = %s ORDER BY m.position", teamID);
        return getAll(query);
    }

    private ResponseEntity<GetMembersByTeamIDResponse> getAll(String query) throws Exception {
        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);
        GetMembersByTeamIDResponse result = new GetMembersByTeamIDResponse();
        result.setSuccess(1);

        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(query);
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

        while(set.next()){
            Member member = new Member();
            member.setId(set.getInt("id"));
            member.setTeamID(set.getInt("team_id"));
            member.setFirstName(set.getString("first_name"));
            member.setSecondName(set.getString("second_name"));
            member.setSurname(set.getString("surname"));
            String date = formatter.format(set.getDate("date_of_birth").getTime());
            member.setDateOfBirth(date);
            member.setPosition(set.getString("position"));

            result.addMember(member);
        }

        return new ResponseEntity<GetMembersByTeamIDResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/members/move", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> move(@RequestBody @Valid MoveMember moveMember) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        if (moveMember.getId() == null ||
                moveMember.getTo() == null){
            result.setSuccess(0);
            return new ResponseEntity<EmptyResponse>(result, HttpStatus.BAD_REQUEST);
        }

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);

        Statement statement = connection.createStatement();
        String query = String.format("UPDATE member SET team_id = %s WHERE ID = %s", moveMember.getTo(), moveMember.getId());
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/members/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> updateInfo(@RequestBody @Valid Member member) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        if (member.getId() == null ||
                (member.getPosition() == null &&
                member.getSurname() == null &&
                member.getSecondName() == null &&
                member.getFirstName() == null &&
                member.getDateOfBirth() == null)){
            result.setSuccess(0);
            return new ResponseEntity<EmptyResponse>(result, HttpStatus.BAD_REQUEST);
        }

        String update = "";
        if(member.getPosition() != null){
            update += String.format("position = '%s',", member.getPosition());
        }
        if(member.getSurname() != null){
            update += String.format("surname = '%s',", member.getSurname());
        }
        if(member.getSecondName() != null){
            update += String.format("second_name = '%s',", member.getSecondName());
        }
        if(member.getFirstName() != null){
            update += String.format("first_name = '%s',", member.getFirstName());
        }
        if(member.getDateOfBirth() != null){
            update += String.format("date_of_birth = '%s',", member.getDateOfBirth());
        }

        update = update.substring(0, update.length() - 1);

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);

        Statement statement = connection.createStatement();
        String query = String.format("UPDATE member SET %s WHERE ID = %s", update, member.getId());
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/members/delete", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyResponse> delete(@RequestParam Integer memberID) throws Exception {
        EmptyResponse result = new EmptyResponse();
        result.setSuccess(1);

        Connection connection = DriverManager.getConnection(DbInfo.DB_URL, DbInfo.DB_USERNAME, DbInfo.DB_PASSWORD);

        Statement statement = connection.createStatement();
        String query = String.format("DELETE FROM member WHERE ID = %s", memberID);
        statement.execute(query);

        return new ResponseEntity<EmptyResponse>(result, HttpStatus.OK);
    }
}
