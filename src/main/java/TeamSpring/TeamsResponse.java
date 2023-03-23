package TeamSpring;

import java.util.ArrayList;

public class TeamsResponse extends Response{
    private ArrayList<Team> teams;

    public TeamsResponse() {
        this.teams = new ArrayList<Team>();
    }

    public void addTeam(Team team){
        teams.add(team);
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }
}
