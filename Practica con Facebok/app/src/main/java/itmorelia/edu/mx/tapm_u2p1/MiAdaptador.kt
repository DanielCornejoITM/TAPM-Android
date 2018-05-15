package itmorelia.edu.mx.tapm_u2p1

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.IntentCompat
import android.view.View
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

/**
 * Created by Alexander on 25/04/2018.
 */
class MiAdaptador(val elementos: JSONArray, val contexto: Context, val recursoLayout: Int) : RecyclerView.Adapter<MiAdaptador.MiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MiViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(recursoLayout, parent, false)

        val miHolder = MiViewHolder(view)
        return miHolder
    }

    override fun getItemCount(): Int {
        return elementos.length()
    }

    override fun onBindViewHolder(holder: MiViewHolder?, position: Int) {
        try {
            val elemento = elementos.getJSONObject(position)

            val jsonVenue = elemento.getJSONObject("venue")

            var arrayTips = JSONArray()
            if(elemento.has("tips"))
                arrayTips = elemento.getJSONArray("tips")

            var jsonTip = JSONObject()
            if (arrayTips.length() > 0)
                jsonTip = arrayTips.getJSONObject(0)
            else
                jsonTip.put("text", "Sin descripcion")

            var rating = 0.0
            var ratingColor = "000000"
            if(jsonVenue.has("rating")) {
                rating = jsonVenue.getDouble("rating")
                ratingColor = jsonVenue.getString("ratingColor")
            }

            val arrayCategorias = jsonVenue.getJSONArray("categories")
            val jsonCategoria = arrayCategorias.getJSONObject(0)
            val jsonIcon = jsonCategoria.getJSONObject("icon")

            holder?.bind(
                    contexto,
                    jsonVenue.getString("id"),
                    jsonVenue.getString("name"),
                    jsonIcon.getString("prefix").replace("\\/","/") + "88" + jsonIcon.getString("suffix"),
                    rating,
                    ratingColor,
                    jsonTip.getString("text")
            )

        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    class MiViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        internal fun bind(context:Context,id:String,name:String,icon:String,rating:Double,color:String,desc:String) {
            // Views
            val titulo = itemView?.findViewById<TextView>(R.id.textRowTitulo)
            val subtitulo = itemView?.findViewById<TextView>(R.id.textRowSubtitulo)
            val estrellas = itemView?.findViewById<RatingBar>(R.id.ratingRow)
            val calificacion = itemView?.findViewById<TextView>(R.id.textRatingRow)
            val botonFlotante = itemView?.findViewById<FloatingActionButton>(R.id.fabRow)
            // Se asignan los valores
            titulo?.text = name
            subtitulo?.text = desc
            estrellas?.rating = rating.toFloat()
            calificacion?.text = """   $rating   """
            calificacion?.setBackgroundColor(Color.parseColor("#$color"))
            botonFlotante?.setOnClickListener { verDetalles(context,id) }
            // Obtener la imagen con un hilo
            val hilo = DownloadIconTask()
            hilo.execute(icon)
        }

        private fun verDetalles(context: Context, id: String) {
            val intent = Intent(context,DetailActivity::class.java)
            intent.putExtra("id",id)
            context.startActivity(intent)
        }

        inner class DownloadIconTask : AsyncTask<String, Bitmap?, Bitmap?>() {

            override fun doInBackground(vararg p0: String?): Bitmap? {
                Log.i("URLImg",p0[0])
                try {
                    val imgURL = URL(p0[0])
                    val bitmap = BitmapFactory.decodeStream(
                        imgURL.openConnection().getInputStream()
                    )
                    return bitmap
                } catch (e:Exception) {
                    e.printStackTrace()
                    return null
                }
            }

            override fun onPostExecute(result: Bitmap?) {
                super.onPostExecute(result)
                val imagen = itemView?.findViewById<ImageView>(R.id.imageRow)
                result?.let {
                    imagen?.setImageBitmap(it)
                    imagen?.setBackgroundResource(R.color.colorPrimaryDark)
                }
            }

        }

    }

}