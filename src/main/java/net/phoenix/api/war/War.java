package net.phoenix.api.war;

import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class War {

    private List<String> warMembersUUID = new ArrayList<>();
    private String territoryName = "";
    private String territoryDefense = "";
    private TerritoryDefenses start;
    private TerritoryDefenses end;
    private long startTime;
    private long endTime;
    Pattern warBossBar = Pattern.compile("\\[([A-Z,a-z]{3,4})\\] ([A-Z,a-z ]*) - ❤ ([0-9]*) \\(([0-9,.,%]*)\\) - ☠ ([0-9]*)-([0-9]*) \\(([0-9,.]*)");


    public War(ClientBossBar bossBar){
        this.startTime = System.currentTimeMillis();
    }

    public void deserializeBossBar(ClientBossBar bossBar) {
    }



    public class TerritoryDefenses {
        public long health;
        public int attackLow;
        public int attackHigh;
        public float defense;
        public float atckSpeed;

        public TerritoryDefenses(long health, int attackLow, int attackHigh, float defense, float atckSpeed) {
            this.health = health;
            this.attackLow = attackLow;
            this.attackHigh = attackHigh;
            this.defense = defense;
            this.atckSpeed = atckSpeed;
        }

    }


}
