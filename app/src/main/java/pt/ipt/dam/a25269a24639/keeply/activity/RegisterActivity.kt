package pt.ipt.dam.a25269a24639.keeply.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import pt.ipt.dam.a25269a24639.keeply.R
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.UserRepository

class RegisterActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)

        // Inicializa o banco de dados e repositório
        val database = NoteDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())

        // Botão de registrar
        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateFields(name, email, password)) {
                // Inserir o usuário no banco de dados
                lifecycleScope.launch {
                    val user = User(name = name, email = email, password = password, type = "user")
                    userRepository.register(user)
                    Toast.makeText(
                        this@RegisterActivity,
                        "Usuário registado com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

        // Botão de cancelar
        val cancelBtn = findViewById<Button>(R.id.cancelButton)
        cancelBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Valida os campos de entrada.
     * Verifica se o nome, email e senha estão preenchidos corretamente.
     */
    private fun validateFields(name: String, email: String, password: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira seu nome.", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira seu email.", Toast.LENGTH_SHORT).show()
                false
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Por favor, insira uma palavra passe.", Toast.LENGTH_SHORT).show()
                false
            }
            password.length < 6 -> {
                Toast.makeText(
                    this,
                    "A password deve ter pelo menos 6 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }
            else -> true
        }
    }
}