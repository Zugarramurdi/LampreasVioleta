package dto;
import model.Cliente;
import model.DetalleCliente;

public class ClienteDetalleDTO {

    private int id;
    private String nombre;
    private String email;

    private String direccion;
    private String telefono;
    private String notas;


    public ClienteDetalleDTO(){}

    public ClienteDetalleDTO(int id, String nombre, String email, String direccion, String telefono, String notas) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.notas = notas;
    }

    public static ClienteDetalleDTO from(Cliente c, DetalleCliente d) {
        String direccion = null;
        String telefono = null;
        String notas = null;

        if(d != null){
            direccion=d.getDireccion();
            telefono=d.getTelefono();
            notas=d.getNotas();
        }
        return new ClienteDetalleDTO(c.getId(),c.getNombre(),c.getEmail(),direccion,telefono,notas);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }
}
