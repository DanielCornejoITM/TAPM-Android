package itmorelia.edu.mx.tapm_u2p1

import android.app.Activity
import android.app.IntentService
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.telecom.Call
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import com.facebook.login.widget.LoginButton
import java.util.*


class Inicio : AppCompatActivity() {

private var callbackManager :CallbackManager? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        var Verifico = AccessToken.getCurrentAccessToken()
        if (Verifico!=null){
            Toast.makeText(this@Inicio, "Ya Tienes una sesion Iniciada", Toast.LENGTH_LONG).show()


            Iniciamos(this@Inicio,MainActivity::class.java)

        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)

        //Inicio de Sesion de Facebook
        var BotondeFace = findViewById<Button>(R.id.login_button)

        BotondeFace.setOnClickListener(View.OnClickListener {
            Toast.makeText(this@Inicio, "INICIEMOS!!", Toast.LENGTH_LONG).show()


            callbackManager=CallbackManager.Factory.create()
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                    object : FacebookCallback<LoginResult>{
                        override fun onSuccess(result: LoginResult?) {
                            Log.d("Inicio", "Facebook token: " + result?.accessToken!!.token)
                            Toast.makeText(this@Inicio, "Has Iniciado Sesion Exitosamente", Toast.LENGTH_LONG).show()
                            Iniciamos(this@Inicio,MainActivity::class.java)
                        }
                        override fun onCancel() {
                            Log.d("Inicio","Facebook Cancelado")
                            Toast.makeText(this@Inicio, "Se cancela", Toast.LENGTH_LONG).show()
                        }
                        override fun onError(error: FacebookException?) {
                            Log.d("Inicio","UN ERROR")
                            Toast.makeText(this@Inicio, "Algo Salio Mal", Toast.LENGTH_LONG).show()
                        }
                    }
                    )


        })

    }


    fun Iniciamos(activity: Activity, nextActivity: Class<*>){

        val intent = Intent(activity, nextActivity)
        activity.startActivity(intent)
        activity.finish()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }
}
