package com.anadiait.GestorLogin;

public class GestorLogin {

	private IRepositorioCuentas repo;
	private int numFallos;
	private static final int MAX_FALLOS = 3;
	private String usuarioAnterior = ""; 

	public GestorLogin(IRepositorioCuentas repo) {
		this.repo = repo;
		setNumFallos(0);
	}

	public void acceder(String usuario, String clave) {
		ICuenta cuenta = repo.buscar(usuario); 
		
		if (cuenta == null)
			throw new ExcepcionUsuarioDesconocido(); 
		
		if (cuenta.estaEnUso())
			throw new ExcepcionCuentaEnUso();

		if (! cuenta.estaBloqueada()) {

			if (cuenta.claveCorrecta(clave)) {
				cuenta.entrarCuenta();
			}
			else {
				if (! usuario.equals(usuarioAnterior)) {
					setNumFallos(0);
					usuarioAnterior = usuario;
				}
				setNumFallos(getNumFallos() + 1);
				if (getNumFallos() >= MAX_FALLOS) {
					cuenta.bloquearCuenta(); 
				}
			}
		}
	}

  public int getNumFallos() {
    return numFallos;
  }

  public void setNumFallos(int numFallos) {
    this.numFallos = numFallos;
  }

}
