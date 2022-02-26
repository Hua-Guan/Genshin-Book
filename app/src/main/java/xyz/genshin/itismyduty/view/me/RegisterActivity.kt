package xyz.genshin.itismyduty.view.me

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.utils.VolleyInstance


/**
 * @author 登录和注册
 */
class RegisterActivity: AppCompatActivity() {

    companion object{

        private const val REGISTER_URI = "http://genshin.itismyduty.xyz:8080/GenshinBook/register"
        private const val SEND_VERIFICATION_URI = "http://genshin.itismyduty.xyz:8080/GenshinBook/getIdCode"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        sendVerification()
        setRegister()
    }

    private fun sendVerification(){

        val account = findViewById<EditText>(R.id.edit_account)
        val sendVerification = findViewById<Button>(R.id.send_verification_code)
        sendVerification.setOnClickListener {

            val strAccount = account.text.toString()
            val stringRequest = StringRequest(Request.Method.GET,
                "$SEND_VERIFICATION_URI?e_mail=$strAccount",
                {respond ->
                    Toast.makeText(this, respond, Toast.LENGTH_LONG).show()
                },{}
            )
            stringRequest.retryPolicy = DefaultRetryPolicy(90000, 0, 1f)
            VolleyInstance.getInstance(this).addToRequestQueue(stringRequest)

        }

    }

    private fun setRegister(){

        val account = findViewById<EditText>(R.id.edit_account)
        val password = findViewById<EditText>(R.id.edit_password)
        val verification = findViewById<EditText>(R.id.edit_verification_code)

        val register = findViewById<Button>(R.id.register)

        register.setOnClickListener {
            val stringRequest = object: StringRequest(Method.POST, REGISTER_URI,
                {respond ->
                    Toast.makeText(this, respond, Toast.LENGTH_LONG).show()
                }, {}){

                override fun getParams(): MutableMap<String, String> {
                    val parameters = HashMap<String, String>()
                    parameters["idCode"] = verification.text.toString()
                    parameters["e_mail"] = account.text.toString()
                    parameters["password"] = password.text.toString()
                    return parameters
                }

            }
            stringRequest.retryPolicy = DefaultRetryPolicy(90000, 0, 1f)
            VolleyInstance.getInstance(this).addToRequestQueue(stringRequest)

        }

    }

}