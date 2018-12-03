package com.vontroy.pku_ss_cloud_class.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.vontroy.pku_ss_cloud_class.data.LoginResult;
import com.vontroy.pku_ss_cloud_class.data.RegResult;
import com.vontroy.pku_ss_cloud_class.data.RndResult;
import com.vontroy.pku_ss_cloud_class.data.Student;
import com.vontroy.pku_ss_cloud_class.utils.DecryptHelper;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadService;
import net.gotev.uploadservice.UploadStatusDelegate;
import net.gotev.uploadservice.okhttp.OkHttpStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;

import static com.android.volley.VolleyLog.TAG;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by LinkedME06 on 16/10/28.
 */

public class ServerImp implements ServerApi {

    @Nullable
    private static ServerImp INSTANCE = null;

    public static ServerImp getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerImp();
        }
        return INSTANCE;
    }

    @Override
    public Observable<RegResult> reg(final String requestTag, @NonNull final Student student) {
        checkNotNull(student);
        return Observable.defer(new Func0<Observable<RegResult>>() {
            @Override
            public Observable<RegResult> call() {
                try {
                    ServerData<RndResult> serverDataRnd = new ServerData<RndResult>();
                    Map paramRnd = new HashMap();
                    paramRnd.put("sid", student.getSid());
                    return Observable.just(serverDataRnd.getServerData(requestTag, Request.Method.GET, ServerInterface.rnd, paramRnd, RndResult.class))
                            .concatMap(new Func1<RndResult, Observable<RegResult>>() {
                                @Override
                                public Observable<RegResult> call(RndResult rndResult) {
                                    try {
                                        System.out.println("ddd " + rndResult.getData());
                                        ServerData<RegResult> serverDataReg = new ServerData<RegResult>();
                                        Map paramReg = new HashMap();
                                        paramReg.put("sid", student.getSid());
                                        paramReg.put("nick", student.getNick());
                                        paramReg.put("password", DecryptHelper.getEncryptedPassword(student.getPassword(), rndResult.getData()));
                                        return Observable.just(serverDataReg.getServerData(requestTag, Request.Method.POST, ServerInterface.reg, paramReg, RegResult.class));
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                        return Observable.error(e);
                                    }
                                }
                            });
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("routes", e.getMessage());
                    return Observable.error(e);
                }
            }
        });
    }


    @Override
    public Observable<LoginResult> login(final String requestTag, @NonNull final Student student) {
        checkNotNull(student);
        return Observable.defer(new Func0<Observable<LoginResult>>() {
            @Override
            public Observable<LoginResult> call() {
                try {
                    ServerData<RndResult> serverDataRnd = new ServerData<RndResult>();
                    Map paramRnd = new HashMap();
                    paramRnd.put("sid", student.getSid());
                    return Observable.just(serverDataRnd.getServerData(requestTag, Request.Method.GET, ServerInterface.rnd, paramRnd, RndResult.class))
                            .concatMap(new Func1<RndResult, Observable<LoginResult>>() {
                                @Override
                                public Observable<LoginResult> call(RndResult rndResult) {
//                                    System.out.println("ddd " + rndResult.getData());
                                    ServerData<LoginResult> serverDataLogin = new ServerData<LoginResult>();
                                    Map paramLogin = new HashMap();
                                    Log.d("ddd", "call: " + student.getSid() + "==" + student.getPassword());
                                    paramLogin.put("sid", student.getSid());

                                    paramLogin.put("password", DecryptHelper.getEncryptedPassword(student.getPassword(), rndResult.getData()));
                                    Log.d("ddd", "call: " + DecryptHelper.getEncryptedPassword("123456", "0.3164703997484356"));
                                    try {
                                        return Observable.just(serverDataLogin.getServerData(requestTag, Request.Method.POST, ServerInterface.login, paramLogin, LoginResult.class));
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    return Observable.error(new Throwable());
                                }
                            });
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("routes", e.getMessage());
                    return Observable.error(e);
                }
            }
        });
    }

    @Override
    public <T> Observable<T> common(final String requestTag, final int method, final String url, final Map<String, String> param, final Class<T> clazz) {
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                try {
                    ServerData<T> serverData = new ServerData<T>();
                    return Observable.just(serverData.getServerData(requestTag, method, url, param, clazz));
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("routes", e.getMessage());
                    return Observable.error(e);
                }
            }
        });
    }

    public void uploadMultipart(final Context context, final String filePath, String uuid, final Boolean isEncUpload) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context, ServerInterface.upload)
                            .addFileToUpload(filePath, "file")
                            .setUtf8Charset()
                            .addParameter("uuid", uuid)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .setDelegate(new UploadStatusDelegate() {
                                @Override
                                public void onProgress(Context context, UploadInfo uploadInfo) {

                                }

                                @Override
                                public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                    Log.d(TAG, "onError upload: " + exception);
                                }

                                @Override
                                public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                    if (isEncUpload) {
                                        File toDelete = new File(filePath);
                                        toDelete.delete();
                                    }
                                    // your code here
                                    // if you have mapped your server response to a POJO, you can easily get it:
                                    // YourClass obj = new Gson().fromJson(serverResponse.getBodyAsString(), YourClass.class);
                                    Toast.makeText(context, "上传成功!", Toast.LENGTH_SHORT).show();
                                    long fileUploadCompleteTime = System.currentTimeMillis();
                                    Log.d("time_state", "file upload complete time: " + fileUploadCompleteTime);
                                }

                                @Override
                                public void onCancelled(Context context, UploadInfo uploadInfo) {

                                }
                            })
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }
}
