/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package motor;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Clase que carga el driver para conecarse a SQL en memoria, establece la
 * conexion, y modifica, inserta borrar registros.
 *
 * @author Cosmin Firsov
 */
public class Motor {

    Connection conexion;

    /**
     * Metodo que realiza una conexion con la BD.
     *
     * @throws ExceptionMotor
     */
    public void conectar() throws ExceptionMotor {
        try {
            Class.forName("org.sqlite.JDBC");

            conexion = DriverManager.getConnection("jdbc:sqlite:.\\BD\\motor.db");
        } catch (ClassNotFoundException ex) {
            ExceptionMotor exHR = new ExceptionMotor(-1, "Se ha producido un error al cargar el jdbc", "Error general del sistema. Consulte con el administrador.", null);
            throw exHR;
        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";
            ExceptionMotor exHR = new ExceptionMotor(-1, ex.getMessage(), mensajeErrorUsuario, null);
            throw exHR;
        }
    }

    /**
     * Método que inserta una registro en la tabla coche.
     *
     * @param cliente el coche a insertar
     * @return: numero de filas afectadas
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos.
     */
    public int insertarCliente(Cliente cliente) throws ExceptionMotor {
        conectar();
        String dml = "pragma foreign_keys = on;";
        int registrosAfectados = -1;

        try {
            dml += "insert into cliente (nombre, apellido1, apellido2, email, telefono, dni)"
                    + " values ( \'" + cliente.getNombre() + "\',\'" + cliente.getApellido1() + "\',\'" + cliente.getApellido2()
                    + "\',\'" + cliente.getEmail() + "\',\'" + cliente.getTelefono() + "\',\'" + cliente.getDni() + "\');";

            Statement sentencia = conexion.createStatement();
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();
        } catch (SQLException ex) {
            ExceptionMotor exMotor = new ExceptionMotor();
            exMotor.setCodigoErrorAdministrador(ex.getErrorCode());
            exMotor.setMensajeErrorAdministrador(ex.getMessage());
            exMotor.setSentenciaSQL(ex.getSQLState());

            exMotor.setMensajeErrorUsuario("Error en el sistema. Consulte con el administrador");
            if (ex.getMessage().contains("NO PUEDE ESTAR VACIO")) {
                exMotor.setMensajeErrorUsuario(ex.getMessage());
            }
            if (ex.getMessage().contains("is not unique")) {
                exMotor.setMensajeErrorUsuario("Los siguientes campos son únicos: email, telefono y dni");
            }
            if (ex.getMessage().contains("constraint failed")) {
                exMotor.setMensajeErrorUsuario("El campo telefono debe comenzar por 6 o 9");
            }
            throw exMotor;
        }

        return registrosAfectados;
    }

    /**
     * Método que elimina registros de una base de datos. Elimina un coche
     * buscandolo por su identificador de coche
     *
     * @param clienteId: el identificador del coche a eliminar
     * @return: el numero de filas afetadas
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public int eliminarClientes(int clienteId) throws ExceptionMotor {
        conectar();
        String dml = "pragma foreign_keys = on;";
        int registrosAfectados = -1;
        try {
            Statement sentencia = conexion.createStatement();

            dml += "delete from cliente where cliente_id = " + clienteId + ";";
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExceptionMotor exMotor = new ExceptionMotor();
            exMotor.setCodigoErrorAdministrador(ex.getErrorCode());
            exMotor.setMensajeErrorAdministrador(ex.getMessage());
            exMotor.setSentenciaSQL(ex.getSQLState());
            exMotor.setMensajeErrorUsuario("Error en el sistema. Consulte con el administrador");

            throw exMotor;
        }

        return registrosAfectados;
    }

    /**
     * Método que modifica los registros de un coche ya existente.
     *
     * @param cliente los datos del coche a modificar
     * @return: el numero de filas afectadas
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public int modificarCliente(Cliente cliente) throws ExceptionMotor {
        conectar();
        comprobarNulosCliente(cliente);
        String dml = "pragma foreign_keys = on;";
        int registrosAfectados = -1;
        try {
            Statement sentencia = conexion.createStatement();

            dml += " update cliente SET nombre=\'" + cliente.getNombre() + "\',apellido1=\'" + cliente.getApellido1() + "\',apellido2=\'" + cliente.getApellido2() + "\',email=\'" + cliente.getEmail() + "\',telefono=" + cliente.getTelefono() + ",dni=\'" + cliente.getDni() + "\' where cliente_id = " + cliente.getClienteId() + ";";
            registrosAfectados = sentencia.executeUpdate(dml);

            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExceptionMotor exMotor = new ExceptionMotor();
            exMotor.setCodigoErrorAdministrador(ex.getErrorCode());
            exMotor.setMensajeErrorAdministrador(ex.getMessage());
            exMotor.setSentenciaSQL(ex.getSQLState());

            exMotor.setMensajeErrorUsuario("Error en el sistema. Consulte con el administrador");
            if (ex.getMessage().contains("NO PUEDE ESTAR VACIO")) {
                exMotor.setMensajeErrorUsuario(ex.getMessage());
            }
            if (ex.getMessage().contains("is not unique")) {
                exMotor.setMensajeErrorUsuario("Los siguientes campos son únicos: email, telefono y dni");
            }
            if (ex.getMessage().contains("constraint failed")) {
                exMotor.setMensajeErrorUsuario("El campo telefono debe comenzar por 6 o 9");
            }
            throw exMotor;
        }

        return registrosAfectados;
    }

    /**
     * Método que ejecuta una select en una base de datos. Lee los datos de un
     * coche buscandolo por su identificador de coche. Se apoya en vistas
     *
     * @param clienteId el identificador de coche
     * @return devuelve el coche con cliente_id = clienteId
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public Cliente leerCliente(int clienteId) throws ExceptionMotor {
        conectar();
        String dql = "select * from VW_CLIENTE where cliente_id = " + clienteId;
        int registrosAfectados = -1;
        Cliente cliente;
        try {
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(dql);
            cliente = new Cliente();

            if (rs.next()) {
                cliente.setClienteId(rs.getInt("cliente_id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido1(rs.getString("apellido1"));
                cliente.setApellido2(rs.getString("apellido2"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDni(rs.getString("dni"));
            } else {
                return null;
            }

            rs.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";

            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dql);
            throw exHR;
        }

        return cliente;
    }

    /**
     * Método que ejecuta una select en una base de datos. Lee todos los
     * clientes que hay y los devuelve en un ArrayList. Se apoya en vistas
     *
     * @return un ArrayList de coche con todos los clientes
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public ArrayList<Cliente> leerClientes() throws ExceptionMotor {
        conectar();
        String dql = "select * from CLIENTE";
        int registrosAfectados = -1;
        ArrayList<Cliente> clientes = new ArrayList<>();
        try {
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(dql);
            Cliente cliente;
            while (rs.next()) {
                cliente = new Cliente();
                cliente.setClienteId(rs.getInt("cliente_id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido1(rs.getString("apellido1"));
                cliente.setApellido2(rs.getString("apellido2"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDni(rs.getString("dni"));
                clientes.add(cliente);
            }

            rs.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";

            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dql);
            throw exHR;
        }

        return clientes;
    }

    /**
     * Método que ejecuta una select en una base de datos. Lee todos los
     * clientes que hay atendiendo a un filtro y un orden y los devuelve en un
     * ArrayList. Se apoya en vistas
     *
     * @param filtro debe ser parte de una sentencia sql, formara parte de la
     * clausula where y en dicha clausula se podran incorporar la condicion que
     * se desee atendiendo a los nombres de cada campo que se quiera ver. ej:
     * cliente_id='7'
     * @param orden debe ser parte de una sentencia sql, formara parte de la
     * clausula order by y en dicha clausula se podran incorporar la condicion
     * que se desee para conseguir el orden deseado ej: order by nombre
     * @return un ArrayList de coche con todos los clientes
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public ArrayList<Cliente> leerClientes(String filtro, String orden) throws ExceptionMotor {
        conectar();
        String dql = "select * from VW_CLIENTE where " + filtro + "  order by " + orden;
        int registrosAfectados = -1;
        ArrayList<Cliente> clientes = new ArrayList<>();
        try {
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(dql);
            Cliente cliente;
            while (rs.next()) {
                cliente = new Cliente();
                cliente.setClienteId(rs.getInt("cliente_id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido1(rs.getString("apellido1"));
                cliente.setApellido2(rs.getString("apellido2"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setDni(rs.getString("dni"));
                clientes.add(cliente);
            }

            rs.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";

            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dql);
            throw exHR;
        }

        return clientes;
    }

    /**
     * Método que inserta un registro en la tabla coches. Usa procedimientos
     * almacenados en la BD
     *
     * @param coche El objeto coche
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public void insertarCoche(Coche coche) throws ExceptionMotor {
        conectar();
        comprobarNulosCoche(coche);
        String dml = "pragma foreign_keys = on;";
        try {
            dml += "insert into coche (marca, modelo, matricula, itv, cliente_id)"
                    + "  VALUES ('" + coche.getMarca() + "', '" + coche.getModelo() + "', '" + coche.getMatricula() + "', '" + coche.getItv() + "', " + coche.getCliente().getClienteId() + ");";
            Statement sentencia = conexion.createStatement();

            sentencia.executeUpdate(dml);
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExceptionMotor exMotor = new ExceptionMotor();
            exMotor.setCodigoErrorAdministrador(ex.getErrorCode());
            exMotor.setMensajeErrorAdministrador(ex.getMessage());
            exMotor.setSentenciaSQL(ex.getSQLState());

            exMotor.setMensajeErrorUsuario("Error en el sistema. Consulte con el administrador");
            if (ex.getMessage().contains("NO PUEDE ESTAR VACIO")) {
                exMotor.setMensajeErrorUsuario(ex.getMessage());
            }
            if (ex.getMessage().contains("is not unique")) {
                exMotor.setMensajeErrorUsuario("Los siguientes campos son únicos: matricula");
            }
            if (ex.getMessage().contains("EL COCHE TIENE QUE TENER PASADA LA ITV")) {
                exMotor.setMensajeErrorUsuario("EL COCHE TIENE QUE TENER PASADA LA ITV");
            }
            if (ex.getMessage().contains("foreign key")) {
                exMotor.setMensajeErrorUsuario("No existe ese identificador de usuario");
            }
            throw exMotor;
        }
    }

    /**
     * Método que elimina una fila de una base de datos. Elimina un coche
     * buscandolo por su identificador de coche. Usa procedimientos almacenados
     * en la BD
     *
     * @param cocheId el identificador del coche a eliminar
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public void eliminarCoche(int cocheId) throws ExceptionMotor {
        conectar();
        String dml = "";
        try {
            dml = "delete from coche where coche_id = " + cocheId + ";";
            Statement sentencia = conexion.createStatement();

            sentencia.executeUpdate(dml);;
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario;

            switch (ex.getErrorCode()) {
                default:
                    mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";
                    break;
            }
            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dml);
            throw exHR;
        }
    }

    /**
     * Método que modifica los datos de un coche ya existente. Usa
     * procedimientos almacenados en la BD
     *
     * @param idCocheViejo el identificador del coche a modificar
     * @param coche el objeto coche con los datos a modificar
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public void modificarCoche(Coche coche) throws ExceptionMotor {
        conectar();
        comprobarNulosCoche(coche);
        String dml = "pragma foreign_keys = on;";
        try {
            dml += " update coche SET marca = \'" + coche.getMarca() + "\', modelo = \'" + coche.getModelo() + "\', matricula = \'" + coche.getMatricula() + "\', itv = \'" + coche.getItv() + "\' " + " where coche_id=" + coche.getCocheId() + ";";
            System.out.println(dml);
            Statement sentencia = conexion.createStatement();

            sentencia.executeUpdate(dml);
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            ExceptionMotor exMotor = new ExceptionMotor();
            exMotor.setCodigoErrorAdministrador(ex.getErrorCode());
            exMotor.setMensajeErrorAdministrador(ex.getMessage());
            exMotor.setSentenciaSQL(ex.getSQLState());

            exMotor.setMensajeErrorUsuario("Error en el sistema. Consulte con el administrador");
            if (ex.getMessage().contains("NO PUEDE ESTAR VACIO")) {
                exMotor.setMensajeErrorUsuario(ex.getMessage());
            }
            if (ex.getMessage().contains("is not unique")) {
                exMotor.setMensajeErrorUsuario("Los siguientes campos son únicos: matricula");
            }
            if (ex.getMessage().contains("EL COCHE TIENE QUE TENER PASADA LA ITV")) {
                exMotor.setMensajeErrorUsuario("EL COCHE TIENE QUE TENER PASADA LA ITV");
            }
            if (ex.getMessage().contains("foreign key")) {
                exMotor.setMensajeErrorUsuario("No existe ese identificador de usuario");
            }
            throw exMotor;
        }

    }

    /**
     * Metodo que lee un coche buscandolo por su identificador de coche. Se
     * apoya en vistas
     *
     * @param cocheId el identificador del coche
     * @return un coche Coche
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public Coche leerCoche(int cocheId) throws ExceptionMotor {
        conectar();
        String dql = "select * from VW_COCHE c where c.coche_id=" + cocheId;
        int registrosAfectados = -1;
        Coche coche = new Coche();
        try {
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(dql);

            while (rs.next()) {
                coche.setCocheId(rs.getInt("coche_id"));
                coche.setMarca(rs.getString("marca"));
                coche.setMatricula(rs.getString("matricula"));
                coche.setModelo(rs.getString("modelo"));
                
                
                
                
                coche.setItv(java.sql.Date.valueOf(rs.getString("itv")));
                
                ;

                coche.setCliente(new Cliente());
                coche.getCliente().setClienteId(rs.getInt("cliente_id"));
                coche.getCliente().setNombre(rs.getString("nombre"));
                coche.getCliente().setApellido1(rs.getString("apellido1"));
                coche.getCliente().setApellido2(rs.getString("apellido2"));
                coche.getCliente().setEmail(rs.getString("email"));
                coche.getCliente().setTelefono(rs.getString("telefono"));
                coche.getCliente().setDni(rs.getString("dni"));
            }

            rs.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";

            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dql);
            throw exHR;
        }

        return coche;
    }

    /**
     * Metodo que devuelve todos los coches con sus respectivos clientes
     *
     * @return todos los coches
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public ArrayList<Coche> leerCoches() throws ExceptionMotor {
        conectar();
        String dql = "select * from VW_COCHE";
        int registrosAfectados = -1;
        ArrayList<Coche> coches = new ArrayList<>();
        try {
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(dql);

            Coche coche;
            while (rs.next()) {
                coche = new Coche();
                coche.setCocheId(rs.getInt("coche_id"));
                coche.setMarca(rs.getString("marca"));
                coche.setMatricula(rs.getString("matricula"));
                coche.setModelo(rs.getString("modelo"));
                coche.setItv(java.sql.Date.valueOf(rs.getString("itv")));

                coche.setCliente(new Cliente());
                //coche.getCliente().setClienteId(rs.getInt("cliente_id"));
                coche.getCliente().setNombre(rs.getString("nombre"));
                coche.getCliente().setApellido1(rs.getString("apellido1"));
                coche.getCliente().setApellido2(rs.getString("apellido2"));
                coche.getCliente().setEmail(rs.getString("email"));
                coche.getCliente().setTelefono(rs.getString("telefono"));
                coche.getCliente().setDni(rs.getString("dni"));

                coches.add(coche);
            }

            rs.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";

            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dql);
            throw exHR;
        }

        return coches;
    }

    /**
     * Método que ejecuta una select en una base de datos. Lee todos los coches
     * que hay atendiendo a un filtro y un orden y los devuelve en un ArrayList.
     *
     * @param filtro debe ser parte de una sentencia sql, formara parte de la
     * clausula where y en dicha clausula se podran incorporar la condicion que
     * se desee atendiendo a los nombres de cada campo que se quiera ver. ej:
     * matricula='1234AAA'
     * @param orden debe ser parte de una sentencia sql, formara parte de la
     * clausula order by y en dicha clausula se podran incorporar la condicion
     * que se desee para conseguir el orden deseado ej: order by marca
     * @return un ArrayList de coche con todos los coches
     * @throws ExceptionMotor excepcion que integra el mensaje de error al
     * usuario, el codigo de error y el mensaje de error que nos ha devuelto la
     * base de datos
     */
    public ArrayList<Coche> leerCoches(String filtro, String orden) throws ExceptionMotor {
        conectar();
        String dql = "select * from VW_COCHE where " + filtro + " order by " + orden;
        int registrosAfectados = -1;
        ArrayList<Coche> coches = new ArrayList<>();
        try {
            Statement sentencia = conexion.createStatement();
            ResultSet rs = sentencia.executeQuery(dql);

            Coche coche;
            while (rs.next()) {
                coche = new Coche();
                coche.setCocheId(rs.getInt("coche_id"));
                coche.setMarca(rs.getString("marca"));
                coche.setMatricula(rs.getString("matricula"));
                coche.setModelo(rs.getString("modelo"));
                coche.setItv(java.sql.Date.valueOf(rs.getString("itv")));

                coche.setCliente(new Cliente());
                //coche.getCliente().setClienteId(rs.getInt("cliente_id"));
                coche.getCliente().setNombre(rs.getString("nombre"));
                coche.getCliente().setApellido1(rs.getString("apellido1"));
                coche.getCliente().setApellido2(rs.getString("apellido2"));
                coche.getCliente().setEmail(rs.getString("email"));
                coche.getCliente().setTelefono(rs.getString("telefono"));
                coche.getCliente().setDni(rs.getString("dni"));

                coches.add(coche);
            }

            rs.close();
            sentencia.close();
            conexion.close();

        } catch (SQLException ex) {
            String mensajeErrorUsuario = "Error en el sistema. Consulta con el administrador";

            ExceptionMotor exHR = new ExceptionMotor(ex.getErrorCode(), ex.getMessage(), mensajeErrorUsuario, dql);
            throw exHR;
        }

        return coches;
    }

    private void comprobarNulosCliente(Cliente cliente) throws ExceptionMotor {
        if (cliente != null) {
            if (cliente.getNombre() == null || cliente.getApellido1() == null || cliente.getDni() == null || cliente.getEmail() == null || cliente.getTelefono() == null) {
                throw new ExceptionMotor(0, "Verificacion campos nulos", "Los siguientes campos son obligatorios: nombre, apellido1, dni, email y telefono", "");
            }
        }
    }

    private void comprobarNulosCoche(Coche coche) throws ExceptionMotor {
        if (coche != null) {
            if (coche.getCliente() == null || coche.getItv() == null || coche.getMarca() == null || coche.getMatricula() == null || coche.getMatricula() == null || coche.getModelo() == null) {
                throw new ExceptionMotor(0, "Verificacion campos nulos", "Los siguientes campos son obligatorios: marca, matricula, modelo, itv y cliente", "");
            }
        }
    }

}
