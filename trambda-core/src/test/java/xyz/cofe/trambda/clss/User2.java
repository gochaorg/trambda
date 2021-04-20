package xyz.cofe.trambda.clss;

import java.util.List;

public class User2 {
    public User2(){}
    public User2(String name){
        this.name = name;
    }

    @Desc("name of user")
    private String name;

    @Desc("name of user")
    public String getName(){ return name; }
    public void setName( @Required @MaxLength(100) @MinLength(1) String name ){ this.name = name; }

    private List<String> emails;

    @Desc("emails of user")
    public List<String> getEmails(){ return emails; }
    public void setEmails( List<String> emails ){ this.emails = emails; }
}
