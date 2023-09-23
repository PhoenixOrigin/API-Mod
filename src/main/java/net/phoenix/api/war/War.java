package net.phoenix.api.war;

import java.util.ArrayList;
import java.util.List;

public class War {

    private List<String> warMembersUUID = new ArrayList<>();
    private String territoryName = "";
    private String territoryDefense = "";

    public class TerritoryDefenses {
        public long health;
        public int attack1;
        public int attack2;
        public float defense;

    }


}
