/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple;

import bangmaple.dao.UsersDAO;
import bangmaple.dto.UsersDTO;
import bangmaple.jdbc.dao.base.Store;
import bangmaple.jdbc.repository.JdbcRepository;

import java.util.Arrays;

/**
 *
 * @author bangmaple
 */
public class Main {

   public static void main(String[] args) throws Exception {
       JdbcRepository.DEBUG = false;
        UsersDAO dao = Store.select(UsersDAO.class);
       System.out.println(dao.findUsersByRole("Administrator"));
       System.out.println(dao.findAllByIds(Arrays.asList("admin", "mod", "beluga", "hecker", "eugene")));;
       dao.insert(new UsersDTO("ad", "ad", "Admin", "ad"));
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
        dao.deleteById("admin");
        System.out.println(dao.count());
        dao.deleteAll();
       dao.deleteAllByIds(Arrays.asList("1", "2", "3", "4"));
    }
    
}
