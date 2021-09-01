/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple;

import bangmaple.dao.UsersDAO;
import bangmaple.dto.UsersDTO;
import bangmaple.helper.Store;

import java.util.List;

/**
 *
 * @author bangmaple
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        UsersDAO dao = Store.select("bangmaple.dao.UsersDAO");
        System.out.println("Find by Id");
        UsersDTO dto = dao.findById("admin");
        System.out.println(dto);
        System.out.println("Find All");
        List<UsersDTO> list = dao.findAll();
        System.out.println(list);
        UsersDTO dto1 = new UsersDTO("mod", "mod", "Mode", "Moderator");
        dao.insert(dto1);
        System.out.println(dao.findById("mod"));
    }
    
}
