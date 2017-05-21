package com.example.jcala.ej6;

import android.app.DatePickerDialog;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jcala.practica6.R;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private int anio;
    private int mes;
    private int dia;
    private double IMC;
    private boolean calculado = false;
    private boolean cerrojo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText campoAltura  = (EditText) findViewById(R.id.editAltura);
        final EditText campoPeso  = (EditText) findViewById(R.id.editPeso);
        final TextView resultadoIMC = (TextView) findViewById(R.id.resultadoIMC);
        final TextView resultadoIMCTexto = (TextView) findViewById(R.id.resultadoIMCTexto);
        Button calculoIMC = (Button) findViewById(R.id.calculoIMC);
        final TextView fechaNacimiento = (TextView) findViewById(R.id.FechaDeNacimiento);
        final TextView resultadoIGCE = (TextView) findViewById(R.id.resultadoIGCE);
        final Button calculoIGC = (Button) findViewById(R.id.calculoIGCE);
        final RadioGroup grupoRadio = (RadioGroup) findViewById(R.id.RadioGroup);

        grupoRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(calculado && comprovaciones()){
                    double IGCE = 0;
                    int sexo = 0;
                    Calendar mcurrentDate = Calendar.getInstance();
                    int edad  = mcurrentDate.get(Calendar.YEAR) - anio;
                    int selectedId  = grupoRadio.getCheckedRadioButtonId();

                    sexo = getSexo(selectedId);
                    IGCE = calcularIGCE(IMC,(double)edad,(double)sexo);
                    String texto = String.format("%2.2f %%",IGCE);
                    resultadoIGCE.setText(texto);
                }
            }

        });



        calculoIMC.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                    try {
                        double peso = Double.valueOf(campoPeso.getText().toString());
                        double altura = Double.valueOf(campoAltura.getText().toString()) / 100;
                        IMC = peso / (altura * altura);
                        String texto = String.format("%2.2f",IMC);
                        resultadoIMC.setText(texto);
                        if (IMC < 16.00) {
                            resultadoIMCTexto.setText("Delgadez Severa");
                        } else if (IMC >= 16.00 && IMC <= 16.99) {
                            resultadoIMCTexto.setText("Delgadez Moderada");
                        } else if (IMC >= 17.00 && IMC <= 18.49) {
                            resultadoIMCTexto.setText("Delgadez no muy pronunciada");
                        } else if (IMC >= 18.5 && IMC <= 24.99) {
                            resultadoIMCTexto.setText("Normal");
                        } else if (IMC >= 25.00 && IMC <= 29.99) {
                            resultadoIMCTexto.setText("Preobeso");
                        } else if (IMC >= 30.00 && IMC <= 34.99) {
                            resultadoIMCTexto.setText("Obeso tipo I");
                        } else if (IMC >= 35.00 && IMC <= 39.99) {
                            resultadoIMCTexto.setText("Obeso tipo II");
                        } else if (IMC >= 40.00) {
                            resultadoIMCTexto.setText("Obeso tipo III");
                        }
                    } catch (Exception e) {
                        Log.e("Error de conversion", "Error " + e.toString());
                        Toast.makeText(getApplication(), R.string.errorDatos, Toast.LENGTH_SHORT).show();
                    }
            }
        });

        fechaNacimiento.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                //Ponemos la fecha Actual
                final Calendar mcurrentDate = Calendar.getInstance();
                anio = mcurrentDate.get(Calendar.YEAR);
                mes = mcurrentDate.get(Calendar.MONTH);
                dia = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        anio = selectedyear;
                        mes = selectedmonth+1;
                        dia = selectedday;
                        Toast.makeText(getApplication(),"Año "+anio+" mes "+mes+" dia "+dia,Toast.LENGTH_LONG).show();
                        if(calculado && comprovaciones()) {
                            double IGCE = 0;
                            int sexo = 0;
                            int edad = mcurrentDate.get(Calendar.YEAR) - anio;
                            int selectedId = grupoRadio.getCheckedRadioButtonId();

                            sexo = getSexo(selectedId);
                            IGCE = calcularIGCE(IMC, (double) edad, (double) sexo);
                            String texto = String.format("%2.2f %%", IGCE);
                            resultadoIGCE.setText(texto);

                        }
                    }
                }, anio, mes, dia);
                mDatePicker.setTitle("Elige la fecha de nacimiento");
                mDatePicker.show();

            }
        });

        calculoIGC.setOnClickListener(new View.OnClickListener(){

            @Override
                public void onClick(View v){
                    double IGCE = 0;
                    int sexo = 0;
                    Calendar mcurrentDate = Calendar.getInstance();
                    int edad  = mcurrentDate.get(Calendar.YEAR) - anio;
                    int selectedId  = grupoRadio.getCheckedRadioButtonId();

                    //Comprovaciones
                    if(!comprovaciones()){
                        return;
                    }
                    sexo = getSexo(selectedId);
                    if(sexo == -1)
                        return;
                    IGCE = calcularIGCE(IMC,(double)edad,(double)sexo);
                    String texto = String.format("%2.2f %%",IGCE);
                    resultadoIGCE.setText(texto);
                    calculado = true;
                }
        });
    }

    private boolean comprovaciones(){
        Calendar mcurrentDate = Calendar.getInstance();
        if(IMC == 0){
            Toast.makeText(getApplication(),R.string.errorIMC,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dia == 0){
            Toast.makeText(getApplication(),R.string.errorFecha2,Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dia > mcurrentDate.get(Calendar.DAY_OF_MONTH)){
            if(mes >= mcurrentDate.get(Calendar.MONTH)){
                if(anio >= mcurrentDate.get(Calendar.YEAR)){
                    Toast.makeText(getApplication(),R.string.errorFecha,Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    private int getSexo(int selectedId){
        switch (selectedId){
            case -1:
                Toast.makeText(getApplication(),R.string.errorSexo,Toast.LENGTH_SHORT).show();
                return -1;
            case 0:  return 1;

            case 1: return 0;
            default:
                    return -1;
        }
    }

    private double calcularIGCE(double IMC, double edad, double sexo){
        double IGCE = 0;
        if(edad < 16){
            IGCE = 1.51*IMC - 0.7*edad - 3.6*sexo+1.4;
        }else{
            IGCE = 1.20*IMC + 0.23*edad - 10.8*sexo-5.4;
        }
        return IGCE;
    }


}
