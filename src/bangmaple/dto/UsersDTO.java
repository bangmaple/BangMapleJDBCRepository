/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.dto;

import bangmaple.helper.annotations.Column;
import bangmaple.helper.annotations.Id;
import bangmaple.helper.annotations.Table;

/**
 *
 * @author bangmaple
 */
@Table(name = "users")
public class UsersDTO {

    @Id
    @Column(value = "username")
    private String username;

    @Column(value = "password")
    private String password;

    @Column(value = "fullname")
    private String fullname;

    @Column(value = "role")
    private String role;

    public UsersDTO() {
    }

    public UsersDTO(String username, String password, String fullname, String role) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UsersDTO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fullname='" + fullname + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
