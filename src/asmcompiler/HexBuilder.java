/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package asmcompiler;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ivan_
 */
public class HexBuilder 
{
    private ArrayList<ASMCommand> commands;
    private Vector cabeceras;
    
    public Vector getCabeceras()
    {
        return this.cabeceras;
    }
        
    public HexBuilder(ArrayList<ASMCommand> commands)
    {
        this.commands = commands;
    }
    
    private String[][] getHexTable()
    {
        String[][] cells = new String[16][17];
        for (int i = 0 ; i < 16; i++) 
        {
            for (int j = 0; j < 17; j++) 
            {
                if(j==0)
                {
                    cells[i][j] = "0"+Integer.toHexString(i).toUpperCase();
                }
                else 
                {
                    cells[i][j] = "00";
                }
            }
        }
        Integer IC = 0;
        for (int i = 0; i < commands.size(); i++) 
        {
            ASMCommand comm = commands.get(i);
            IC += comm.getOffset();
            for (int j = 0; j < comm.getSize() ; j++) 
            {
                Integer[] pos = getPosition(IC);
                cells[pos[0]][pos[1]] = comm.getHexByte(j);
                IC++;
            }
        }
        return cells;
    }
    
    private Integer[] getPosition(int value)
    {
        Integer[] pos = new Integer[2];
        pos[0] = value / 16;
        pos[1] = value % 16 + 1;
        return pos;
    }

    public DefaultTableModel getTable() {
        cabeceras = new Vector();
        cabeceras.addElement(" ");
        for (int i = 0; i < 16; i++) 
        {
            cabeceras.addElement(Integer.toHexString(i).toUpperCase());
        }

        DefaultTableModel modelo_tabla = new DefaultTableModel(cabeceras, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
        };

        try 
        {
            String[][] cells = getHexTable();
            for (int i = 0; i < cells.length; i++) 
            {
                modelo_tabla.addRow(cells[i]);
            }
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, e);
        }
        return modelo_tabla;
    }
    
}
