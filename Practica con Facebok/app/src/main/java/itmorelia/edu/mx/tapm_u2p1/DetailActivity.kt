package itmorelia.edu.mx.tapm_u2p1

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setSupportActionBar(toolbar)

        val hilo = GetDetailTask()
        hilo.execute(
            getString(R.string.api_url) + intent.getStringExtra("id") +
            "?" + getString(R.string.client_id) + "&" +
            getString(R.string.client_secret) + "&" +
            getString(R.string.version)
        )

    }

    inner class GetDetailTask : AsyncTask<String, String, String>() {

        override fun doInBackground(vararg p0: String?): String {
            p0[0]?.let { return Conexion.resultado(it) }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val info = result ?: ""

            if(info.length > 0) {
                val jsonInfo = JSONObject(info)
                val jsonResponse = jsonInfo.getJSONObject("response")
                val jsonVenue = jsonResponse.getJSONObject("venue")

                textTitulo.text = jsonVenue.getString("name")

                // Firebase
                try {
                    // Guardar en FireBase el lugar
                    val fireBase = FirebaseDatabase.getInstance()
                    val ref = fireBase.getReference("tapmVisitas")
                    //No se Ocupa
                    // ref.setValue(jsonVenue.getString("id"), 1)
                    val childref = ref.child(jsonVenue.getString("id"))

                    if(childref != null){
                        childref.addListenerForSingleValueEvent(object : ValueEventListener {

                            override fun onCancelled(p0: DatabaseError?) {

                            Log.w("ReadFireBase", p0?.toException())

                            }

                            override fun onDataChange(p0: DataSnapshot?) {
                            try {
                                val conteo = p0?.value ?: p0?.value as Long
                                val visits = conteo as Long
                                if(visits != null){
                                    ref.child(jsonVenue.getString("id")).setValue(visits + 1)
                                    Toast.makeText(this@DetailActivity, "Super se  guardo la visita: $visits", Toast.LENGTH_LONG).show()

                                }
                            }catch(e: Exception){
                                e.printStackTrace()
                                ref.child(jsonVenue.getString("id")).setValue(1)
                                Toast.makeText(this@DetailActivity, "Felicidades!!! Esta es tu primera visita, Eres super genial", Toast.LENGTH_LONG).show()

                            }




                            }

                        })


                    }
/*
                    // Leer de firebase
                    ref.addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            Log.w("ReadFireBase", p0?.toException())
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            val lectura = p0?.value as String
                            Toast.makeText(this@DetailActivity, "Se guardo: $lectura", Toast.LENGTH_LONG).show()
                        }

                    })
                    */
                } catch (e:Exception) {
                    e.printStackTrace()
                }

                // Check social
                if(jsonVenue.has("contact")) {
                    val jsonContact = jsonVenue.getJSONObject("contact")
                    if(jsonContact.has("twitter")) {
                        fabTwitter.visibility = View.VISIBLE
                        fabTwitter.setOnClickListener { mostrarWeb("t", jsonContact.getString("twitter")) }
                    } else fabTwitter.visibility = View.INVISIBLE
                    if(jsonContact.has("facebook")) {
                        fabFacebook.visibility = View.VISIBLE
                        fabFacebook.setOnClickListener { mostrarWeb("f", jsonContact.getString("facebook")) }
                    } else fabFacebook.visibility = View.INVISIBLE
                } else {
                    fabTwitter.visibility = View.INVISIBLE
                    fabFacebook.visibility = View.INVISIBLE
                }

                // Location
                if(jsonVenue.has("location")) {
                    val jsonLocation = jsonVenue.getJSONObject("location")
                    if(jsonLocation.has("address")) {
                        textDescripcion.text = jsonLocation.getString("address")
                    } else textDescripcion.text = "Sin direccion"
                }
            }
        }

        private fun mostrarWeb(tipo:String, page:String) {
            if(tipo.equals("t")) {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/$page")
                )
                startActivity(intent)
            } else {
                val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/$page")
                )
                startActivity(intent)
            }
        }

    }

}
