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
import pt.ipt.dam.a25269a24639.keeply.api.UserApi
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.Note.NoteDatabase
import pt.ipt.dam.a25269a24639.keeply.data.domain.User
import pt.ipt.dam.a25269a24639.keeply.data.infrastructure.User.UserRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Activity responsável pelo registo de novos utilizadores.
 *
 * Esta activity permite:
 * - Criar novas contas de utilizador
 * - Validar os dados introduzidos
 * - Comunicar com o servidor através da API
 * - Retornar as credenciais à LoginActivity após registo bem-sucedido
 *
 * Funcionalidades principais:
 * - Validação em tempo real dos campos de registo
 * - Integração com API REST para criação de conta
 * - Gestão de erros e feedback ao utilizador
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository

    // Configuração do cliente Retrofit para comunicação com a API
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://keeplybackend-production.up.railway.app/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(UserApi::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val nameInput = findViewById<TextInputEditText>(R.id.nameInput)
        val emailInput = findViewById<TextInputEditText>(R.id.emailInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)


        val database = NoteDatabase.getDatabase(this)
        userRepository = UserRepository(database.userDao())

        // Botão de registar
        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (validateFields(name, email, password)) {
                lifecycleScope.launch {
                    try {
                        val user = User(name = name, email = email, password = password)
                        val response = api.createUser(user)

                        if (!response.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Registration failed: Email already in use.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }

                        Toast.makeText(
                            this@RegisterActivity,
                            "User registered successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        // enviar email e password para a LoginActivity
                        val intent = Intent()
                        intent.putExtra("email", email)
                        intent.putExtra("password", password)
                        setResult(RESULT_OK, intent)
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return when {
            // validação do nome
            name.length < 2 || name.length > 50 -> {
                Toast.makeText(this, "O nome deve ter entre 2 e 50 caracteres.", Toast.LENGTH_SHORT)
                    .show()
                false
            }

            name.any { it.isDigit() } -> {
                Toast.makeText(this, "O nome não pode conter números.", Toast.LENGTH_SHORT).show()
                false
            }

            // validação do email
            email.isEmpty() || !email.matches(emailPattern.toRegex()) -> {
                Toast.makeText(this, "Por favor, insira um email válido.", Toast.LENGTH_SHORT)
                    .show()
                false
            }

            // validação da password
            password.length < 8 -> {
                Toast.makeText(
                    this,
                    "A password deve ter pelo menos 8 caracteres.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            !password.any { it.isDigit() } -> {
                Toast.makeText(
                    this,
                    "A password deve conter pelo menos um número.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            !password.any { it.isUpperCase() } -> {
                Toast.makeText(
                    this,
                    "A password deve conter pelo menos uma letra maiúscula.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            !password.any { it.isLowerCase() } -> {
                Toast.makeText(
                    this,
                    "A password deve conter pelo menos uma letra minúscula.",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            else -> true
        }
    }
}