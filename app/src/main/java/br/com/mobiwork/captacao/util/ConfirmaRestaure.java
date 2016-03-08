package br.com.mobiwork.captacao.util;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import br.com.mobiwork.captacao.R;


public class ConfirmaRestaure extends Activity implements  View.OnClickListener {

    private EditText edTxSenha;
    private Alertas a;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.infosenha);

        // senha
        edTxSenha = (EditText) findViewById(R.id.senha);
        a= new Alertas(this);

        View.OnTouchListener otl = new View.OnTouchListener() {
            public boolean onTouch (View v, MotionEvent event) {
                return true;
            }
        };
        edTxSenha.setOnTouchListener(otl);
        configBt();
    }


    public void onClick(View v) {
        String keyInfo = (String)v.getTag();

        if (keyInfo.equals("back"))
            isBack();
        else if (keyInfo.equals("limpa"))
            edTxSenha.setText("");
        else if (keyInfo.equals("cancel")){
            Intent result = new Intent(this, ConfirmaRestaure.class);
            setResult(RESULT_CANCELED, result);
            ConfirmaRestaure.this.finish();
        }
        else if (keyInfo.equals("done")){
            Intent result = new Intent(this, ConfirmaRestaure.class);
            if(!edTxSenha.getText().toString().equals("123456")){
                a.AlertaSinc("Senha Incorreta");
                edTxSenha.setText("");
            }else{
                result.putExtra("senhaRestaure",edTxSenha.getText().toString());
                setResult(RESULT_OK, result);
                ConfirmaRestaure.this.finish();
            }

        }
        else if (!keyInfo.equals("")){
            edTxSenha.append(keyInfo);
        }

    }

    private void isBack() {
        CharSequence cc = edTxSenha.getText();
        if (cc != null && cc.length() > 0)
        {
            edTxSenha.setText("");
            edTxSenha.append(cc.subSequence(0, cc.length() - 1));
        }
    }

    public void configBt(){
        Button bt = (Button) findViewById(R.id.x1);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x2);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x3);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x4);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x5);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x6);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x7);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x8);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x9);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.x0);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xLimpa);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xDoneBt);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xCancelBt);
        bt.setOnClickListener(this);
        bt = (Button) findViewById(R.id.xPonto);
        bt.setOnClickListener(this);

    }

}