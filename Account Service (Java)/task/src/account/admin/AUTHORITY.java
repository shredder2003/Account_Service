package account.admin;

public enum AUTHORITY {
         ADMINISTRATOR
        ,ACCOUNTANT
        ,USER;

    public String roleName(){
        return "ROLE_"+this.name();
    }
}
