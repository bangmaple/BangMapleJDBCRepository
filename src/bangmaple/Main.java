/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple;

import bangmaple.dao.UsersDAO;
import bangmaple.dto.UsersDTO;
import bangmaple.helper.Store;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author bangmaple
 */
public class Main {

   public static void main(String[] args) throws Exception {
        UsersDAO dao = Store.select(UsersDAO.class);
      //  dao.insert(new UsersDTO("ad", "ad", "Admin", "ad"));
       dao.insertAll(Arrays.asList(
                new UsersDTO("admin", "admin", "Admin", "Administrator"),
                new UsersDTO("user", "user", "User", "User"),
                new UsersDTO("mod", "mod", "Mod", "Moderator"),
                new UsersDTO("beluga", "beluga", "Beluga", "Moderator"),
                new UsersDTO("hecker", "hecker", "Hecker", "Administrator"),
                new UsersDTO("skittle", "skittle", "Skittle", "Moderator"),
                new UsersDTO("eugene", "eugene", "Eugene", "Users")
        ));
        System.out.println(dao.findAll());
        System.out.println(dao.findById("user"));
        System.out.println(dao.count());
        System.out.println(dao.existsById("user"));
        UsersDTO dto = new UsersDTO("admin", "admin", "Administrator", "Admin");
        dao.update(dto, "admin");
       // dao.deleteById("admin");
        System.out.println(dao.count());
        // dao.deleteAll();
    }
    
}
