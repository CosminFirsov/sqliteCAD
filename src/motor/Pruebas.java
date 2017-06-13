/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package motor;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Oracle
 */
public class Pruebas {

    public static void main(String[] args) {
        Motor motor = new Motor();
        try {
            //motor.insertarCliente(new Cliente(1, "Cosmin", "Firsov", "", "bbbb", "6aa", "bbbb"));
            //motor.modificarCliente(new Cliente(11,"Cosmin","kk"," ","kker","9","kkik"));
            //motor.eliminarClientes(9);
            //motor.insertarCoche(new Coche(1,"kk","kkkk","kkkk",new Date(System.currentTimeMillis()+100000000),new Cliente(5,"kk","kk","","kker",null,"kkik")));
            //motor.modificarCoche(new Coche(8,"cer","cerer","cerer",new Date(System.currentTimeMillis()+100000000),new Cliente(1,"kkkkkkk","kkkkkkkkk","","kkkkkkkkker","600","kkkkkkkkkik")));
            //motor.eliminarCoche(8);
            /*
            Cliente cliente = motor.leerCliente(5);
            System.out.printf("%4s %20s %20s %20s %20s %9s %9s \n", "ID", "Nombre cliente", "Primer apellido", "Segundo apellido",
                    "Email", "Telefono", "DNI");
            System.out.printf("%4d %20s %20s %20s %20s %9s %9s \n",cliente.getClienteId(), cliente.getNombre(), cliente.getApellido1(), cliente.getApellido2(),
                        cliente.getEmail(), cliente.getTelefono(), cliente.getDni());
            */
            
            /*
            Coche coche = motor.leerCoche(9);
            
            System.out.printf("%4s %15s %15s %7s %10s %20s %20s %9s \n", "ID", "Marca", "Modelo", "Matricula", "ITV", "Nombre cliente", "Primer apellido",
                                            "DNI");
            
            System.out.printf("%4d %15s %15s %7s %tF %20s %20s %9s \n", coche.getCocheId(),
                        coche.getMarca(), coche.getModelo(), coche.getMatricula(),
                        coche.getItv(), coche.getCliente().getNombre(),
                        coche.getCliente().getApellido1(), coche.getCliente().getDni());
            */
            
            
            
            
            
            
            
            ArrayList<Coche> coches = motor.leerCoches();

            System.out.printf("%4s %15s %15s %7s %10s %20s %20s %9s \n", "ID", "Marca", "Modelo", "Matricula", "ITV", "Nombre cliente", "Primer apellido",
                                            "DNI");
            while (!coches.isEmpty()) {
                System.out.printf("%4d %15s %15s %7s %tF %20s %20s %9s \n", coches.get(0).getCocheId(),
                        coches.get(0).getMarca(), coches.get(0).getModelo(), coches.get(0).getMatricula(),
                        coches.get(0).getItv(), coches.get(0).getCliente().getNombre(),
                        coches.get(0).getCliente().getApellido1(), coches.get(0).getCliente().getDni());
                coches.remove(0);
            }
            /*
            ArrayList<Cliente> clientes = motor.leerClientes();
            System.out.printf("%4s %20s %20s %20s %20s %9s %9s \n", "ID", "Nombre cliente", "Primer apellido", "Segundo apellido",
                    "Email", "Telefono", "DNI");
            while (!clientes.isEmpty()) {
                System.out.printf("%4d %20s %20s %20s %20s %9s %9s \n",clientes.get(0).getClienteId(), clientes.get(0).getNombre(), clientes.get(0).getApellido1(), clientes.get(0).getApellido2(),
                        clientes.get(0).getEmail(), clientes.get(0).getTelefono(), clientes.get(0).getDni());
                clientes.remove(0);
            }*/
        } catch (ExceptionMotor ex) {
            System.out.println(ex.getMensajeErrorUsuario());
        }/* catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }*/
    }
}
