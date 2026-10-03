package interfaces;

import java.util.List;
import modelo.Movimiento;

public interface IntMovimiento {
	
	public int registrarMovimiento(Movimiento m);
    public List<Movimiento> listarMovimientos();
    public int contarMovimientos();
}
