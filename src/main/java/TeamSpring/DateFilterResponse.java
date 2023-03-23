package TeamSpring;

import java.util.ArrayList;

public class DateFilterResponse extends Response {
    private ArrayList<Team> teams;

    public DateFilterResponse() {
        this.teams = new ArrayList<Team>();
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
    }
}
