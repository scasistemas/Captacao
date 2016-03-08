package br.com.mobiwork.captacao.util;

import java.math.BigDecimal;

/**
 * Created by LuisGustavo on 28/05/2015.
 */
public class BigDecimalRound {

    public static double Round(double valor){
        return Round(valor,4);
    }

    public static double Round(double valor, int scale){
        double resultado=0;
        if(valor>0) {

            BigDecimal bd = new BigDecimal(valor);
            BigDecimal rounded = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
            resultado = rounded.doubleValue();
            if (resultado > 1000000)
                resultado = 1000000;
        }
        return resultado;
    }

    public static double arredondar(double valor, int casas, int ceilOrFloor) {
        double arredondado = valor;
        arredondado = (Math.pow(10, casas));
        if (ceilOrFloor == 0) {
            arredondado = Math.ceil(arredondado);
        } else {
            arredondado = Math.floor(arredondado);
        }
        arredondado /= (Math.pow(10, casas));
        return arredondado;
    }
}
