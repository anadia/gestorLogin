package com.anadiait.GestorLogin;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GestorLoginTest {
  GestorLogin login; //SUT
  IRepositorioCuentas repo; //Collaborator
  ICuenta cuenta; //collaborator

  @Before
  public void setUp() throws Exception {
    repo = mock(IRepositorioCuentas.class);
    cuenta = mock(ICuenta.class);
    when (repo.buscar("pepe")).thenReturn(cuenta); 
    login = new GestorLogin(repo);
  }

  @After
  public void tearDown() throws Exception {
      repo = null;
      cuenta = null;
      login = null;
  }
  
  @Test
  public void testAccessoConcedidoALaPrimera() {
    when (cuenta.claveCorrecta("1234")).thenReturn(true);
    
    login.acceder("pepe", "1234");
        
        verify(cuenta, times(1)).entrarCuenta();
        verify(cuenta, never()).bloquearCuenta();
  }
  
  @Test
  public void siFallaPrimeraVez_AccesoNoConcedido_NoBloqueada() {
    when (cuenta.claveCorrecta("1235")).thenReturn(false);
    
    login.acceder("pepe", "1235");
        
        verify(cuenta, never()).entrarCuenta();
        verify(cuenta, never()).bloquearCuenta();
        assertThat(login.getNumFallos(), is(1));
 
  }
  
  @Test
  public void siUsuarioDesconocidoExcepcion() {
    when (repo.buscar("manolo")).thenThrow(ExcepcionUsuarioDesconocido.class);
    
    
    try {
      login.acceder("manolo", anyString());
      fail("Debe lanzar excepcion");
    } catch (ExcepcionUsuarioDesconocido e) {
       verify(repo).buscar(anyString());
    }
        
        
  } 

  @Test
  public void siCuatroFallos_CuentaBloqueada() {
    when(cuenta.claveCorrecta("1235")).thenReturn(false);
    
    login.acceder("pepe",  "1235");
    login.acceder("pepe",  "1235");
    login.acceder("pepe",  "1235");
    login.acceder("pepe",  "1235");
    
    verify(cuenta, atLeastOnce()).bloquearCuenta();
    assertThat(login.getNumFallos(), is(3)); 
    
    
  }
  @Test
  public void siEstaBloqueada_SEDeniegaAcceso() {
    when(cuenta.estaBloqueada()).thenReturn(true);
    
    login.acceder("pepe",  "1234");
    
    verify(cuenta, never()).entrarCuenta();
    
    
  }
}
