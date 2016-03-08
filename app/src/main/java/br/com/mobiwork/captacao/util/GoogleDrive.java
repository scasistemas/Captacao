package br.com.mobiwork.captacao.util;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentList;
import com.google.api.services.drive.model.ParentReference;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import br.com.mobiwork.captacao.R;
import br.com.mobiwork.captacao.dao.DaoConfiggd;
import br.com.mobiwork.captacao.dao.DaoCreateDB;
import br.com.mobiwork.captacao.dao.DaoCreateDBC;
import br.com.mobiwork.captacao.dao.DaoMotorista;
import br.com.mobiwork.captacao.model.Configgd;
import br.com.mobiwork.captacao.model.Motorista;

/**
 * Created by LuisGustavo on 27/10/2015.
 */
public class GoogleDrive extends Activity {

    static final int 				COMPLETE_AUTHORIZATION_REQUEST_CODE = 3;
    static final int 				REQUEST_ACCOUNT_PICKER = 1;
    static final int 				REQUEST_AUTHORIZATION = 2;
    static final int 				RESULT_STORE_FILE = 4;
    public static final String 			FOLDER = "CAPTACAO";
    public static final String 			FOLDEREXPORTADOS = "EXPORTADOS";
    public static final String 			FOLDERCOLETADOS = "COLETADOS";
    public static final String 			FOLDER_VS = "vs";

    private static Uri mFileUri;
    private static Drive mService;
    private GoogleAccountCredential mCredential;
    private Context mContext;
    SQLiteDatabase dbC,db;
    private List<File> mResultList;
    private ProgressDialog dialog,dialog2;
    Motorista localConfig;
    boolean erro;
    com.google.api.services.drive.model.FileList fileList;
    private DaoConfiggd dcggd;
    private Cursor geralccgd;

    public GoogleDrive(Context ctx){

        this.dbC = new DaoCreateDBC(ctx).getWritableDatabase();
        localConfig = new DaoMotorista(ctx).consultar(this.dbC);
        this.db = new DaoCreateDB(ctx).getWritableDatabase();
        // Connect to Google Drive
        mCredential = GoogleAccountCredential.usingOAuth2(ctx, Arrays.asList(DriveScopes.DRIVE));
      //    startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
        mCredential.setSelectedAccountName(localConfig.getLogindrive());
        //mCredential.setSelectedAccountName("lgdasilveira4@gmail.com");
        mService = getDriveService(mCredential);
        dcggd= new DaoConfiggd(ctx);
        mContext=ctx;
        geralccgd=dcggd.selconfig(db);


    }

    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential)
                .build();
    }

    public String saveFileToDrive(final String paramString)
    {
        erro=false;
        ArrayList<String> erro = new ArrayList<String>(2);
        erro.add(0, "");
        erro.add(1, "1");
        com.google.api.services.drive.model.File body = null;
        ContentResolver cR = null;
        FileContent mediaContent=null;
        if(mCredential.getSelectedAccount()!=null) {
            try {
                mFileUri = Uri.parse(FolderConfig.getExternalStorageDirectory() + "/" + paramString);
                cR = mContext.getContentResolver();
                // File's binary content
                java.io.File fileContent = new java.io.File(mFileUri.getPath());
                 mediaContent = new FileContent(cR.getType(mFileUri), fileContent);
                // File's meta data.
                body = new com.google.api.services.drive.model.File();
                body.setTitle(fileContent.getName());
                body.setMimeType(cR.getType(mFileUri));
                if(!geralccgd.moveToFirst()){
                    erro.set(1, criarConfig(FOLDER));
                }
                body.setParents(Arrays.asList(new ParentReference().setId(getExistsFolder(mService, FOLDERCOLETADOS))));
                com.google.api.services.drive.Drive.Files f1 = mService.files();
                com.google.api.services.drive.Drive.Files.Insert i1 = f1.insert(body, mediaContent);
                com.google.api.services.drive.model.File file = i1.execute();

            } catch (UserRecoverableAuthIOException e) {
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                erro.set(1, e.toString());
                erro.set(0, e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                erro.set(1, e.toString());
                erro.set(0, "Erro ao trasnferir , verifique sua internet ");
            }
            //SEGUNDA TENTATIVA
            if(!erro.get(1).equalsIgnoreCase("1")){
                erro.set(1,"1");
                erro.set(0,"");
                try
                {
                    java.io.File fil = new java.io.File(mFileUri.getPath());
                    InputStream is = new FileInputStream(fil);
                    InputStreamContent mediaContent2 = new InputStreamContent(cR.getType(mFileUri), is);
                    mediaContent2.setLength(fil.length());
                    File destFile = null;
                    Drive.Files.Insert insert = mService.files().insert(body, mediaContent);
                    MediaHttpUploader uploader = insert.getMediaHttpUploader();
                    uploader.setDirectUploadEnabled(false);
                    destFile = insert.execute();
                } catch (UserRecoverableAuthIOException e) {
                    startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                    erro.set(1,e.toString());
                    erro.set(0,e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    erro.set(1,e.toString());
                    erro.set(0, "Erro ao trasnferir , verifique sua internet " );
                }

            }



        }else{
            erro.set(1, "ERRO - Conta nao adicionada !!!");
        }



        return erro.get(1);
    }



    public String criarConfig(String pasta) throws IOException {

        Configgd cggd = new Configgd();
        cggd.setFolderpai(getExistsFolder(mService, pasta));
        if(cggd.getFolderpai().equalsIgnoreCase("")){
            com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
            body.setTitle(pasta);
            body.setMimeType("application/vnd.google-apps.folder");
            try {
                mService.files().insert(body).execute();
                cggd.setFolderpai(getExistsFolder(mService, pasta));
            } catch (IOException e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
       cggd.setFoldercol(getExistsFolder(mService, FOLDERCOLETADOS));
        if(cggd.getFoldercol().equalsIgnoreCase("")){
                criarPasta( FOLDERCOLETADOS,cggd.getFolderpai());
                cggd.setFoldercol(getExistsFolder(mService, FOLDERCOLETADOS));
        }
        cggd.setFolderexp(getExistsFolder(mService, FOLDEREXPORTADOS));
        if(cggd.getFolderexp().equalsIgnoreCase("")){
            criarPasta( FOLDEREXPORTADOS,cggd.getFolderpai());
            cggd.setFolderexp(getExistsFolder(mService, FOLDEREXPORTADOS));
        }
        cggd.setFoldervs(getExistsFolder(mService, FOLDER_VS));
        if(cggd.getFoldervs().equalsIgnoreCase("")){
            criarPasta( FOLDER_VS,cggd.getFolderpai());
            cggd.setFoldervs(getExistsFolder(mService, FOLDER_VS));
        }
        try {
            if (geralccgd == null) {
                dcggd.instValores(db, cggd);
            } else {
                if (!geralccgd.moveToFirst()) {
                    dcggd.instValores(db, cggd);
                } else {
                    dcggd.altValores(db, cggd);
                }
            }
        }catch (Exception e){
            return e.getMessage();
        }
        return "1";
    }

    public String criarPasta(String pasta,String ParentReference) throws IOException {
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setTitle(pasta);
        body.setMimeType("application/vnd.google-apps.folder");
        body.setParents(Arrays.asList(new ParentReference().setId(ParentReference)));
        try {
            mService.files().insert(body).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "1";
    }

    private String getExistsFolder(final Drive service, final String title) throws IOException
    {
        final String[] folderpai = new String[1];
            try {
                Drive.Files.List request;
                request = service.files().list();
                String query = "mimeType='application/vnd.google-apps.folder' AND trashed=false AND title='" + title + "'";// AND '" + parentId + "' in parents";
                //   Logger.info(TAG + ": isFolderExists(): Query= " + query);
                request = request.setQ(query);
                FileList files = request.execute();
                //   Logger.info(TAG + ": isFolderExists(): List Size =" + files.getItems().size());
                if (files.getItems().size() == 0) //if the size is zero, then the folder doesn't exist
                    return  "";
                else {
                    //since google drive allows to have multiple folders with the same title (name)
                    //we select the first file in the list to return
                    // return files.getItems().get(0);
                    folderpai[0] = (files.getItems().get(0).getId());

                   return  folderpai[0];
                }
            }catch (Exception e){
              return  "";
            }
    }

    public String getDriveContents(String param)
    {
        ArrayList<String> erro = new ArrayList<String>(2);
        erro.add(0,"");
        erro.add(1,"");
        mResultList = new ArrayList<File>();
        com.google.api.services.drive.Drive.Files f1 = mService.files();
        com.google.api.services.drive.Drive.Files.List request = null;
        do
        {
            try
            {
                try {
                    if (!geralccgd.moveToFirst()) {
                        erro.set(1, criarConfig(FOLDER));
                    }
                } catch (UserRecoverableAuthIOException e) {
                    Intent f =e.getIntent();
                    Activity activity = (Activity) mContext;
                    activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                }

                request = f1.list();
                String query="";
                if(param.equalsIgnoreCase(GoogleDrive.FOLDER_VS)) {
                     query = "'" + getExistsFolder(mService, FOLDER_VS) + "' in parents and trashed=false";// AND '" + parentId + "' in parents";//   Logger.info(TAG + ": isFolderExists(): Query= " + query);
                }else {
                    query = "'" + getExistsFolder(mService, FOLDEREXPORTADOS) + "' in parents and trashed=false";// AND '" + parentId + "' in parents";//   Logger.info(TAG + ": isFolderExists(): Query= " + query);
                }

                // String query = "(mimeType contains 'application/zip' or mimeType contains 'text/plain') and trashed=false";// AND '" + parentId + "' in parents";//   Logger.info(TAG + ": isFolderExists(): Query= " + query);
                request = request.setQ(query);
                try {
                    try {
                        fileList = request.execute();
                    } catch (UserRecoverableAuthIOException e) {
                        Intent f =e.getIntent();
                        Activity activity = (Activity) mContext;
                        activity.startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
                    }
                }catch(IOException e){
                    erro.set(1,e.toString());
                    erro.set(0,"SEM AUTENTICACAO, OU ERRO GOOGLE PLAY S.!!");
                    break;
                }
                try {
                    mResultList.addAll(fileList.getItems());
                    request.setPageToken(fileList.getNextPageToken());
                }catch(Exception e){
                    erro.set(1,e.toString());
                    erro.set(0,"erro");
                    break;
                }
            } catch (UserRecoverableAuthIOException e)   {
                erro.set(1,e.toString());
                erro.set(0, "POR FAVOR CONFIGURE O GOOGLE PLAY SERVICES!!!");
                startActivityForResult(e.getIntent(), REQUEST_AUTHORIZATION);
            } catch (IOException e) {
                erro.set(1,e.toString());
                erro.set(0,"POR FAVOR ADICIONA UMA CONTA DE SINCRONIZACAO!!!");
                e.printStackTrace();
                if (request != null)
                {
                    request.setPageToken(null);
                }
            }  catch (Exception e)   {
                erro.set(1,"POR FAVOR CONFIGURE O GOOGLE PLAY SERVICES!!!");
                erro.set(0, "POR FAVOR CONFIGURE O GOOGLE PLAY SERVICES!!!");
            }
        } while (request.getPageToken() !=null && request.getPageToken().length() > 0);
        return erro.get(1);
    }

    public String  downloadItemFromList(String nome,String ext,String param)
    {
        ArrayList<String> msgerro = new ArrayList<String>(2);
        msgerro.add(0,"");
        msgerro.add(1,"");
        boolean erro =false;
        boolean encontrou=false;
        String arquivo=nome+ext;

        for(File tmp : mResultList)
        {
            if (tmp.getTitle().equalsIgnoreCase(arquivo))
            {
                if (tmp.getDownloadUrl() != null && tmp.getDownloadUrl().length() >0)
                {
                    try
                    {
                        encontrou=true;
                        com.google.api.client.http.HttpResponse resp =
                                mService.getRequestFactory()
                                        .buildGetRequest(new GenericUrl(tmp.getDownloadUrl()))
                                        .execute();
                        InputStream iStream = resp.getContent();
                        try
                        {
                            java.io.File file=null;
                            if(param.equals("vs")) {
                                file = new java.io.File(FolderConfig.getExternalStorageDirectoryVs(),
                                        tmp.getTitle());
                            }else{
                                file = new java.io.File(FolderConfig.getExternalStorageDirectory(),
                                        tmp.getTitle());
                            }
                            try{
                                storeFile(file, iStream);
                                break;
                            }catch (Exception e){
                                erro=true;
                                msgerro.set(1, e.toString());
                                msgerro.set(0,"Não foi possivel baixar os dados");
                            }

                        } finally {
                            iStream.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        erro=true;
                        msgerro.set(0,"Erro no download dos dados");
                        msgerro.set(1,e.toString());
                    }
                }
            }
        }
        if(encontrou==false){
            erro=true;
            msgerro.set(1,"Não foi possivel encontrar o arquivo de dados");
            msgerro.set(0,"Alerta");
        }
        return msgerro.get(1);
    }



    public  List<HashMap<String, String>>  ListarArq()
    {
        List<HashMap<String, String>> fillMaps=new ArrayList<>();
        if(fillMaps!=null){
            fillMaps.clear();
        }
        for(File tmp :mResultList)
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("_id", "" +String.valueOf(tmp.getTitle()));
            map.put("titulo", "" + String.valueOf(tmp.getTitle()));
            map.put("data", "" + String.valueOf(tmp.getModifiedDate()));
            fillMaps.add(map);

        }
        return fillMaps;

    }





    private void storeFile(java.io.File file, InputStream iStream)
    {
        try
        {
            final OutputStream oStream = new FileOutputStream(file);
            try
            {
                try
                {
                    final byte[] buffer = new byte[1024];
                    int read;
                    while ((read = iStream.read(buffer)) != -1)
                    {
                        oStream.write(buffer, 0, read);
                    }
                    oStream.flush();
                } finally {
                    oStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
