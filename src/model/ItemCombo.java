/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Eric
 */
public class ItemCombo {

    private final int valor;
    private final String texto;

    public ItemCombo(int valor, String texto) {
        this.valor = valor;
        this.texto = texto;
    }

    public int getValor() {
        return valor;
    }

    public String getTexto() {
        return texto;
    }

    @Override
    public String toString() {
        return texto;
    }
}
