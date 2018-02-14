package net.obstfelder.celleye.models;

import java.util.List;

/**
 * Created by obsjoa on 13.02.2017.
 */
public class User
{
    public Integer id;
    public String name;
    public String username;
    public String firstName;
    public String lastName;
    public String role;
    public String organizationId;
    public String lastLoggedInOrgId;

    public User(){}

    public User(Integer id, String name, String username,String organizationId)
    {
        this.id = id;
        this.name = name;
        this.username = username;
        this.organizationId = organizationId;
    }
}
