package TeamSpring;

public class Team {
    private Integer id;
    private String name;
    private String kindOfSport;
    private String foundingDate;

    public String getFoundingDate() {
        return foundingDate;
    }

    public void setFoundingDate(String founding_date) {
        this.foundingDate = founding_date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKindOfSport() {
        return kindOfSport;
    }

    public void setKindOfSport(String kindOfSport) {
        this.kindOfSport = kindOfSport;
    }
}
