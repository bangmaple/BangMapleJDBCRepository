/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.dao;


import bangmaple.dto.UsersDTO;
import bangmaple.jdbc.utils.ConnectionManager;
import bangmaple.jdbc.dao.base.Store;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author bangmaple
 */
public class UsersDAO extends Store<UsersDTO, String> {

    private UsersDAO() {}

    public List<UsersDTO> findUsersByRole(String role) throws SQLException {
        List<UsersDTO> list = null;
        conn = ConnectionManager.getConnection();
        try {
            if (conn != null) {
                conn.setCatalog("users_management");
                String query = "SELECT username, password, fullname, role FROM users WHERE role = ?";
                prStm = conn.prepareStatement(query);
                prStm.setString(1, role);
                rs = prStm.executeQuery();
                list = new ArrayList<>();
                while (rs.next()) {
                    UsersDTO dto = new UsersDTO();
                    dto.setUsername(rs.getString("username"));
                    dto.setPassword(rs.getString("password"));
                    dto.setFullname(rs.getString("fullname"));
                    dto.setRole(rs.getString("role"));
                    list.add(dto);
                }
            }
        } finally {
            closeConnection();
        }
        return list;
    }

}
