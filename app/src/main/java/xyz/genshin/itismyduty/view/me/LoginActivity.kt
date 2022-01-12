package xyz.genshin.itismyduty.view.me

import android.content.ContextParams
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import xyz.genshin.itismyduty.R
import xyz.genshin.itismyduty.utils.VolleyInstance

/**
 * @author GuanHua
 */
class LoginActivity: AppCompatActivity() {

    companion object{

        private const val LOGIN_URI = "http://genshin.itismyduty.xyz:8080/GenshinBook/login"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setQuickRegister()
        setForgetPassword()
        setLogin()
    }

    private fun setQuickRegister(){

        val quickRegister = findViewById<TextView>(R.id.quick_register)
        quickRegister.setOnClickListener {

            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)

        }


    }

    private fun setForgetPassword(){

        val forgetPassword = findViewById<TextView>(R.id.forget_password)
        forgetPassword.setOnClickListener {

            val intent = Intent(this, ForgetPasswordActivity::class.java)
            startActivity(intent)

        }

    }

    private fun setLogin(){
        val account = findViewById<EditText>(R.id.edit_account)
        val password = findViewById<EditText>(R.id.edit_password)

        val login = findViewById<Button>(R.id.btn_login)

        login.setOnClickListener {

            val stringRequest = object: StringRequest(Method.POST, LOGIN_URI,
                {respond ->
                    println(respond)
                    Toast.makeText(this, respond, Toast.LENGTH_LONG).show()
                }, {}){
                override fun getParams(): MutableMap<String, String> {
                    val parameters = HashMap<String, String>()
                    parameters.put("e_mail", account.text.toString())
                    parameters.put("password", password.text.toString())
                    return parameters
                }
            }

            VolleyInstance.getInstance(this).addToRequestQueue(stringRequest)
        }

    }

}