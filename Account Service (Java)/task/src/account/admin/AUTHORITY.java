package account.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum AUTHORITY {
         ADMINISTRATOR(ROLE_TYPE.ADMIN)
        ,ACCOUNTANT(ROLE_TYPE.BUSINESS)
        ,USER(ROLE_TYPE.BUSINESS);

    private final ROLE_TYPE roleType;

    private static final Map<String, AUTHORITY> nameToValueMap =
            new HashMap<String, AUTHORITY>();

    static {
        for (AUTHORITY value : EnumSet.allOf(AUTHORITY.class)) {
            nameToValueMap.put(value.name(), value);
        }
    }

    public static AUTHORITY forName(String name) {
        return nameToValueMap.get(name);
    }

    public String roleName(){
        return "ROLE_"+this.name();
    }
}
