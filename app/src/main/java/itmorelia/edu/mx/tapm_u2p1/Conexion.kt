package itmorelia.edu.mx.tapm_u2p1

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

/**
 * Created by Alexander on 23/04/2018.
 */
class Conexion {

    companion object {
        fun resultado(dir:String) : String {
            val url = URL(dir)

            val con = url.openConnection()
            con.connect()

            val br = BufferedReader(
                InputStreamReader(con.getInputStream())
            )

            var info = ""
            var prettyInfo = ""
            var lectura : String?
            do {

                lectura = br.readLine()
                if(lectura != null) {
                    info += lectura
                    prettyInfo += lectura + "\n"
                } else break

            }while (true)

            Log.i("ConexionResult",prettyInfo)

            return info
        }
    }

}