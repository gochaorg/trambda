package xyz.cofe.trambda;

import java.util.ArrayList;
import java.util.List;
import xyz.cofe.iter.Eterable;

public class EnvLocal implements IEnv {
    private List<User> users;
    {
        users = new ArrayList<>();
        var lastNames = List.of("Petrov","Sidorov","Ivanov");
        var firstNames = List.of("Boris","Ivan","Egor");
        for( var ln : lastNames ){
            for( var fn : firstNames ){
                users.add(new User(
                    fn+" "+ln, fn.toLowerCase()+"-"+ln.toLowerCase()+"@gmail.com"
                ));
            }
        }
    }
    @Override
    public Eterable<User> getUsers(){
        return Eterable.of(users);
    }
}
