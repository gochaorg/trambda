package xyz.cofe.bc.xml.clss;

import java.util.List;

@Desc("sample User2")
public class User2 {
    public User2(){}
    public User2(String name1){
        this.name = name1;
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
