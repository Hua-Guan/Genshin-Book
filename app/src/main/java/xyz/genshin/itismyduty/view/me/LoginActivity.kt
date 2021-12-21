package xyz.genshin.itismyduty.view.me

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import xyz.genshin.itismyduty.R

/**
 * @author GuanHua
 */
class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setQuickRegister()
        setForgetPassword()
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

}