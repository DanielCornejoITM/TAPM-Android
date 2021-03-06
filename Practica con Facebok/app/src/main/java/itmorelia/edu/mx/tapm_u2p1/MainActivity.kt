package itmorelia.edu.mx.tapm_u2p1

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var FlotaFace = findViewById<FloatingActionButton>(R.id.Face)
        FlotaFace.setOnClickListener(View.OnClickListener {
            LoginManager.getInstance().logOut()
            Movemos(this,Inicio::class.java)
            this.finish()
            Toast.makeText(this, "Cerrando Sesion", Toast.LENGTH_LONG).show()
        })



        editLugar.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val cadena = p0?.toString() ?: ""
                if(cadena.length > 0)   consulta(cadena)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })
    }

    private fun consulta(lugar:String) {
        val hilo = GetVenuesTask()
        hilo.execute(
            getString(R.string.api_url) + "explore" +
            "?near=Morelia,Mich&query=$lugar&" +
            getString(R.string.client_id) + "&" +
            getString(R.string.client_secret) + "&" +
            getString(R.string.version)
        )
    }

    inner class GetVenuesTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?) : String {
            p0[0]?.let { return Conexion.resultado(it) }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val info = result ?: ""

            if(info.length > 0) {
                val jsonInfo = JSONObject(info)
                val jsonResponse = jsonInfo.getJSONObject("response")
                val jsonArrayGroups = jsonResponse.getJSONArray("groups")
                val jsonGroup = jsonArrayGroups.getJSONObject(0)
                val jsonArrayItems = jsonGroup.getJSONArray("items")

                val linearLayoutManager = LinearLayoutManager(this@MainActivity)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL

                recyclerLugares.layoutManager = linearLayoutManager
                recyclerLugares.adapter = MiAdaptador(jsonArrayItems,this@MainActivity, R.layout.venue_row)
            }
        }

    }

   fun Movemos(activity: Activity, nextActivity: Class<*>){

        val intent = Intent(activity, nextActivity)
        activity.startActivity(intent)

    }


}
