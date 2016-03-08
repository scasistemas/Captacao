package br.com.mobiwork.captacao.util;

/**
 * Created by LuisGustavo on 28/01/2016.
 */

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;

        import android.app.Activity;
        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;

        import android.util.Log;
        import android.widget.Toast;

        import br.com.mobiwork.captacao.R;
        import br.com.mobiwork.captacao.dao.DaoFazenda;
        import br.com.mobiwork.captacao.dao.DaoMateria;
        import br.com.mobiwork.captacao.dao.DaoProdutor;
        import br.com.mobiwork.captacao.model.Lancamentos;
        import br.com.mobiwork.captacao.model.Motorista;

public class ArquivoTxt   {

    //private static final String TAG = get.class.getName();
    private  String FILENAME ;
    private Context ctx;
    private Motorista config;

    /** Called when the activity is first created. */

    public ArquivoTxt(Context ctx,String filename,Motorista config){
        this.ctx=ctx;
        this.FILENAME=filename;
        this.config=config;

    }

    public File createFileTxt() throws IOException {
        // Get the directory for the user's public pictures directory.
        File file = new File(FolderConfig.getExternalStorageDirectoryTempTxt(), FILENAME);
        if (!file.exists()) {
            file.createNewFile();
            Log.e("CAPTACAO", "Directory not created");
        }
        // ***Maneira Antiga
        // FileOutputStream stream = new FileOutputStream(file);
        // stream.write(data.getBytes());
        // stream.close();
        return file;
    }

    public File writeFileformatTeste(File file) throws IOException {
        FileWriter arq = null;
        String empresa="";
        arq = new FileWriter(file);
        //A porcentagem siginicia a variavel que será substituida pelos parametros, por ordem ...
        //O numero na frente da letra do parametro define a quantidade de espaço de um para outro
        //gravarArq.printf("| %d X %d = %2d %20d |%n", i, n, (i*n),0);
        DataHoraPedido dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        if(this.config.getEmp().equalsIgnoreCase("vimilk")){
            empresa="Vimilk";
        }
        if(this.config.getEmp().equalsIgnoreCase("bdest")){
            empresa="Bom Destino";
        }
        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.printf("--- Pagina de Teste Captacao --- %n");
        gravarArq.printf("  Empresa: "+empresa+ " %n",2);
        gravarArq.printf("  Data: %2d/%d/%d %n",dataHoraMS.getDia(),dataHoraMS.getMes(),dataHoraMS.getAno());
        gravarArq.printf("  Hora: %2d:%d:%d  %n" ,dataHoraMS.getHora(),dataHoraMS.getMinuto(),dataHoraMS.getSegundo());
        gravarArq.printf("  SCA SISTEMAS %n " );
        gravarArq.printf("%n " );
        gravarArq.printf("%n " );

        arq.close();
        return file;

    }
    public void defineImp(File file,Lancamentos l,SQLiteDatabase db,boolean pag_teste) throws IOException {
        if(pag_teste){
            writeFileformatTeste(file);
        }else{
            writeFileformat(file, l, db);
        }
    }

    public File writeFileformat(File file,Lancamentos l,SQLiteDatabase db) throws IOException {
        FileWriter arq = null;
        String empresa="";
        arq = new FileWriter(file);
        DaoProdutor dp = new DaoProdutor(ctx);
        DaoFazenda df = new DaoFazenda(ctx);
        DaoMateria dm = new DaoMateria(ctx);
        //A porcentagem siginicia a variavel que será substituida pelos parametros, por ordem ...
        //O numero na frente da letra do parametro define a quantidade de espaço de um para outro
        //gravarArq.printf("| %d X %d = %2d %20d |%n", i, n, (i*n),0);
        DataHoraPedido dataHoraMS = DataHoraPedido.criarDataHoraPedido();
        if(this.config.getEmp().equalsIgnoreCase("vimilk")){
            empresa="Vimilk";
        }
        if(this.config.getEmp().equalsIgnoreCase("bdest")){
            empresa="Bom Destino";
        }

        PrintWriter gravarArq = new PrintWriter(arq);
        gravarArq.printf("   --- Captacao de Leite ---    %n");
        gravarArq.printf(" Empresa2 : "+empresa+ " %n",2);
        gravarArq.printf("Data: %2d/%d/%d %n",dataHoraMS.getDia(),dataHoraMS.getMes(),dataHoraMS.getAno());
        gravarArq.printf("Hora: %2d:%d:%d %n",dataHoraMS.getHora(),dataHoraMS.getMinuto(),dataHoraMS.getSegundo());
        gravarArq.printf("Produtor : "+dp.pesqprdoCod(db, l.getCodigo()).trim()+"%n");
        gravarArq.printf("Fazenda : "+df.pesquisarFazendaNome(db, Integer.parseInt(l.getFazenda())).trim()+"%n");
        gravarArq.printf("Materia : "+dm.pesqNomeMateria(db, String.valueOf(l.getMateria())).trim()+" %n");
        gravarArq.printf("Volume : "+l.getVolume()+"%n");
        if(l.getNtanque()!=0){
            gravarArq.printf("N Tanque : "+l.getVolume()+"%n");
        }
        if(l.getTemperatura()!=0){
            gravarArq.printf("Temperatura : "+l.getTemperatura()+"%n");
        }
        gravarArq.printf("%n");
        gravarArq.printf("SCA SISTEMAS %n " );
        gravarArq.printf("%n " );
        gravarArq.printf("...............................%n " );
        gravarArq.printf("%n " );
        arq.close();
        return file;

    }



    public void writeToFile(String data) {
        try {
            File file = new File(ctx.getFilesDir(), FILENAME);
            if(!file.exists()){
               file.mkdir();
            }
            String caminho=file.getPath();
            FileOutputStream outputStream = ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE);
          //  OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(FILENAME, Context.MODE_PRIVATE));
            outputStream.write(data.getBytes());
            outputStream.close();
        }
        catch (IOException e) {
            Log.e("Captaçao", "File write failed: " + e.toString());
        }

    }


    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = ctx.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("Captaçao", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Captaçao", "Can not read file: " + e.toString());
        }

        return ret;
    }
}