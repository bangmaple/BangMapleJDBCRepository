/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bangmaple.dto;

import bangmaple.jdbc.annotations.Column;
import bangmaple.jdbc.annotations.Id;
import bangmaple.jdbc.annotations.Table;

/**
 *
 * @author bangmaple
 */
@Table(name = "equipments", catalog = "LAB231_1")
public class EquipmentsDTO {

    @Id
    @Column(value = "equipment_id")
    private int equipmentId;

    @Column(value = "equipment_name")
    private String equipmentName;

    @Column(value = "equipment_color")
    private String equipmentColor;

    @Column(value = "equipment_quantity")
    private int equipmentQuantity;

    @Column(value="equipment_category_id")
    private int equipmentCategoryId;

    public EquipmentsDTO() {
    }

    public EquipmentsDTO(int equipmentId, String equipmentName, String equipmentColor, int equipmentQuantity, int equipmentCategoryId) {
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.equipmentColor = equipmentColor;
        this.equipmentQuantity = equipmentQuantity;
        this.equipmentCategoryId = equipmentCategoryId;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentColor() {
        return equipmentColor;
    }

    public void setEquipmentColor(String equipmentColor) {
        this.equipmentColor = equipmentColor;
    }

    public int getEquipmentQuantity() {
        return equipmentQuantity;
    }

    public void setEquipmentQuantity(int equipmentQuantity) {
        this.equipmentQuantity = equipmentQuantity;
    }

    public int getEquipmentCategoryId() {
        return equipmentCategoryId;
    }

    public void setEquipmentCategoryId(int equipmentCategoryId) {
        this.equipmentCategoryId = equipmentCategoryId;
    }

    @Override
    public String toString() {
        return "EquipmentsDTO{" +
                "equipmentId=" + equipmentId +
                ", equipmentName='" + equipmentName + '\'' +
                ", equipmentColor='" + equipmentColor + '\'' +
                ", equipmentQuantity=" + equipmentQuantity +
                ", equipmentCategoryId=" + equipmentCategoryId +
                '}';
    }
}
