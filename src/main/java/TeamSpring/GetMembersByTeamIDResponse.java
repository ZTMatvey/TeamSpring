package TeamSpring;

import java.util.ArrayList;

public class GetMembersByTeamIDResponse extends Response {
    private ArrayList<Member> members;

    public GetMembersByTeamIDResponse() {
        this.members = new ArrayList<Member>();
    }

    public void addMember(Member member){
        members.add(member);
    }

    public ArrayList<Member> getMembers() {
        return members;
    }
}
