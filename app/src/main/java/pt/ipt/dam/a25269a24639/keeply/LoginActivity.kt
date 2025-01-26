package pt.ipt.dam.a25269a24639.keeply

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //TODO: Fazer página de Boas Vindas

        //TODO: Se o user estiver logged in, ir diretamente para a MainActivity

        //TODO: Dar extract string resources em todos os xml's

        // Checka se é a primeira vez que o utilizador abre a aplicação
        val prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            showConsentDialog()
            // Tornar isFirstLaunch falso para não mostrar o consentimento novamente
            prefs.edit().putBoolean("isFirstLaunch", false).apply()
        }

        // PARA DEBUG: reset ao first launch com long click no botão de login
        findViewById<Button>(R.id.loginButton).setOnLongClickListener {
            resetFirstLaunch()
            Toast.makeText(this, "First launch reset!", Toast.LENGTH_LONG).show()
            true
        }

        // PARA DEBUG: botão para testar a MainActivity
        findViewById<Button>(R.id.testButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // TODO: implementar lógica de login
                Toast.makeText(this, "Login com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        val aboutButton = findViewById<Button>(R.id.aboutButton)
        aboutButton.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Sobre o Keeply")
            .setMessage("Keeply é uma aplicação simples e prática para tirar notas.\n\nVersão 1.0   © 2025 \n\nJoão Campos nº 25269 \nCristiane Mayabanza nº 24639")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showConsentDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Política de Privacidade")
            .setMessage(
                "Bem-vindo ao Keeply!\n\n" +
                        "Ao utilizar esta aplicação, concorda com:\n\n" +
                        "• Armazenamento local das suas notas\n" +
                        "• Armazenamento seguro das suas credenciais\n" +
                        "• Utilização de dados apenas para funcionamento da app\n\n" +
                        "Não partilhamos os seus dados com terceiros."
            )
            .setCancelable(false)
            .setPositiveButton("Aceitar") { _, _ -> // Nada, user aceitou
            }
            .setNegativeButton("Recusar") { _, _ ->
                resetFirstLaunch()
                finish()        // User não aceitou, fechar a aplicação
            }
            .show()
    }

    private fun resetFirstLaunch() {
        getSharedPreferences("AppPrefs", MODE_PRIVATE)
            .edit()
            .putBoolean("isFirstLaunch", true)
            .apply()
    }
}
